/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import fr.feedreader.Bootstrap;
import fr.feedreader.models.Feed;
import java.util.Collections;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author philippefichet
 */
@RunWith(Arquillian.class)
public class FeedHasErrorBuisnessTest {
    
    @Inject
    private FeedHasErrorBuisness feedHasErrorBuisness;

    @Inject
    private FeedBuisness feedBuisness;
    
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
            .addPackage("fr.feedreader.junit")
            .addPackage("fr.feedreader.buisness")
            .addPackage("fr.feedreader.hibernate")
            .addPackage("fr.feedreader.models")
            .deleteClass("fr.feedreader.buisness.TimerBuisness")
            .addAsResource("META-INF/persistence-arquillian.xml", "META-INF/persistence.xml")
            .addAsResource("fr/feedreader/liquibase/", "fr/feedreader/liquibase/")
            .addAsResource("META-INF/services/org.hibernate.integrator.spi.Integrator", "META-INF/services/org.hibernate.integrator.spi.Integrator")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        try {
            return Bootstrap.addAsLibrary(war);
        } catch(Exception e) {
            return war;
        }
    }
    
    @Test
    public void errorOnUpdateAllFeed() {
        Feed feed = new Feed();
        feed.setUrl("test.unit.io");
        Feed feedUpdate = feedBuisness.add(feed);
        feedBuisness.parallelUpdateAllFeed();
        Feed feedOnError = feedBuisness.find(feedUpdate.getId());
        assertNotNull(feedOnError.getError());
        assertEquals("URI is not absolute", feedOnError.getError().getError());
        feedHasErrorBuisness.updateErrorOnAllFeeds(Collections.emptyList());
        Feed feedHadError = feedBuisness.find(feedUpdate.getId());
        assertNull(feedHadError.getError());
        
    }
}
