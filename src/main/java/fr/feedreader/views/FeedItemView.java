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
    
    private Label title = null;
    
    private Long countUnreadFeed = null;
    
    public Boolean noReadedOnly = false;
    
    public Button noReadedOnlyButton = null;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if(init) {
            
        } else {
            removeAllComponents();
            noReadedOnly = getSession().getAttribute("noReadedOnly") == null ? Boolean.FALSE : (Boolean)getSession().getAttribute("noReadedOnly");
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

            noReadedOnlyButton = new Button(FontAwesome.STAR);
            noReadedOnlyButton.addClickListener((clickEvent) -> {
                noReadedOnly = !noReadedOnly;
                getSession().setAttribute("noReadedOnly", noReadedOnly);
                updateTotalPage();
                update();
            });
            
            initFeedItemLayout(feed, page);
            updateTotalPage();
            countUnreadFeed = feedBuisness.countUnread(feed.getId());
            
            HorizontalLayout titleLayout = new HorizontalLayout();
            titleLayout.setWidth(100, Unit.PERCENTAGE);
            title = new Label();
            setCounterTitle(countUnreadFeed);
            title.setContentMode(ContentMode.HTML);
            title.setWidthUndefined();
            
            Button back = new Button();
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
            
            HorizontalLayout noReadLayout = new HorizontalLayout();
            noReadLayout.addComponent(noReadedOnlyButton);
            noReadLayout.setComponentAlignment(noReadedOnlyButton, Alignment.MIDDLE_RIGHT);
            noReadLayout.setWidthUndefined();
            noReadLayout.setHeight("4em");
            
            
            // Test AbsoluteLayout absoluteLayout;
            AbsoluteLayout absoluteLayout = new AbsoluteLayout();
            absoluteLayout.setWidth("100%");
            absoluteLayout.setHeight("3.5em");
            absoluteLayout.addComponent(titleLayout, "left: 0");
            absoluteLayout.addComponent(backLayout, "left: 0");
            absoluteLayout.addComponent(noReadLayout, "right: 0");

//            addComponent(titleLayout);
            addComponent(absoluteLayout);
            
            
            
            HorizontalLayout paginator = new HorizontalLayout();
            Button previous = new Button();
            previous.setIcon(FontAwesome.ARROW_CIRCLE_O_LEFT);
            Button next = new Button();
            next.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
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
            
            if(Utils.isSmall(getUI()) == false) {
                back.setCaption("Retour");
                previous.setCaption("Précédent");
                next.setCaption("Suivant");
                next.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_RIGHT);
            }
        }
    }
    
    protected void updateTotalPage() {
        if (noReadedOnly) {
            totalPage = feedItemBuisness.getTotalPage(feed.getId(), false);
        } else {
            totalPage = feedItemBuisness.getTotalPage(feed.getId());
        }
    }
    
    protected void update() {
        initFeedItemLayout(feed, page);
        pageLabel.setValue(page + " / " + totalPage);
    }
    
    protected void initFeedItemLayout(Feed feed, Integer page) {
        feedItemLayout.removeAllComponents();
        
        if (noReadedOnly) {
            noReadedOnlyButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        } else {
            noReadedOnlyButton.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
        }

        List<FeedItem> findAll = null;
        if (noReadedOnly) {
            findAll = feedItemBuisness.findAll(feed.getId(), page, false);
        } else {
            findAll = feedItemBuisness.findAll(feed.getId(), page);
        }
        findAll.stream().forEach((feedItem) -> {
            Component c = createFeedItemComponent(feedItem);
            feedItemLayout.addComponent(c);
        });
    }
    
    protected Component createFeedItemComponent(FeedItem feedItem) {
        HorizontalLayout feedItemLayout = new HorizontalLayout();
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
        
        if (Utils.isSmall(getUI()) == false) {
            Button switchReaded = new Button(feedItem.getReaded() ? FontAwesome.STAR_O : FontAwesome.STAR);
            switchReaded.addClickListener((clickEvent) -> {
                if (feedItem.getReaded()) {
                    feedItem.setReaded(feedItemBuisness.setReaded(feedItem.getId(), Boolean.FALSE).getReaded());
                    switchReaded.setIcon(FontAwesome.STAR);
                    b.addStyleName(ValoTheme.BUTTON_FRIENDLY);
                    countUnreadFeed++;
                    setCounterTitle(countUnreadFeed);
                } else {
                    feedItem.setReaded(feedItemBuisness.setReaded(feedItem.getId(), Boolean.TRUE).getReaded());
                    switchReaded.setIcon(FontAwesome.STAR_O);
                    b.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
                    countUnreadFeed--;
                }
                setCounterTitle(countUnreadFeed);
            });
            feedItemLayout.addComponent(switchReaded);
            switchReaded.setHeight("100%");
        }
        
        feedItemLayout.addComponent(b);
        feedItemLayout.setExpandRatio(b, 100);
        feedItemLayout.setWidth("100%");
        return feedItemLayout;
    }
    
    public void setCounterTitle(Long counter) {
        title.setValue(feed.getName() + (counter > 0 ? " <span class=\"badge\">" + counter + "</span>" : ""));
    }
}
