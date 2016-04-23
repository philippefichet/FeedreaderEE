/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author philippefichet
 */
@XmlRootElement
public class FeedUpdateWrapper {
    private String type = "feeds";
    private List<FeedWrapper> feeds = null;
    private List<FeedItemWrapper> feedItems = null;

    public FeedUpdateWrapper(Map<Feed, List<FeedItem>> newFeedItem, Map<Feed, Long> countUnread) {
        feeds = new ArrayList<>();
        feedItems = new ArrayList<>();
        newFeedItem.forEach((Feed feed, List<FeedItem> fis) -> {
            FeedWrapper feedWrapper = new FeedWrapper(feed);
            feedWrapper.setUnread(countUnread.get(feed));
            feedItems.addAll(fis.stream().map((feedItem) -> {
                return new FeedItemWrapper(feedItem, new FeedItemUrlWrapper("/feedreader", feedItem));
            }).collect(Collectors.toList()));
            feeds.add(feedWrapper);
        });
    }

    public List<FeedWrapper> getFeeds() {
        return feeds;
    }

    public String getType() {
        return type;
    }

    public List<FeedItemWrapper> getFeedItems() {
        return feedItems;
    }

}
