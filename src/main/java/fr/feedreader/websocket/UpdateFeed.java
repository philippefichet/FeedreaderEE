/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import fr.feedreader.ws.wrapper.FeedUpdateWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author philippe
 */
@ServerEndpoint("/live/feed")
public class UpdateFeed {
    
    // Concervation des session pour communication avec plusieur utilisateur (broadcast)
    private static List<Session> availableSession = new Vector<>();
    
    public static void notifyUpdateFeed(Map<Feed, List<FeedItem>> newFeedItem, Map<Feed, Long> countUnread) {
        LogManager.getLogger().info("notification de mise à jour de flux");
        ObjectMapper mapper = new ObjectMapper();
        FeedUpdateWrapper feedUpdateWrapper= new FeedUpdateWrapper(newFeedItem, countUnread);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            mapper.writeValue(baos, feedUpdateWrapper);
            String jsonResponse = baos.toString("UTF-8");
            availableSession.iterator();
            for (Iterator<Session> it = availableSession.iterator(); it.hasNext();) {
                Session session = it.next();
                try {
                    session.getBasicRemote().sendText(jsonResponse);
                    LogManager.getLogger().info("Envoyer à \"" + session.getId() + "\"");
                } catch (ClosedChannelException ex) {
                    LogManager.getLogger().error("Session \"" + session.getId() + "\" Fermer. Exclusion de la liste des session active");
                    it.remove();
                } catch (IOException ex) {
                    LogManager.getLogger().error("Erreur dans l'envoi à la websocket : " + session.getId(), ex);
                }
            }
        } catch(IOException ioe) {
            LogManager.getLogger().error("Impossible de convertir les flux mis à jour en json", ioe);
        }
    }
    
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        availableSession.add(session);
        LogManager.getLogger().info("Opening websocket !!!! " + session.getId());
    }
    
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        availableSession.remove(session);
    }
}
