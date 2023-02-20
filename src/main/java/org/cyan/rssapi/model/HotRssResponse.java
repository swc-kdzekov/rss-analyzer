package org.cyan.rssapi.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotRssResponse {
    private String element;
    private Integer frequency;
    private String title;
    private String reference;
}
