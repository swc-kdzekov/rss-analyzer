package org.cyan.rssapi.configuraton;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = "classpath:test-feed.xml")
public class TestConfiguration {

}
