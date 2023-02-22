package org.cyan.rssapi.service;

import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.HotRssDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BaseJpaTest {

    @Autowired
    private JpaRssRepository jpaRssRepository;
    @Autowired
    private JpaRssDetailsRepository jpaRssDetailsRepository;


    @BeforeEach
    void init() {
       insertData();
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
}
