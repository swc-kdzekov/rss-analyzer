package org.cyan.rssapi.service;

import java.util.List;

import org.cyan.rssapi.model.HotRssDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRssDetailsRepository extends JpaRepository<HotRssDetails, Integer> {

    @Query("SELECT hr FROM HotRssDetails hr WHERE hr.rssId = ?1 AND hr.element = ?2 ")
    List<HotRssDetails> findTopRssTopicDetails(String id, String element);
}
