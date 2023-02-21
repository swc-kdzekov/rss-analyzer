package org.cyan.rssapi.model;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class ElementInfo {
    private String word;
    private int frequency;
    private Set<String> titles;
    private Set<String> references;
}
