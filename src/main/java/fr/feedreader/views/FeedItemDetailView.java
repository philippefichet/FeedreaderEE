/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author glopinous
 */
@CDIView(value = "feedItemDetail")
public class FeedItemDetailView extends VerticalLayout implements View {
    
    private boolean init = false;
    
    private Integer page = 1;
    
    private Map<Feed, Long> countUnread = null;
    
    private Label title = null;
    
    private FeedItem feedItem = null;
    
    private Button switchReaded = null;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(init) {
            
        } else {
            removeAllComponents();
            String[] parameter = event.getParameters().split("/");
            feedItem = feedItemBuisness.find(Integer.parseInt(parameter[0]));
            if (!feedItem.getReaded()) {
                feedItem = feedItemBuisness.setReaded(feedItem.getId(), Boolean.TRUE);
            }
            if (parameter.length > 1) {
                try {
                    page = Integer.parseInt(parameter[1]);
                } catch(NumberFormatException e) {
                    page = 1;
                }
            } else {
                page = 1;
            }
            
            countUnread = feedBuisness.countUnread();
            
            HorizontalLayout titleLayout = new HorizontalLayout();
            titleLayout.setWidth(100, Unit.PERCENTAGE);
            Long countUnreadFeed = countUnread.get(feedItem.getFeed());
            title = new Label(feedItem.getFeed().getName() + (countUnreadFeed != null ? " <span class=\"badge\">" + countUnreadFeed + "</span>" : ""));
            title.setContentMode(ContentMode.HTML);
            title.setWidthUndefined();
            
            Button back = new Button("Retour");
            back.setData(feedItem.getFeed().getId());
            back.setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
            back.addClickListener((eventClick) -> {
                getUI().getNavigator().navigateTo("feedItem/" + eventClick.getButton().getData() + "/" + page);
            });
            
            titleLayout.addComponent(title);
            titleLayout.addStyleName("title");
            titleLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
            titleLayout.setHeight("4em");
            
            HorizontalLayout backLayout = new HorizontalLayout();
            backLayout.addComponent(back);
            backLayout.setComponentAlignment(back, Alignment.MIDDLE_LEFT);
            backLayout.setWidthUndefined();
            backLayout.setHeight("4em");
            
            // Test AbsoluteLayout absoluteLayout;
            AbsoluteLayout absoluteLayout = new AbsoluteLayout();
            absoluteLayout.setWidth("100%");
            absoluteLayout.setHeight("4em");
            absoluteLayout.addComponent(titleLayout, "left: 0");
            absoluteLayout.addComponent(backLayout, "left: 0");
            addComponent(absoluteLayout);

            HorizontalLayout actionLayout = new HorizontalLayout();
            actionLayout.setHeight("4em");
            Panel feedItemPanel = new Panel(feedItem.getTitle());
            Label summary = new Label(feedItem.getSummary());
            summary.setContentMode(ContentMode.HTML);
            Link link = new Link("Lien vers l'article", new ExternalResource(feedItem.getLink()));
            VerticalLayout feedItemDetailLayout = new VerticalLayout();
            feedItemDetailLayout.addComponent(summary);
            feedItemDetailLayout.addComponent(link);
            feedItemPanel.setContent(feedItemDetailLayout);
            
            switchReaded = new Button(FontAwesome.STAR);
            switchReaded.addClickListener((eventClick) -> {
                switchReaded();
                updateReaded();
            });
            actionLayout.addComponent(switchReaded);
            actionLayout.setComponentAlignment(switchReaded, Alignment.MIDDLE_LEFT);
            addComponent(actionLayout);
            addComponent(feedItemPanel);
        }
    }
    
    protected void switchReaded() {
        feedItem = feedItemBuisness.setReaded(feedItem.getId(), !feedItem.getReaded());
    }
    
    protected void updateReaded() {
        countUnread = feedBuisness.countUnread();
        Long countUnreadFeed = countUnread.get(feedItem.getFeed());
        title.setValue(
                feedItem.getFeed().getName() + 
                (countUnreadFeed != null ? " <span class=\"badge\">" + countUnreadFeed + "</span>" : "")
        );
        if (feedItem.getReaded()) {
             switchReaded.setIcon(FontAwesome.STAR);
        } else {
            switchReaded.setIcon(FontAwesome.STAR_O);
        }
    }
}
