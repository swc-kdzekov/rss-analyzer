package org.cyan.rssapi.callable;

import java.util.concurrent.Callable;

import com.sun.syndication.feed.synd.SyndFeed;
import org.cyan.rssapi.model.RssFeed;
import org.cyan.rssapi.util.RssParser;

public class ParseFeedCallable implements Callable<RssFeed> {

    private SyndFeed feed;

    public ParseFeedCallable(SyndFeed feed) {
        this.feed = feed;
    }

    @Override
    public RssFeed call() {
        RssFeed rssFeed = RssParser.parseRssFeed(feed);
        return rssFeed;
    }

}
