/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.feedreader.models.Feed;
import java.util.Date;
import java.util.function.Function;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author philippe
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class FeedWrapper {
    private Integer id;
    private String name;
    private String url;
    private String description;
    private Date lastUpdate;
    private Boolean enable = Boolean.TRUE;

    public FeedWrapper() {
        
    }
    
    public FeedWrapper(Feed feed) {
        description = feed.getDescription();
        enable = feed.isEnable();
        id = feed.getId();
        lastUpdate = feed.getLastUpdate();
        name = feed.getName();
        url = feed.getUrl();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public Boolean getEnable() {
        return enable;
    }

    public static FeedWrapper feedToFeedWrapper(Feed feed) {
        return new FeedWrapper(feed);
    }
    
    public Feed toFeed() {
        Feed feed = new Feed();
        feed.setDescription(getDescription());
        feed.setEnable(getEnable());
        feed.setLastUpdate(getLastUpdate());
        feed.setName(getName());
        feed.setUrl(getUrl());
        return feed;
    }
}
