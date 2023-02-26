package org.cyan.rssapi.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class ElementInfo {
    private String word;
    private int frequency;
    private List<String> titles;
    private List<String> references;
}
