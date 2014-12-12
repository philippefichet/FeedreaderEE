/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import com.rometools.rome.io.FeedException;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fr.feedreader.junit.Assert.*;
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
        File[] dependencies = Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
        WebArchive archive = ShrinkWrap.create(WebArchive.class)
            .addPackage("fr.feedreader.junit")
            .addPackage("fr.feedreader.buisness")
            .addPackage("fr.feedreader.models")
            .addAsLibraries(dependencies)
            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        return archive;
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
