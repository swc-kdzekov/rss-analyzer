package org.cyan.rssapi.model;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class ElementInfo {
    private String word;
    private int frequency;
    private String title;
    private String reference;
}
