package org.cyan.rssapi.service;

import java.util.List;

import org.cyan.rssapi.model.HotRss;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaRssRepository extends JpaRepository<HotRss, Integer> {
    @Query("SELECT hr FROM HotRss hr WHERE hr.rssId = ?1")
    List<HotRss> findTopRssTopicsById(String id, Sort sort);
}
