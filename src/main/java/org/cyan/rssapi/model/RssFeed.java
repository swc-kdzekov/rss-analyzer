package org.cyan.rssapi.model;

import java.util.List;
import java.util.Map;

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
    private Map<String, ElementInfo> keyWordToInfo;
}
