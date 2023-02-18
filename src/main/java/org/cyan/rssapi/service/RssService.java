package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.validator.routines.UrlValidator;
import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.RssFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RssService {

    @Autowired
    private JpaRssRepository jpaRssRepository;

    public List<String> getMostFrequentTopics(String id){
        HotRss hrss = new HotRss("rss7", "element7", 88);
        jpaRssRepository.save(hrss);

        hrss = new HotRss("rss7", "element77", 56);
        jpaRssRepository.save(hrss);

        hrss = new HotRss("rss2", "element25", 2);
        jpaRssRepository.save(hrss);

        List<HotRss> rssList = jpaRssRepository.findTopRssTopicsById(id, Sort.by(Sort.Direction.DESC, "frequency"));
        return rssList.stream().map(el -> el.getElement()+": "+el.getFrequency()).collect(Collectors.toList());
    }

    public void analyzeRssFeeds(String[] urls) throws IOException, FeedException {
        List<RssFeed> listRssFeed = new ArrayList<>();
        for(String url:urls){
            RssFeed rssFeed = parseRssFeed(url);
            listRssFeed.add(rssFeed);
        }
        String test = "test";
    }

    private RssFeed parseRssFeed(String url) throws IOException, FeedException {
        URL feedSource = new URL(url);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        List<SyndEntry> entries = feed.getEntries();
        return new RssFeed(feed.getTitle(), feed.getLink(), feed.getDescription(), entries);
    }

    public void validateResource(String[] urls) throws MalformedURLException {

        if (urls.length < 2) throw new UrlsArgumentException("Please provide more RSS resources!");

        for (String url:urls){
            if (!isValidURL(url)){
                throw new UrlsArgumentException("Not valid Url resource: '"+url+"'!");
            }
        }
    }

    boolean isValidURL(String url) throws MalformedURLException {
        UrlValidator validator = new UrlValidator();
        return validator.isValid(url);
    }
}
