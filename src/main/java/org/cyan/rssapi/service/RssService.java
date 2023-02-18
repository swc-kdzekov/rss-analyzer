package org.cyan.rssapi.service;

import org.cyan.rssapi.model.HotRss;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RssService {

    @Autowired
    private RssRepository rssRepository;

    public void printDbData(){
        HotRss hrss = new HotRss("rss7", "element7", 88);
        rssRepository.save(hrss);
        rssRepository.findAll().forEach(rss -> System.out.println(rss.getElement()));
    }
}
