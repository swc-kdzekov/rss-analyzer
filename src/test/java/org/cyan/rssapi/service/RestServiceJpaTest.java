package org.cyan.rssapi.service;

import java.util.List;

import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.HotRssDetails;
import org.cyan.rssapi.model.HotRssRespDetail;
import org.cyan.rssapi.model.HotRssResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RestServiceJpaTest {

    @Autowired
    private JpaRssRepository jpaRssRepository;
    @Autowired
    private JpaRssDetailsRepository jpaRssDetailsRepository;
    @Autowired
    private RssService rssService;

    @BeforeEach
    void init() {
        insertData();
    }

    @Test
    void testMostFrequentTopics() {
        List<HotRssResponse> listHotRss = rssService.getMostFrequentTopics("0bc77ffd-77d1-46b0-9d85-636e5799877f");
        Assertions.assertEquals(3, listHotRss.size());
        Assertions.assertEquals("biden", listHotRss.get(0).getElement());
        Assertions.assertEquals("ukraine", listHotRss.get(1).getElement());
        Assertions.assertEquals("nato", listHotRss.get(2).getElement());
    }

    @Test
    void testMostFrequentTopicDetails() {
        List<HotRssResponse> listHotRss = rssService.getMostFrequentTopics("0bc77ffd-77d1-46b0-9d85-636e5799877f");
        Assertions.assertEquals(2, listHotRss.get(0).getDetails().size());
        Assertions.assertTrue(listHotRss.get(0).getDetails().contains(new HotRssRespDetail("biden-title1", "biden-reference1")));
        Assertions.assertTrue(listHotRss.get(0).getDetails().contains(new HotRssRespDetail("biden-title2", "biden-reference2")));
        Assertions.assertFalse(listHotRss.get(0).getDetails().contains(new HotRssRespDetail("biden-title3", "biden-reference3")));
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

    @AfterEach
    void clear() {
        jpaRssRepository.deleteAll();
        jpaRssDetailsRepository.deleteAll();
    }
}
