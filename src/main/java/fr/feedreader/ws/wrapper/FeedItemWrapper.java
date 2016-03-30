/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

import fr.feedreader.models.FeedItem;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author philippe
 */
@XmlRootElement
public class FeedItemWrapper {
    private Integer id;
    private String feedItemId;
    private String title;
    private String link;
    private String enclosure;
    private String summary;
    private Long updated;
    private Long feedId;
    private Boolean readed = null;
    private FeedItemUrlWrapper url = null;
    private FeedWrapper feed = null;

    public FeedItemWrapper(FeedItem feedItem, FeedItemUrlWrapper feedItemUrlWrapper) {
        this(feedItem, feedItemUrlWrapper, false);
    }
    
    public FeedItemWrapper(FeedItem feedItem, FeedItemUrlWrapper feedItemUrlWrapper, boolean summary) {
        enclosure = feedItem.getEnclosure();
        feedItemId = feedItem.getFeedItemId();
        id = feedItem.getId();
        link = feedItem.getLink();
        readed = feedItem.getReaded();
        title = feedItem.getTitle();
        if (summary) {
            this.summary = feedItem.getSummary();
        }
        if (feedItem.getUpdated() != null) {
            updated = feedItem.getUpdated().getTime();
        }
        url = feedItemUrlWrapper;
    }
    
    public Integer getId() {
        return id;
    }

    public String getFeedItemId() {
        return feedItemId;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getSummary() {
        return summary;
    }

    public Long getUpdated() {
        return updated;
    }

    public Long getFeedId() {
        return feedId;
    }

    public Boolean getReaded() {
        return readed;
    }

    public FeedItemUrlWrapper getUrl() {
        return url;
    }

    public FeedWrapper getFeed() {
        return feed;
    }

    public void setFeed(FeedWrapper feed) {
        this.feed = feed;
    }
}
