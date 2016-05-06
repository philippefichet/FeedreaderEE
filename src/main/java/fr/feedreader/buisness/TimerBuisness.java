/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.util.List;
import java.util.Map;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

/**
 *
 * @author philippe
 */
@Stateless
public class TimerBuisness {
    
    @Inject
    protected FeedBuisness feedBuisness;
    
    @Schedule(hour = "*", minute = "*/15", persistent = false)
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void updateFeed() throws NotSupportedException, SystemException {
        Map<Feed, List<FeedItem>> newFeedItem = feedBuisness.parallelUpdateAllFeed();
//        Map<Feed, Long> countUnread = feedBuisness.countUnread();
//        UpdateFeed.notifyUpdateFeed(newFeedItem, countUnread);
    }

}
