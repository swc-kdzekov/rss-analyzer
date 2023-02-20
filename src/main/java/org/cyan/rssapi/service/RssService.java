package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.cyan.rssapi.model.MatchedElementInfo;
import org.cyan.rssapi.model.RssFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import weka.core.Stopwords;

//TODO
//Write tests for the class
@Service
public class RssService {

    @Autowired
    private JpaRssRepository jpaRssRepository;

    private static final int NUMBER_OF_TOP_NEWS = 3;
    private static final String DELIMITER_REGEX = "[\\s,.\"\']+";
    private static final String WORD_REGEX = "[a-zA-Z]+";

    public List<String> getMostFrequentTopics(String id) {
        Pageable sortedByFrequency = PageRequest.of(0, NUMBER_OF_TOP_NEWS, Sort.by("frequency").descending());
        List<HotRss> rssList = jpaRssRepository.findTopRssTopicsById(id, sortedByFrequency);
        return rssList.stream().map(el -> el.getElement() + ": " + el.getFrequency()).collect(Collectors.toList());
    }

    //TODO
    //Code refactor
    public String analyzeRssFeeds(String[] urls) throws IOException, FeedException {

        Map<String, MatchedElementInfo> matchedElements = new HashMap<>();

        RssFeed[] arrayRssFeed = new RssFeed[urls.length];
        int index = 0;
        for (String url : urls) {
            RssFeed rssFeed = parseRssFeed(url);
            arrayRssFeed[index] = rssFeed;
            index++;
        }

        for (int i = 0; i < arrayRssFeed.length - 1; i++) {
            findMatchingElements(arrayRssFeed[i], arrayRssFeed, i + 1, matchedElements);
        }

        return storeMatchingElements(matchedElements);
    }

    private String storeMatchingElements(Map<String, MatchedElementInfo> matchedElements) {
        UUID uuid = UUID.randomUUID();

        matchedElements.entrySet().forEach(me -> {
            HotRss hotRss = new HotRss(uuid.toString(), me.getKey(), me.getValue().getFrequency());
            jpaRssRepository.save(hotRss);
        });

        return uuid.toString();
    }

    private void findMatchingElements(
            RssFeed rssFeedPrev,
            RssFeed[] arrayRssFeed,
            int index,
            Map<String, MatchedElementInfo> matchedElements
    ) {
        for (String kWord : rssFeedPrev.getKeyWordFrequency().keySet()) {
            int frequency = rssFeedPrev.getKeyWordFrequency().get(kWord);
            if (matchedElements.get(kWord) != null) {
                continue;
            }
            for (int j = index; j < arrayRssFeed.length; j++) {
                RssFeed rssFeedNext = arrayRssFeed[j];
                if (rssFeedNext.getKeyWordFrequency().keySet().contains(kWord)) {
                    if (matchedElements.get(kWord) != null) {
                        frequency = matchedElements.get(kWord).getFrequency();
                    }
                    frequency += rssFeedNext.getKeyWordFrequency().get(kWord);
                    matchedElements.put(kWord, MatchedElementInfo.builder().word(kWord).frequency(frequency).build());
                }
            }
        }
    }

    private RssFeed parseRssFeed(String url) throws IOException, FeedException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        Map<String, Integer> keyWordsFreq = parseKeyWords(feed.getEntries());

        return RssFeed.builder()
                .title(feed.getTitle())
                .link(feed.getLink())
                .description(feed.getDescription())
                .keyWordFrequency(keyWordsFreq)
                .build();
    }

    private Map<String, Integer> parseKeyWords(List<SyndEntry> entries) {

        Map<String, Integer> kWordFrequency = new HashMap<>();
        entries.forEach(entry -> {
            String title = entry.getTitle();
            String[] tWords = title.split(DELIMITER_REGEX);
            Arrays.stream(tWords).forEach(word -> {
                if ((word.matches(WORD_REGEX)) && !Stopwords.isStopword(word)) {
                    if (kWordFrequency.get(word.toLowerCase()) != null) {
                        int freq = kWordFrequency.get(word.toLowerCase());
                        kWordFrequency.put(word.toLowerCase(), freq + 1);
                    } else {
                        kWordFrequency.put(word.toLowerCase(), 1);
                    }
                }
            });
        });
        return kWordFrequency;
    }

    public void validateResource(String[] urls) throws MalformedURLException {

        if (urls.length < 2) {
            throw new UrlsArgumentException("Please provide more RSS resources!");
        }

        for (String url : urls) {
            if (!isValidURL(url)) {
                throw new UrlsArgumentException("Not valid Url resource: '" + url + "'!");
            }
        }
    }

    boolean isValidURL(String url) throws MalformedURLException {
        UrlValidator validator = new UrlValidator();
        return validator.isValid(url);
    }
}
