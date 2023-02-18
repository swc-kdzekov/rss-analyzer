package org.cyan.rssapi.service;

import org.cyan.rssapi.model.HotRss;
import org.springframework.data.repository.CrudRepository;

public interface RssRepository extends CrudRepository<HotRss, Integer> {
}
