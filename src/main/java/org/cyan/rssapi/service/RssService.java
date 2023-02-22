package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.validator.routines.UrlValidator;
import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.ElementInfo;
import org.cyan.rssapi.model.HotRssDetails;
import org.cyan.rssapi.model.HotRssRespDetail;
import org.cyan.rssapi.model.HotRssResponse;
import org.cyan.rssapi.model.RssFeed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import weka.core.Stopwords;


@Service
public class RssService {

    private JpaRssRepository jpaRssRepository;
    private JpaRssDetailsRepository jpaRssDetailsRepository;

    public RssService(JpaRssRepository jpaRssRepository, JpaRssDetailsRepository jpaRssDetailsRepository){
        this.jpaRssRepository = jpaRssRepository;
        this.jpaRssDetailsRepository = jpaRssDetailsRepository;
    }

    private static final int NUMBER_OF_TOP_NEWS = 3;
    private static final String DELIMITER_REGEX = "[\\s,.\"\'’;:]+";
    private static final String WORD_REGEX = "[a-zA-Z]+";

    public List<HotRssResponse> getMostFrequentTopics(String id) {
        Pageable sortedByFrequency = PageRequest.of(0, NUMBER_OF_TOP_NEWS, Sort.by("frequency").descending());
        List<HotRss> rssList = jpaRssRepository.findTopRssTopicsById(id, sortedByFrequency);
        return rssList.stream()
                .map(el -> HotRssResponse.builder()
                        .element(el.getElement())
                        .frequency(el.getFrequency())
                        .details(getElementDetails(id, el.getElement()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<HotRssRespDetail> getElementDetails(String id, String element) {
        List<HotRssDetails> rssDetails = jpaRssDetailsRepository.findTopRssTopicDetails(id, element);
        return rssDetails.stream().map(rssD -> new HotRssRespDetail(rssD.getTitle(), rssD.getReference())).collect(Collectors.toList());
    }

    public String analyzeRssFeeds(String[] urls) throws IOException, FeedException {

        Map<String, ElementInfo> matchedElements = new HashMap<>();

        RssFeed[] arrayRssFeed = new RssFeed[urls.length];
        int index = 0;
        for (String url : urls) {
            SyndFeed feed = getFeedFromUrlResource(url);
            RssFeed rssFeed = parseRssFeed(feed);
            arrayRssFeed[index] = rssFeed;
            index++;
        }

        for (int i = 0; i < arrayRssFeed.length - 1; i++) {
            findMatchingElements(arrayRssFeed[i], arrayRssFeed, i + 1, matchedElements);
        }

        return storeMatchingElements(matchedElements);
    }

    private SyndFeed getFeedFromUrlResource(String url) throws FeedException, IOException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        return feed;
    }

    private String storeMatchingElements(Map<String, ElementInfo> matchedElements) {
        UUID uuid = UUID.randomUUID();

        matchedElements.entrySet().forEach(me -> {
            HotRss hotRss = new HotRss(uuid.toString(), me.getKey(), me.getValue().getFrequency());
            jpaRssRepository.save(hotRss);
            storeMatchingElementsDetails(
                    uuid.toString(),
                    me.getKey(),
                    me.getValue().getTitles(),
                    me.getValue().getReferences()
            );
        });

        return uuid.toString();
    }

    private void storeMatchingElementsDetails(String rssId, String element, Set<String> titles, Set<String> links) {

        Iterator<String> titleIterator = titles.iterator();
        Iterator<String> linkIterator = links.iterator();
        List<HotRssDetails> listHotRssDetails = new ArrayList<>();

        while (titleIterator.hasNext() && linkIterator.hasNext()) {
            HotRssDetails hotRssDetails = new HotRssDetails(rssId, element, titleIterator.next(), linkIterator.next());
            listHotRssDetails.add(hotRssDetails);
        }
        jpaRssDetailsRepository.saveAll(listHotRssDetails);
    }

    private void findMatchingElements(
            RssFeed rssFeedPrev,
            RssFeed[] arrayRssFeed,
            int index,
            Map<String, ElementInfo> matchedElements
    ) {
        for (String kWord : rssFeedPrev.getKeyWordToInfo().keySet()) {
            int frequency = rssFeedPrev.getKeyWordToInfo().get(kWord).getFrequency();
            if (matchedElements.get(kWord) != null) {
                continue;
            }
            for (int j = index; j < arrayRssFeed.length; j++) {
                RssFeed rssFeedNext = arrayRssFeed[j];
                if (rssFeedNext.getKeyWordToInfo().keySet().contains(kWord)) {
                    if (matchedElements.get(kWord) != null) {
                        frequency = matchedElements.get(kWord).getFrequency();
                    }
                    frequency += rssFeedNext.getKeyWordToInfo().get(kWord).getFrequency();
                    matchedElements.put(kWord, ElementInfo.builder().word(kWord).frequency(frequency).build());
                }
            }
            matchedElements.put(kWord, ElementInfo.builder()
                    .frequency(frequency)
                    .titles(rssFeedPrev.getKeyWordToInfo().get(kWord).getTitles())
                    .references(rssFeedPrev.getKeyWordToInfo().get(kWord).getReferences())
                    .build());
        }
    }

    protected RssFeed parseRssFeed(SyndFeed feed) throws IOException, FeedException {
        Map<String, ElementInfo> keyWordsToInfo = parseKeyWords(feed.getEntries());

        return RssFeed.builder()
                .title(feed.getTitle())
                .link(feed.getLink())
                .description(feed.getDescription())
                .keyWordToInfo(keyWordsToInfo)
                .build();
    }

    private Map<String, ElementInfo> parseKeyWords(List<SyndEntry> entries) {

        Map<String, ElementInfo> kWordElementInfo = new HashMap<>();
        entries.forEach(entry -> {
            String title = entry.getTitle();
            String link = entry.getLink();
            String[] tWords = title.split(DELIMITER_REGEX);
            Arrays.stream(tWords).forEach(word -> {
                if ((word.matches(WORD_REGEX)) && !Stopwords.isStopword(word)) {
                    if (kWordElementInfo.get(word.toLowerCase()) != null) {

                        Set<String> titles = kWordElementInfo.get(word.toLowerCase()).getTitles();
                        titles.add(title);
                        Set<String> references = kWordElementInfo.get(word.toLowerCase()).getReferences();
                        references.add(link);

                        int freq = kWordElementInfo.get(word.toLowerCase()).getFrequency();
                        kWordElementInfo.put(
                                word.toLowerCase(),
                                ElementInfo.builder().frequency(freq + 1).titles(titles).references(references).build()
                        );
                    } else {
                        kWordElementInfo.put(
                                word.toLowerCase(),
                                ElementInfo.builder().frequency(1).titles(new HashSet<>(Arrays.asList(title)))
                                        .references(new HashSet<>(Arrays.asList(link))).build()
                        );
                    }
                }
            });
        });
        return kWordElementInfo;
    }

    public void validateResource(String[] urls) {

        if (urls.length < 2) {
            throw new UrlsArgumentException("Please provide more RSS resources!");
        }

        for (String url : urls) {
            if (!isValidURL(url)) {
                throw new UrlsArgumentException("Not valid Url resource: '" + url + "'!");
            }
        }

        for (String url : urls) {
            if (!isValidRssResource(url)) {
                throw new UrlsArgumentException("The provided Url resource: '" + url + "' is not valid RssFeed!");
            }
        }
    }

    private boolean isValidURL(String url) {
        UrlValidator validator = new UrlValidator();
        return validator.isValid(url);
    }

    private boolean isValidRssResource(String feedUrl) {
        SyndFeedInput input = new SyndFeedInput();
        try {
            input.build(new XmlReader(new URL(feedUrl)));
            return true;
        } catch (IOException | FeedException e) {
            return false;
        }
    }
}
