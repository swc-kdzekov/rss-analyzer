package org.cyan.rssapi.model;

import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class RssFeed {

    private String title;
    private String link;
    private String description;

    private List<SyndEntry> entries;
}
