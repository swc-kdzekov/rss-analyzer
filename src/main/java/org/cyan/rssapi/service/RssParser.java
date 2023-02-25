package org.cyan.rssapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.cyan.rssapi.model.ElementInfo;
import org.cyan.rssapi.model.RssFeed;
import weka.core.Stopwords;

public class RssParser {

    private static final String DELIMITER_REGEX = "[\\s,.\"\'’;:]+";
    private static final String WORD_REGEX = "[a-zA-Z]+";

    public static RssFeed parseRssFeed(SyndFeed feed) {
        Map<String, ElementInfo> keyWordsToInfo = parseKeyWords(feed.getEntries());

        return RssFeed.builder()
                .title(feed.getTitle())
                .link(feed.getLink())
                .description(feed.getDescription())
                .keyWordToInfo(keyWordsToInfo)
                .build();
    }

    private static Map<String, ElementInfo> parseKeyWords(List<SyndEntry> entries) {

        Map<String, ElementInfo> kWordElementInfo = new HashMap<>();
        entries.forEach(entry -> {
            String title = entry.getTitle();
            String link = entry.getLink();
            String[] tWords = title.split(DELIMITER_REGEX);
            Arrays.stream(tWords).forEach(word -> {
                if ((word.matches(WORD_REGEX)) && !Stopwords.isStopword(word)) {
                    if (kWordElementInfo.get(word.toLowerCase()) != null) {

                        List<String> titles = new ArrayList<>(kWordElementInfo.get(word.toLowerCase()).getTitles());
                        titles.add(title);
                        List<String> references = new ArrayList<>(kWordElementInfo.get(word.toLowerCase())
                                .getReferences());
                        references.add(link);

                        int freq = kWordElementInfo.get(word.toLowerCase()).getFrequency();
                        kWordElementInfo.put(
                                word.toLowerCase(),
                                ElementInfo.builder().frequency(freq + 1).titles(titles).references(references).build()
                        );
                    } else {
                        kWordElementInfo.put(
                                word.toLowerCase(),
                                ElementInfo.builder().frequency(1).titles(Arrays.asList(title))
                                        .references(Arrays.asList(link)).build()
                        );
                    }
                }
            });
        });
        return kWordElementInfo;
    }
}
