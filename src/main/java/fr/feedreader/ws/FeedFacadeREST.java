/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.ws.wrapper.FeedWrapper;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author philippe
 */
@Stateless
@Path("/feed")
@Consumes(value = MediaType.APPLICATION_JSON)
@Produces(value = MediaType.APPLICATION_JSON)
public class FeedFacadeREST  {
    @Inject
    private FeedBuisness feedBuisness;
    
    @GET
    public List<FeedWrapper> listFeed() {
        return feedBuisness.findAll().stream().map(
            FeedWrapper::feedToFeedWrapper
        ).collect(Collectors.toList());
    }
    
    @PUT
    public FeedWrapper addFeed(FeedWrapper feedWrapper) {
        Feed feed = feedBuisness.add(feedWrapper.toFeed());
        return FeedWrapper.feedToFeedWrapper(feed);
    }
    
    @Path("?id={id}")
    @POST
    public void updateFeed(FeedWrapper feedWrapper) {
        
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
