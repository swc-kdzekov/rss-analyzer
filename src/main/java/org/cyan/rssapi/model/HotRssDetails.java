package org.cyan.rssapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class HotRssDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "Auto generated id number")
    private Long id;

    public HotRssDetails(String rssId, String element, String title, String reference){
        this.rssId = rssId;
        this.element = element;
        this.title = title;
        this.reference = reference;
    }

    public HotRssDetails(){

    }

    @ApiModelProperty(notes = "Unique identifier for the analysis")
    private String rssId;

    @ApiModelProperty(notes = "Matching element")
    private String element;

    @ApiModelProperty(notes = "Title of the article")
    private String title;

    @Column(length = 500)
    @ApiModelProperty(notes = "Reference to the article")
    private String reference;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
