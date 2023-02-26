package org.cyan.rssapi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class HotRss {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Auto generated id number")
    private Long id;

    @ApiModelProperty(notes = "Unique identifier for the analysis")
    private String rssId;

    @ApiModelProperty(notes = "Matching element")
    private String element;

    @ApiModelProperty(notes = "Frequency of the element")
    private int frequency;


    public HotRss(String rssId, String element, int frequency) {
        this.rssId = rssId;
        this.element = element;
        this.frequency = frequency;
    }

    public HotRss() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRssId() {
        return rssId;
    }

    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}
