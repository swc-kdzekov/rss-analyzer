package org.cyan.rssapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.cyan.rssapi.controller", "org.cyan.rssapi.service", "org.cyan.rssapi.config"})
public class RssapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssapiApplication.class, args);
    }
}
