/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws;

import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.FeedItem;
import fr.feedreader.ws.wrapper.FeedItemUrlWrapper;
import fr.feedreader.ws.wrapper.FeedItemWrapper;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 *
 * @author philippe
 */
@Stateless
@Path("/feedItem")
public class FeedItemFacadeREST {
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Context
    private HttpServletRequest request;
    
    @POST
    @Path("{id}/readed/{readed}")
    public FeedItemWrapper setReadedFeedItem(@PathParam("id") Integer feedItemId, @PathParam("readed") Boolean readed) {
        FeedItem feedItem = feedItemBuisness.find(feedItemId);
        feedItem.setReaded(readed);
        feedItemBuisness.update(feedItem);
        
        return new FeedItemWrapper(
            feedItem,
            new FeedItemUrlWrapper(request, feedItem)
        );
    }
}
