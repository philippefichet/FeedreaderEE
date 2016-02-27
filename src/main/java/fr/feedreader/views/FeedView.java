/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.views;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.models.Feed;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author glopinous
 */
@CDIView("")
public class FeedView extends VerticalLayout implements View {

    private boolean init = false;
    
    private List<Button> feedButtons = new ArrayList<>();
    
    private VerticalLayout feedLayout = new VerticalLayout();
    
    private Map<Feed, Long> countUnread = null;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(init) {
            
        } else {
            removeAllComponents();
            addStyleName("feed");
            HorizontalLayout titleLayout = new HorizontalLayout();
            Label title = new Label("Visualisation des flux rss/atom");
            title.setWidthUndefined();
            titleLayout.addComponent(title);
            titleLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
            titleLayout.setStyleName("title");
            titleLayout.setWidth("100%");
            titleLayout.setSpacing(true);
            addComponent(titleLayout);
            addComponent(feedLayout);
            initFeedLayout();
           
        }
    }
    
    public Map<Feed, Long> getCountUnread() {
        if (countUnread == null) {
            countUnread = feedBuisness.countUnread();
        }
        return countUnread;
    }

    public void setCountUnread(Map<Feed, Long> countUnread) {
        this.countUnread = countUnread;
    }
    
    public void initFeedLayout() {
        feedLayout.removeAllComponents();
        
        feedBuisness.findAll().stream().forEach((feed) -> {
            Long countUnreadFeed = getCountUnread().get(feed);
            Button b = new Button(feed.getName() + (countUnreadFeed != null ? " <span class=\"badge\">" + countUnreadFeed + "</span>" : ""));
            b.setHtmlContentAllowed(true);
            b.setIcon(FontAwesome.ARROW_CIRCLE_RIGHT);
            b.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            b.addStyleName("multiline");
            if (countUnreadFeed != null && countUnreadFeed > 0) {
                b.addStyleName(ValoTheme.BUTTON_PRIMARY);
            }
            b.addClickListener((clickEvent) -> {
                getUI().getNavigator().navigateTo("feedItem/" + feed.getId() + "/1");
            });
            b.setWidth(100, Unit.PERCENTAGE);
            feedLayout.addComponent(b);
        });
    }
    
}
