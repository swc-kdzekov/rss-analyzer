package org.cyan.rssapi.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotRssResponse {
    @ApiModelProperty(notes = "Matching element")
    private String element;
    @ApiModelProperty(notes = "Frequency of the element")
    private Integer frequency;
    @ApiModelProperty(notes = "Element details")
    private List<HotRssRespDetail> details;
}
