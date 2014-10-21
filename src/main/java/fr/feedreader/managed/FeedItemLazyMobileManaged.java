/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.managed;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.managed.lazy.FeedItemLazy;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author glopinous
 */
@ManagedBean
@ViewScoped
public class FeedItemLazyMobileManaged {
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    private Feed feed;
    private List<FeedItem> feedItems = null;
    private FeedItem feedItem = null;
    
    private Integer page = 1;
    private Long totalPage = null;
    
    private List<Feed> feeds = null;
    
    private Map<Feed, Long> feedsUnReadCounter;
    
    /**
     * La récupération du titre permet de déclanger le init()
     * @return Titre de la page
     */
    public String getTitle() {
        return "Visualisation des flux rss/atom";
    }
    
    @PostConstruct
    public void init() {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String feedIdRequest = requestParameterMap.get("feedId");
        if (feedIdRequest == null) {
            String feedItemIdRequest = requestParameterMap.get("feedItem");
            try {
                feedItem = feedItemBuisness.find(Integer.parseInt(feedItemIdRequest));
                feed = feedItem.getFeed();
            } catch (NumberFormatException e) {
                
            }
        } else {
            try {
                feed = feedBuisness.find(Integer.parseInt(feedIdRequest));
            } catch(NumberFormatException e) {
                
            }
        }
        
        String pageRequest = requestParameterMap.get("page");
        if (pageRequest != null) {
            page = Integer.parseInt(pageRequest);
            if (page > getTotalPage()) {
                page = getTotalPage().intValue();
            }
            if (page < 1) {
                page = 1;
            }
        }
    }
    
    public List<Feed> getFeeds() {
        if (feeds == null) {
            feeds = feedBuisness.findAll();
        }
        System.out.println("getFeeds");
        return feeds;
    }
    
    public Map<Feed, Long> getFeedsUnReadCounter() {
        if (feedsUnReadCounter == null) {
            feedsUnReadCounter = feedBuisness.countUnread();
        }
        return feedsUnReadCounter;
    }
    
    public List<FeedItem> getFeedItems() {
        if (feedItems == null) {
            feedItems = feedItemBuisness.findAll(feed.getId(), page);
        }
        return feedItems;
    }

    public Feed getFeed() {
        return feed;
    }

    public Integer getPage() {
        return page;
    }

    public Long getTotalPage() {
        if (totalPage == null) {
            totalPage = feedItemBuisness.getTotalPage(feed.getId());
        }
        return totalPage;
    }

    public void previousPage() {
        if (totalPage != null && page > 1) {
            page--;
            totalPage = null;
            feedItems = null;
        }
    }

    public void nextPage() {
        if (totalPage != null && page < totalPage) {
            page++;
            totalPage = null;
            feedItems = null;
        }
    }

    public String setFeed(Feed feed) {
        this.feed = feed;
        feedItems = null;
        page = 1;
        return "pm:feedItem";
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

    public String setFeedItem(FeedItem feedItem) {
        if (feedItem != null && feedItem.getReaded() == false) {
            feedItem = feedItemBuisness.setReaded(feedItem.getId(), Boolean.TRUE);
            feedItems = null;
            feedsUnReadCounter = null;
        }
        this.feedItem = feedItem;
        return "pm:feedItemAction";
    }
}
