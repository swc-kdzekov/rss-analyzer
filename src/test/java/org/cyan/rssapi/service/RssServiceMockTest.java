package org.cyan.rssapi.service;

import java.io.IOException;
import java.io.InputStream;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.cyan.rssapi.configuraton.TestConfiguration;
import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.cyan.rssapi.model.RssFeed;
import org.cyan.rssapi.util.RssParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
class RssServiceMockTest {

    @Mock
    JpaRssRepository jpaRssRepository;

    @Mock
    JpaRssDetailsRepository jpaRssDetailsRepository;

    @Mock
    FeedProvider feedProvider;
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

        Assertions.assertThrows(UrlsArgumentException.class, () -> {
            String[] testurls = new String[2];
            testurls[0] = "https://www.theguardian.com/us-news/rss";
            testurls[1] = "http://localhost:8787";
            rssService.validateResource(testurls);
        });

    }

    @Test
    void testParseRssFeed() throws FeedException, IOException {
        SyndFeed feed = getTestFeed();
        RssFeed rssFeed = RssParser.parseRssFeed(feed);

        String rssTitle = "The Christian Science Monitor | USA";
        Assertions.assertEquals(rssTitle, rssFeed.getTitle());

        String rssLink="https://www.csmonitor.com";
        Assertions.assertEquals(rssLink, rssFeed.getLink());

        Assertions.assertEquals(2, rssFeed.getKeyWordToInfo().get("trump").getFrequency());
        Assertions.assertEquals(2, rssFeed.getKeyWordToInfo().get("china").getFrequency());
        Assertions.assertEquals(1, rssFeed.getKeyWordToInfo().get("ufos").getFrequency());

        String title = "Peace through strength? US rattles China with new defenses near Taiwan.";
        Assertions.assertTrue(rssFeed.getKeyWordToInfo().get("china").getTitles().contains(title));
    }

    private SyndFeed getTestFeed() throws IOException, FeedException {
        InputStream resource = new ClassPathResource("test-feed.xml").getInputStream();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(resource));
        return feed;
    }
}