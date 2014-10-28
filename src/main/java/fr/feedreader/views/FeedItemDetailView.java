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
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
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
            FeedItem feedItem = feedItemBuisness.find(Integer.parseInt(parameter[0]));
            if (!feedItem.getReaded()) {
                feedItemBuisness.setReaded(feedItem.getId(), Boolean.TRUE);
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
            
            Label title = new Label(feedItem.getFeed().getName() + " <span class=\"badge\">" + countUnread.get(feedItem.getFeed()) + "</span>");
            title.setContentMode(ContentMode.HTML);
            title.setWidthUndefined();
            
            Button back = new Button("Retour");
            back.setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
            back.addClickListener((eventClick) -> {
                getUI().getNavigator().navigateTo("feedItem/" + feedItem.getFeed().getId() + "/" + page);
            });
            
//            titleLayout.addComponent(back);
            titleLayout.addComponent(title);
            titleLayout.addStyleName("title");
            titleLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
            titleLayout.setHeight("4em");
//            titleLayout.setExpandRatio(title, 100);
            
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


            
            
//            addComponent(titleLayout);
            addComponent(absoluteLayout);
            
            
//            HorizontalLayout titleLayout = new HorizontalLayout();
//            titleLayout.setWidth(100, Unit.PERCENTAGE);
//            
//            Label title = new Label(feedItem.getFeed().getName() + " <span class=\"valo-menu-badge\">" + countUnread.get(feedItem.getFeed()) + "</span>");
//            title.setContentMode(ContentMode.HTML);
//            title.setWidthUndefined();
//            
//            Button back = new Button("Retour");
//            back.addClickListener((eventClick) -> {
//                getUI().getNavigator().navigateTo("feedItem/" + feedItem.getFeed().getId() + "/" + page);
//            });
//            
//            titleLayout.addComponent(back);
//            titleLayout.addComponent(title);
//            titleLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
//            titleLayout.setExpandRatio(back, 0);
//            titleLayout.setExpandRatio(title, 100);
//
//            addComponent(titleLayout);
            
            Panel feedItemPanel = new Panel(feedItem.getTitle());
            Label summary = new Label(feedItem.getSummary());
            summary.setContentMode(ContentMode.HTML);
            Link link = new Link("Lien vers l'article", new ExternalResource(feedItem.getLink()));
            VerticalLayout feedItemDetailLayout = new VerticalLayout();
            feedItemDetailLayout.addComponent(summary);
            feedItemDetailLayout.addComponent(link);
            feedItemPanel.setContent(feedItemDetailLayout);
            
            addComponent(feedItemPanel);
        }
    }
}
