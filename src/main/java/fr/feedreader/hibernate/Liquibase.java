/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import liquibase.database.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 *
 * @author glopinous
 */
public class Liquibase implements Integrator {
    private static Logger logger = LogManager.getLogger();
    
    static public void update(Connection connection) {
        try {
            String url = connection.getMetaData().getURL();
            Database db = null;
            if (url.startsWith("jdbc:hsqldb")) {
               db = DatabaseFactory.getInstance().getDatabase("hsqldb");
            } else if (url.startsWith("jdbc:h2")) {
               db = DatabaseFactory.getInstance().getDatabase("h2");
            } else if (url.startsWith("jdbc:mysql")) {
               db = DatabaseFactory.getInstance().getDatabase("mysql");
            } else {
                throw new LiquibaseException(url + " => driver pour liquibase non gérer");
            }
            db.setConnection(new JdbcConnection(connection));
            ClassLoaderResourceAccessor ra = new ClassLoaderResourceAccessor(Liquibase.class.getClassLoader());
            liquibase.Liquibase liquibase = new liquibase.Liquibase("fr/feedreader/liquibase/db.changelog.xml", ra, db);
            logger.info("Liquibase update");
            liquibase.update("");
        } catch (SQLException ex) {
            logger.warn(ex);
        } catch (LiquibaseException ex) {
            logger.warn(ex);
        }
    }
    
    @Override
    public void integrate(Metadata mtdt, SessionFactoryImplementor sfi, SessionFactoryServiceRegistry sfsr) {
        SessionImpl session = ((SessionImpl)sfi.openSession());
        update(session.connection());
        session.close();
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sfi, SessionFactoryServiceRegistry sfsr) {
        
    }
}
