package org.cyan.rssapi.service;

import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.io.InputStream;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.HotRssDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BaseJpaTest {

    @Autowired
    protected JpaRssRepository jpaRssRepository;
    @Autowired
    protected JpaRssDetailsRepository jpaRssDetailsRepository;
    @Mock
    protected FeedProvider feedProvider;

    protected String testUrl1 = "https://www.rsshost1.com/test1/rss";

    protected String testUrl2 = "https://www.rsshost2.com/test2/rss";


    @BeforeEach
    void init() throws FeedException, IOException {
        insertData();

        doReturn(getTestFeed("test-match-feed1.xml")).when(feedProvider).getFeedFromUrlResource(testUrl1);

        doReturn(getTestFeed("test-match-feed2.xml")).when(feedProvider).getFeedFromUrlResource(testUrl2);
    }

    @AfterEach
    void clear() {
        jpaRssRepository.deleteAll();
        jpaRssDetailsRepository.deleteAll();
    }

    private void insertData() {
        HotRss hotRss = new HotRss("0bc77ffd-77d1-46b0-9d85-636e5799877f", "ukraine", 5);
        jpaRssRepository.save(hotRss);

        hotRss = new HotRss("0bc77ffd-77d1-46b0-9d85-636e5799877f", "biden", 9);
        jpaRssRepository.save(hotRss);

        hotRss = new HotRss("0bc77ffd-77d1-46b0-9d85-636e5799877f", "nato", 3);
        jpaRssRepository.save(hotRss);

        hotRss = new HotRss("0bc77ffd-77d1-46b0-9d85-636e5799877f", "aple", 1);
        jpaRssRepository.save(hotRss);

        hotRss = new HotRss("0bc77ffd-77d1-46b0-9d85-636e5799877f", "orange", 1);
        jpaRssRepository.save(hotRss);


        hotRss = new HotRss("00f92c74-1bb6-497b-bbe2-967ec16ae160", "biden", 2);
        jpaRssRepository.save(hotRss);

        hotRss = new HotRss("00f92c74-1bb6-497b-bbe2-967ec16ae160", "ukraine", 4);
        jpaRssRepository.save(hotRss);


        //----details-------

        HotRssDetails hotRssDetails = new HotRssDetails(
                "0bc77ffd-77d1-46b0-9d85-636e5799877f",
                "biden",
                "biden-title1",
                "biden-reference1"
        );
        jpaRssDetailsRepository.save(hotRssDetails);

        hotRssDetails = new HotRssDetails(
                "0bc77ffd-77d1-46b0-9d85-636e5799877f",
                "biden",
                "biden-title2",
                "biden-reference2"
        );
        jpaRssDetailsRepository.save(hotRssDetails);

        hotRssDetails = new HotRssDetails(
                "00f92c74-1bb6-497b-bbe2-967ec16ae160",
                "biden",
                "biden-title3",
                "biden-reference3"
        );
        jpaRssDetailsRepository.save(hotRssDetails);
    }

    private SyndFeed getTestFeed(String rssFileName) throws IOException, FeedException {
        InputStream resource = new ClassPathResource(rssFileName).getInputStream();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(resource));
        return feed;
    }
}
