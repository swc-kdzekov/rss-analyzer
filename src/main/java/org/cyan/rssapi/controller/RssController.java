package org.cyan.rssapi.controller;

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
    public String analyzeRssResources(@RequestParam String[] urls){

        //test hybernate
        rssService.printDbData();
        //Analyze urls logic
        return "Return unique identifier";
    }

    @GetMapping("/frequency")
    public void getMostFrequent(@RequestParam String id){
        //Return 3 most frequent items, json object
    }
}

