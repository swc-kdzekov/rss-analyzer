package org.cyan.rssapi.controller;

import java.io.IOException;
import java.util.List;

import com.sun.syndication.io.FeedException;
import org.cyan.rssapi.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rss")
public class RssController {

    @Autowired
    private RssService rssService;

    @GetMapping("/analyse/new")
    public String analyzeRssResources(@RequestParam String[] urls) throws FeedException, IOException {
        rssService.validateResource(urls);

        //Analyze urls logic
        rssService.analyzeRssFeeds(urls);
        return "Return unique identifier";
    }

    @GetMapping("/frequency")
    public List<String> getMostFrequent(@RequestParam String id) {

        //Return 3 most frequent items, json object
        return rssService.getMostFrequentTopics(id);
    }
}

