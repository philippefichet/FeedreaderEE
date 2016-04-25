/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import fr.feedreader.websocket.UpdateFeed;
import fr.feedreader.ws.wrapper.FeedItemUrlWrapper;
import fr.feedreader.ws.wrapper.FeedItemWrapper;
import fr.feedreader.ws.wrapper.FeedWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author philippe
 */
@Path("/feedItem")
@Consumes(value = MediaType.APPLICATION_JSON)
@Produces(value = MediaType.APPLICATION_JSON)
public class FeedItemFacadeREST {
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Context
    private HttpServletRequest request;

    @GET
    @Path("{id}")
    public FeedItemWrapper getFeedItem(@PathParam("id") Integer feedItemId) {
        FeedItem feedItem = feedItemBuisness.find(feedItemId);
        FeedWrapper feedWrapper = new FeedWrapper(feedItem.getFeed());
        feedWrapper.setUnread(feedBuisness.countUnread(feedItem.getFeed().getId()));
        FeedItemWrapper feedItemWrapper = new FeedItemWrapper(feedItem, new FeedItemUrlWrapper(request.getContextPath(), feedItem), true);
        feedItemWrapper.setFeed(feedWrapper);
        return feedItemWrapper;
    }
    
    @POST
    @Path("{id}/readed/{readed}")
    public FeedItemWrapper setReadedFeedItem(@PathParam("id") Integer feedItemId, @PathParam("readed") Boolean readed) {
        FeedItem feedItem = feedItemBuisness.find(feedItemId);
        feedItem.setReaded(readed);
        feedItem = feedItemBuisness.update(feedItem);
        
        // TODO faire ceci en @Async
        // Envoi des flux avec leur compteurs via WebSocket
//        Map<Integer, Long> countUnread = feedBuisness.countUnread();
        Map<Feed, List<FeedItem>> update = new HashMap<>();
        feedBuisness.findAll().stream().forEach((feed) -> {
            update.put(feed, new ArrayList<>());
        });
        update.put(feedItem.getFeed(), Arrays.asList(feedItem));
//        UpdateFeed.notifyUpdateFeed(update, countUnread);
        
        // Retour du webservice
        FeedItemWrapper feedItemWrapper = new FeedItemWrapper(
            feedItem,
            new FeedItemUrlWrapper(request.getContextPath(), feedItem),
            true
        );
        
        // Nombre de nouvelles non lues
        FeedWrapper feedWrapper = new FeedWrapper(feedItem.getFeed());
        feedWrapper.setUnread(feedBuisness.countUnread(feedItem.getFeed().getId()));

        feedItemWrapper.setFeed(feedWrapper);
        return feedItemWrapper;
    }
}
