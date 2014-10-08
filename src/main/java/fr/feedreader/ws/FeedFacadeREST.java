/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.buisness.TimerBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.ws.wrapper.FeedItemResponseWrapper;
import fr.feedreader.ws.wrapper.FeedItemUrlWrapper;
import fr.feedreader.ws.wrapper.FeedItemWrapper;
import fr.feedreader.ws.wrapper.FeedWrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author philippe
 */
@Path("/feed")
@Consumes(value = MediaType.APPLICATION_JSON)
@Produces(value = MediaType.APPLICATION_JSON)
public class FeedFacadeREST  {
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Inject
    private TimerBuisness timerBuisness;
    
    @Context
    private HttpServletRequest request;
    
    @GET
    public List<FeedWrapper> listFeed() {
        Map<Feed, Long> countUnread = feedBuisness.countUnread();
        return feedBuisness.findAll().stream().map((Feed feed)-> {
            FeedWrapper feedWrapper = new FeedWrapper(feed);
            Long unread = countUnread.get(feed);
            if (unread != null) {
                feedWrapper.setUnread(unread);
            }
            return feedWrapper;
        }).collect(Collectors.toList());
    }
    
    @DELETE
    public Response deleteFeed(@QueryParam("id") Integer feedId) {
        try {
            feedBuisness.delete(feedId);
            return Response.ok().build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }
    
    @GET
    @Path("/update")
    public Response updateAllFeed() {
        try {
            timerBuisness.updateFeed();
            return Response.ok().build();
        } catch(Exception e) {
            return Response.serverError().build();
        }
    }
    
    @PUT
    public FeedWrapper addFeed(FeedWrapper feedWrapper) {
        Feed feed = feedBuisness.add(feedWrapper.toFeed());
        return new FeedWrapper(feed);
    }
    
    @GET
    @Path("/{id}/item")
    public FeedItemResponseWrapper updateFeed(
            @PathParam("id") Integer id,
            @DefaultValue("1") @QueryParam("page") Integer page
    ) {
        List<FeedItemWrapper> feedItemsWrapper = feedItemBuisness.findAll(id, page).stream().map((feedItem) -> {
            
            return new FeedItemWrapper(
                feedItem,
                new FeedItemUrlWrapper(request.getContextPath(), feedItem)
            );
        }).collect(Collectors.toList());
        
        return new FeedItemResponseWrapper(
            feedItemsWrapper,
            feedItemBuisness.getTotalPage(id)
        );
    }
}
