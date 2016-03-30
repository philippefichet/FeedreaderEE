/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author philippe
 */
@XmlRootElement
public class FeedItemResponseWrapper {
    private List<FeedItemWrapper> feedItems;
    private FeedWrapper feed;
    private Long pages;

    public FeedItemResponseWrapper(List<FeedItemWrapper> feedItems, Long pages, FeedWrapper feedWrapper) {
        this.feedItems = feedItems;
        this.pages = pages;
        this.feed = feedWrapper;
    }

    public List<FeedItemWrapper> getFeedItems() {
        return feedItems;
    }

    public Long getPages() {
        return pages;
    }

    public FeedWrapper getFeed() {
        return feed;
    }
}
