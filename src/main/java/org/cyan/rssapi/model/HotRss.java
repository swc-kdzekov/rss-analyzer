package org.cyan.rssapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HotRss {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String rssId;

    private String element;

    private int frequency;


    private String title;


    @Column(length = 500)
    private String reference;

    public HotRss(String rssId, String element, int frequency, String title, String reference) {
        this.rssId = rssId;
        this.element = element;
        this.frequency = frequency;
        this.title = title;
        this.reference = reference;
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
