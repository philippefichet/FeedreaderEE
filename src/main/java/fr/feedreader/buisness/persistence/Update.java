/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness.persistence;

import fr.feedreader.models.FeedItem;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author glopinous
 */
@Startup
@Singleton
public class Update {
    
    @PersistenceContext(unitName = "FeedReaderPU")
    private EntityManager em;
    
    @PostConstruct
    public void updateDb() {
        Logger logger = LogManager.getLogger();
        logger.info("Start update database !");
        try {
            int summaryLength = FeedItem.class.getDeclaredField("summary").getAnnotation(Column.class).length();
            Query createNativeQuery = em.createNativeQuery("ALTER TABLE FEEDITEM ALTER COLUMN SUMMARY VARCHAR(" + summaryLength + ")");
            createNativeQuery.executeUpdate();
            logger.info("Update databases sucessful !" );
        } catch (NoSuchFieldException | SecurityException ex) {
            logger.warn("Unable update databases ! " , ex);
        }
    }
}
