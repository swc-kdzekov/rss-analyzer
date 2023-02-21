package org.cyan.rssapi.service;

import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RssServiceTest {

    @Mock
    JpaRssRepository jpaRssRepository;

    @Mock
    JpaRssDetailsRepository jpaRssDetailsRepository;
    @InjectMocks
    RssService rssService;

    @Test
    void testValidation(){

        Assertions.assertThrows(UrlsArgumentException.class, () -> {
            String[] testurls = new String[1];
            testurls[0] = "https://www.theguardian.com/us-news/rss";
            rssService.validateResource(testurls);
        });

        Assertions.assertThrows(UrlsArgumentException.class, () -> {
            String[] testurls = new String[2];
            testurls[0] = "https://www.theguardian.com/us-news/rss";
            testurls[1] = "invalid_url";
            rssService.validateResource(testurls);
        });

    }
}