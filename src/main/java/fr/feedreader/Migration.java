/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader;

import fr.feedreader.hibernate.Liquibase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author glopinous
 */


public class Migration {
    static private Logger LOGGER = LogManager.getLogger(Migration.class);;
    static private void migrationFeed(Connection connectionSource, Connection connectionTarget) throws SQLException {
        connectionTarget.setAutoCommit(true);
        Statement deleteStatement = connectionTarget.createStatement();
        try {
            deleteStatement.executeUpdate("DELETE FROM FEED");
            LOGGER.info("DELETE FROM FEED OK");
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        deleteStatement.close();
        PreparedStatement feedPS = connectionTarget.prepareStatement("INSERT INTO FEED (ID, NAME, URL, DESCRIPTION, LASTUPDATE, ENABLE) VALUES(?, ?, ?, ?, ?, ?)");
        Statement sourceStatement = connectionSource.createStatement();
        ResultSet allFeed = sourceStatement.executeQuery("SELECT * FROM FEED");
        LOGGER.info("SELECT * FROM FEED OK");
        while (allFeed.next()) {
            feedPS.setInt(1, allFeed.getInt("ID"));
            feedPS.setString(2, allFeed.getString("NAME"));
            feedPS.setString(3, allFeed.getString("URL"));
            feedPS.setString(4, allFeed.getString("DESCRIPTION"));
            feedPS.setString(5, allFeed.getString("LASTUPDATE"));
            feedPS.setBoolean(6, allFeed.getBoolean("ENABLE"));
            feedPS.addBatch();
        }
        try {
            feedPS.executeBatch();
            LOGGER.info("Batch FEED OK");
        } catch(SQLException e) {
            LOGGER.error(e);
        }
        allFeed.close();
        sourceStatement.close();
        feedPS.close();
    }

    static private void migrationFeedItem(Connection connectionSource, Connection connectionTarget) throws SQLException {
        connectionTarget.setAutoCommit(true);
        Statement deleteStatement = connectionTarget.createStatement();
        try {
            deleteStatement.executeUpdate("DELETE FROM FEEDITEM");
            LOGGER.info("DELETE FROM FEEDITEM OK");
        } catch (SQLException e) {
            LOGGER.error(e);
        }
        deleteStatement.close();
        PreparedStatement feedItemPS = connectionTarget.prepareStatement("INSERT INTO FEEDITEM (ID, FEEDITEMID, TITLE, LINK, ENCLOSURE, UPDATED, FEED_ID, READED) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        Statement sourceStatement = connectionSource.createStatement();
        ResultSet allFeedItem = sourceStatement.executeQuery("SELECT * FROM FEEDITEM");
        LOGGER.info("SELECT * FROM FEEDITEM OK");
        while (allFeedItem.next()) {
            feedItemPS.setInt(1, allFeedItem.getInt("ID"));
            feedItemPS.setString(2, allFeedItem.getString("FEEDITEMID"));
            feedItemPS.setString(3, allFeedItem.getString("TITLE"));
            feedItemPS.setString(4, allFeedItem.getString("LINK"));
            feedItemPS.setString(5, allFeedItem.getString("ENCLOSURE"));
            feedItemPS.setString(6, allFeedItem.getString("UPDATED"));
            feedItemPS.setInt(7, allFeedItem.getInt("FEED_ID"));
            feedItemPS.setBoolean(8, allFeedItem.getBoolean("READED"));
            feedItemPS.addBatch();
        }
        try {
            feedItemPS.executeBatch();
            LOGGER.info("Batch FEEDITEM OK");
        } catch(SQLException e) {
            LOGGER.error(e);
        }
        allFeedItem.close();
        sourceStatement.close();
        feedItemPS.close();
    }
    
    static public void migration(Connection connectionSource, Connection connectionTarget) {
        try {
            Liquibase.update(connectionSource);
            Liquibase.update(connectionTarget);
            LOGGER.info("Liquibase fini");
            migrationFeed(connectionSource, connectionTarget);
            migrationFeedItem(connectionSource, connectionTarget);
            LOGGER.info("Migration Fini");
        } catch(SQLException ex) {
            LOGGER.error(ex);
        }
    }
}
