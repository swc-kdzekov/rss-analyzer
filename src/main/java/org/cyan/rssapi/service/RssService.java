package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import org.cyan.rssapi.model.MatchedElementInfo;
import org.cyan.rssapi.model.RssFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

        Map<String, Integer> wordFrequency = new HashMap<>();
        for (int j = index; j < arrayRssFeed.length; j++) {
            RssFeed rssFeedNext = arrayRssFeed[j];
            for (String kWord : rssFeedPrev.getKeyWords()) {
                if (rssFeedNext.getKeyWords().contains(kWord) && !matchedElements.keySet().contains(kWord)) {
                    //TODO
                    //Wait for the answer (or think?) if the frequency needs to be calculated by the appearance in the entries separately
                    int frequency = 1;
                    if (wordFrequency.get(kWord) != null) {
                        frequency = wordFrequency.get(kWord);
                    }
                    wordFrequency.put(kWord, frequency + 1);
                }
            }
        }

        //TODO
        //Implement logic for: find the entry(item) in 'rssFeedPrev' where the news appears and get the details: title, link, description(?)
        wordFrequency.entrySet().forEach(el -> {
            matchedElements.put(el.getKey(), new MatchedElementInfo(el.getKey(), el.getValue()));
        });
    }

    private RssFeed parseRssFeed(String url) throws IOException, FeedException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        Set<String> keyWords = parseKeyWords(feed.getEntries());
        return RssFeed.builder()
                .title(feed.getTitle())
                .link(feed.getLink())
                .description(feed.getDescription())
                .keyWords(keyWords)
                .build();
    }

    private Set<String> parseKeyWords(List<SyndEntry> entries) {
        Set<String> kWords = new HashSet<>();
        entries.forEach(entry -> {

            String title = entry.getTitle();
            String[] tWords = title.split(DELIMITER_REGEX);
            Arrays.stream(tWords).forEach(word -> {
                if ((word.matches(WORD_REGEX)) && word.length() > 3) {
                    kWords.add(word.toLowerCase());
                }
            });
        });
        return kWords;
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
