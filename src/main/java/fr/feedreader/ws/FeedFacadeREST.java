/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
                new FeedItemUrlWrapper(
                    request.getContextPath() + "/feedItem/" + feedItem.getId() + "/readed/true",
                    request.getContextPath() + "/feedItem/" + feedItem.getId() + "/readed/" + (feedItem.getReaded() ? Boolean.FALSE.toString() : Boolean.TRUE.toString())
                )
            );
        }).collect(Collectors.toList());
        
        return new FeedItemResponseWrapper(
            feedItemsWrapper,
            feedItemBuisness.getTotalPage(id)
        );
    }
    
//    @PersistenceContext(unitName = "FeedReaderPU")
//    private EntityManager em;
//
//    public FeedFacadeREST() {
//        super(Feed.class);
//    }
//
//    @POST
//    @Override
//    @Consumes({"application/xml", "application/json"})
//    public void create(Feed entity) {
//        super.create(entity);
//    }
//
//    @PUT
//    @Path("{id}")
//    @Consumes({"application/xml", "application/json"})
//    public void edit(@PathParam("id") Integer id, Feed entity) {
//        super.edit(entity);
//    }
//
//    @DELETE
//    @Path("{id}")
//    public void remove(@PathParam("id") Integer id) {
//        super.remove(super.find(id));
//    }
//
//    @GET
//    @Path("{id}")
//    @Produces({"application/xml", "application/json"})
//    public Feed find(@PathParam("id") Integer id) {
//        return super.find(id);
//    }
//
//    @GET
//    @Override
//    @Produces({"application/xml", "application/json"})
//    public List<Feed> findAll() {
//        return super.findAll();
//    }
//
//    @GET
//    @Path("{from}/{to}")
//    @Produces({"application/xml", "application/json"})
//    public List<Feed> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
//        return super.findRange(new int[]{from, to});
//    }
//
//    @GET
//    @Path("count")
//    @Produces("text/plain")
//    public String countREST() {
//        return String.valueOf(super.count());
//    }
//
//    @Override
//    protected EntityManager getEntityManager() {
//        return em;
//    }
    
}
