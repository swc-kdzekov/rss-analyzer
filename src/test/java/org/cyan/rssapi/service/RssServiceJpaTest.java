package org.cyan.rssapi.service;

import java.util.List;

import org.cyan.rssapi.model.HotRssRespDetail;
import org.cyan.rssapi.model.HotRssResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RssServiceJpaTest extends BaseJpaTest {

    @Autowired
    private RssService rssService;

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

}
