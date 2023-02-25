package org.cyan.rssapi.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.sun.syndication.io.FeedException;
import org.cyan.rssapi.model.HotRssResponse;
import org.cyan.rssapi.service.RssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rss")
public class RssController {

    @Autowired
    private RssService rssService;

    @PostMapping("/analyse/new")
    public String analyzeRssResources(@RequestParam String[] urls)
            throws FeedException, IOException, ExecutionException, InterruptedException {
        rssService.validateResource(urls);
        return rssService.analyzeRssFeeds(urls);
    }

    @GetMapping("/frequency")
    public List<HotRssResponse> getMostFrequent(@RequestParam String id) {
        //Return 3 most frequent items, json object
        return rssService.getMostFrequentTopics(id);
    }
}

