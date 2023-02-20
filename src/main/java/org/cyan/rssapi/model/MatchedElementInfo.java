package org.cyan.rssapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class MatchedElementInfo {
    private String word;
    private int frequency;
    // plus info about the news where the matching word appears: title, link, description (private string ...)
}
