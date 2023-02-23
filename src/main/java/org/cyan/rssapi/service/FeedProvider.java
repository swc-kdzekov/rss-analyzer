package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.URL;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.springframework.stereotype.Component;

@Component
public class FeedProvider {
    public SyndFeed getFeedFromUrlResource(String url) throws FeedException, IOException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        return input.build(new XmlReader(feedSource));
    }
}
