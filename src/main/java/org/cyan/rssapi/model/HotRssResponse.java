package org.cyan.rssapi.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotRssResponse {
    private String element;
    private Integer frequency;
    private List<HotRssRespDetail> details;
}
