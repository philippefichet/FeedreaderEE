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
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/**
 *
 * @author glopinous
 */
@CDIView(value = "feedItem")
public class FeedItemView extends VerticalLayout implements View {

    private boolean init = false;
    
    private VerticalLayout feedItemLayout = new VerticalLayout();
    
    private Integer page = 1;
    
    private Feed feed = null;
    
    private Map<Feed, Long> countUnread = null;
    
    private Long totalPage = null;
    
    private Label pageLabel = null;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("init = " + init);
        if(init) {
            
        } else {
            removeAllComponents();
            feedItemLayout.addStyleName("feedItem");
            String[] parameter = event.getParameters().split("/");
            feed = feedBuisness.find(Integer.parseInt(parameter[0]));
            if (parameter.length > 1) {
                try {
                    page = Integer.parseInt(parameter[1]);
                } catch(NumberFormatException e) {
                    page = 1;
                }
            } else {
                page = 1;
            }

            initFeedItemLayout(feed, page);
            totalPage = feedItemBuisness.getTotalPage(feed.getId());
            
            countUnread = feedBuisness.countUnread();
            
            HorizontalLayout titleLayout = new HorizontalLayout();
            titleLayout.setWidth(100, Unit.PERCENTAGE);
            
            Label title = new Label(feed.getName() + " <span class=\"badge\">" + countUnread.get(feed) + "</span>");
            title.setContentMode(ContentMode.HTML);
            title.setWidthUndefined();
            
            Button back = new Button("Retour");
            back.setIcon(FontAwesome.ARROW_CIRCLE_LEFT);
            back.addClickListener((eventClick) -> {
                getUI().getNavigator().navigateTo("");
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
            absoluteLayout.setHeight("3.5em");
            absoluteLayout.addComponent(titleLayout, "left: 0");
            absoluteLayout.addComponent(backLayout, "left: 0");


            
            
//            addComponent(titleLayout);
            addComponent(absoluteLayout);
            
            
            
            HorizontalLayout paginator = new HorizontalLayout();
            Button previous = new Button("Précédent");
            previous.setIcon(FontAwesome.ARROW_CIRCLE_O_LEFT);
            Button next = new Button("Suivant");
            next.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
            next.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            pageLabel = new Label(page + " / " + totalPage);
            pageLabel.setWidthUndefined();
            paginator.addComponent(previous);
            paginator.addComponent(pageLabel);
            paginator.addComponent(next);
            
            paginator.setComponentAlignment(previous, Alignment.MIDDLE_LEFT);
            paginator.setComponentAlignment(pageLabel, Alignment.MIDDLE_CENTER);
            paginator.setComponentAlignment(next, Alignment.MIDDLE_RIGHT);
            paginator.setWidth(100, Unit.PERCENTAGE);
            
            paginator.setHeight("3.5em");
            addComponent(paginator);
            addComponent(feedItemLayout);
            
            next.addClickListener((eventClick) -> {
                if (page < totalPage) {
                    page++;
                    Page.getCurrent().setUriFragment("!feedItem/" + feed.getId() + "/" + page, false);
                    update();
                }
            });
            previous.addClickListener((eventClick) -> {
                if (page > 1) {
                    page--;
                    Page.getCurrent().setUriFragment("!feedItem/" + feed.getId() + "/" + page, false);
                    update();
                }
            });
        }
    }
    
    protected void update() {
        initFeedItemLayout(feed, page);
        pageLabel.setValue(page + " / " + totalPage);
    }
    
    protected void initFeedItemLayout(Feed feed, Integer page) {
        feedItemLayout.removeAllComponents();
        List<FeedItem> findAll = feedItemBuisness.findAll(feed.getId(), page);
        findAll.stream().forEach((feedItem) -> {
            Component c = createFeedItemComponent(feedItem);
            feedItemLayout.addComponent(c);
        });
    }
    
    protected Component createFeedItemComponent(FeedItem feedItem) {
        Button b = new Button(feedItem.getTitle());
        b.setWidth(100, Unit.PERCENTAGE);
        b.setIcon(FontAwesome.ARROW_CIRCLE_RIGHT);
        b.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
        b.addStyleName("multiline");
        if (!feedItem.getReaded()) {
            b.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        }
        b.addClickListener((eventClick) -> {
            getUI().getNavigator().navigateTo("feedItemDetail/" + feedItem.getId() + "/" + page);
        });
        return b;
    }
}
