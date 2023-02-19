package org.cyan.rssapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class MatchedElementInfo {
    private String word;
    private int frequency;
    // plus info about the news where the matching word appears: title, link, description (private string ...)
}
