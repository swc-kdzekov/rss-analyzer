package org.cyan.rssapi.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.syndication.feed.synd.SyndEntry;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class RssFeed {

    private String title;
    private String link;
    private String description;

    private List<SyndEntry> entries;
    private Set<String> keyWords;
    private Map<String, Integer> keyWordFrequency;
}
