package org.cyan.rssapi.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor @Getter @EqualsAndHashCode
public class HotRssRespDetail {
    @ApiModelProperty(notes = "Title of the item")
    private String title;
    @ApiModelProperty(notes = "Reference of the item")
    private String reference;
}
