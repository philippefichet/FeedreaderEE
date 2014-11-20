/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ui;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.UI;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import fr.feedreader.views.FeedItemDetailView;
import fr.feedreader.views.FeedItemView;
import fr.feedreader.views.FeedView;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author philippe
 */
@CDIUI(value = "/v")
@Theme(value = "feedreader-dark")
@Push(transport = Transport.STREAMING)
public class MainUI  extends UI {
    
    // Concervation des session pour communication avec plusieur utilisateur (broadcast)
    private static List<MainUI> availableUI = new Vector<>();

    @Inject
    private CDIViewProvider viewProvider;
    
    private View currentView = null;
    
    @Override
    public void init(VaadinRequest request) {
        System.out.println("viewProvider = " + viewProvider);
        if (getSession().getAttribute("MobileBoostrapListener") == null) {
            getSession().addBootstrapListener(new MobileBoostrapListener());
            getSession().setAttribute("MobileBoostrapListener", true);
        }
        
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        navigator.addView("", FeedView.class);
        navigator.addView("feedItem", FeedItemView.class);
        navigator.addView("feedItemDetail", FeedItemDetailView.class);
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
                currentView = event.getNewView();
            }
        });
        
        availableUI.add(this);
        
        addDetachListener((DetachEvent event) -> {
            availableUI.remove(this);
        });
        
        // Autorisation des notifications html
        JavaScript.getCurrent().execute("var Notification = window.Notification || window.mozNotification || window.webkitNotification; Notification.requestPermission(function (permission) { console.log('Notification : ' + permission) });");
        
    }
    
    /**
     * Mise à jour des vue contenant des flux
     * @param newFeedItem
     * @param countUnread 
     */
    public void updateFeed(Map<Feed, List<FeedItem>> newFeedItem, Map<Feed, Long> countUnread) {
        getUI().access(() -> {
            JavaScript javaScript = getPage().getJavaScript();
            StringBuilder sb = new StringBuilder();
            Long sum = 0L;
            Iterator<Feed> i = newFeedItem.keySet().iterator();
            while (i.hasNext()) {
                Feed feed = i.next();
                int count = newFeedItem.get(feed).size();
                if (count > 0) {
                    sb.append(feed.getName() + " (" + count + "), ");
                    sum += count;
                }
            }
            if (sb.length() > 2) {
                sb.delete(sb.length()-2, sb.length());
            }
            // Notification html
            javaScript.execute("new Notification(\"" + sum + " Nouveau(x) article(s)\", {\"body\": \"" + sb.toString() + "\", \"icon\": \"/feedreader/images/feed.png\"});");
            
            // Mise à jour des compteurs et flux
            if (currentView instanceof FeedView) {
                FeedView feedView = (FeedView)currentView;
                feedView.setCountUnread(countUnread);
                feedView.initFeedLayout();
            }
            
            // Mise à jour du compteur et flux
            if (currentView instanceof FeedItemView) {
                FeedItemView feedItemView = (FeedItemView)currentView;
            }
            
            // Mise à jour du compteur
            if (currentView instanceof FeedItemDetailView) {
                FeedItemDetailView feedItemDetailView = (FeedItemDetailView)currentView;
            }
        });
    }
    
    /**
     * Permet de mettre à jour les MainUI connecté
     * @param newFeedItem
     * @param countUnread 
     */
    public static void notifyUpdateFeed(Map<Feed, List<FeedItem>> newFeedItem, Map<Feed, Long> countUnread) {
        LogManager.getLogger().info("notification de mise à jour de flux vaadin");
        System.out.println("newFeedItem = " + newFeedItem);
        System.out.println("countUnread = " + countUnread);
        for (Iterator<MainUI> it = availableUI.iterator(); it.hasNext();) {
            MainUI ui = it.next();
            if(ui.isAttached()) {
                ui.updateFeed(newFeedItem, countUnread);
                LogManager.getLogger().info("Envoyer à \"" + ui + "\"");
            } else {
                it.remove();
                LogManager.getLogger().error("MainUI \"" + ui+ "\" Fermer. Exclusion de la liste des UI active");
            }
        }
    }
}
