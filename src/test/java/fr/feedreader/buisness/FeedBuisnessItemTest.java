/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import com.rometools.rome.io.FeedException;
import fr.feedreader.Bootstrap;
import fr.feedreader.models.Feed;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.feedreader.junit.Utils;
import static org.junit.Assert.*;

/**
 *
 * @author philippe
 */
@RunWith(Arquillian.class)
public class FeedBuisnessItemTest {
    
    @Inject
    private Utils utils;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
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
        
        war.addAsResource(new File("./src/test/resources/atom.atom"), "fr/feedreader/junit/atom.atom");
        war.addAsResource(new File("./src/test/resources/developpez.atom"), "fr/feedreader/junit/developpez.atom");
        war.addAsResource(new File("./src/test/resources/developpez-update.atom"), "fr/feedreader/junit/developpez-update.atom");
        war.addAsResource(new File("./src/test/resources/linuxfr.atom"), "fr/feedreader/junit/linuxfr.atom");
        war.addAsResource(new File("./src/test/resources/rss.rss"), "fr/feedreader/junit/rss.rss");
        
        try {
            return Bootstrap.addAsLibrary(war);
        } catch(Exception e) {
            return war;
        }
    }
    
    @Test
    public void markAsRead() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        Feed developpez = utils.loadDeveloppez();
        feedBuisness.refreshFeedItems(developpez.getId());
        Long count = feedBuisness.countUnread(developpez.getId());
        assertTrue(count + " au lieu de 20", count == 20L);
        int countBecameUnread = feedItemBuisness.maskAllFeedItemToRead(developpez.getId());
        assertEquals(20, countBecameUnread);
        count = feedBuisness.countUnread(developpez.getId());
        assertTrue(count + " au lieu de 0", count == 0L);

    }
}
