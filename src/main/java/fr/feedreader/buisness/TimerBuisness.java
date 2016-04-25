/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import fr.feedreader.websocket.UpdateFeed;
import java.util.List;
import java.util.Map;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author philippe
 */
@Stateless
public class TimerBuisness {
    
    @Inject
    protected FeedBuisness feedBuisness;
    
    @Schedule(hour = "*", minute = "*/15", persistent = false)
    public void updateFeed() {
        Map<Feed, List<FeedItem>> newFeedItem = feedBuisness.parallelUpdateAllFeed();
//        Map<Feed, Long> countUnread = feedBuisness.countUnread();
//        UpdateFeed.notifyUpdateFeed(newFeedItem, countUnread);
    }

}
