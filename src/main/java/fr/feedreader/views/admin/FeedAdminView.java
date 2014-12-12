/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.views.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author glopinous
 */
@CDIView(value = "adminFeed")
public class FeedAdminView extends VerticalLayout implements View {

    private boolean init = false;
    
    private long test = 0L;
    
    private Table feeds = new Table();
    
    @PostConstruct
    private void init() {
        test = System.currentTimeMillis();
    }
    
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
            feeds.removeAllItems();
            
            HorizontalLayout addLayout = new HorizontalLayout();
            addLayout.setWidth("100%");
            addLayout.setHeight("4em");
            addLayout.setSpacing(true);
            TextField textFieldUrl = new TextField();
            textFieldUrl.setInputPrompt("Url");
            textFieldUrl.setWidth("100%");
            TextField textFieldName = new TextField();
            textFieldName.setInputPrompt("Nom");
            textFieldName.setWidth("100%");
            TextField textFieldDescription = new TextField();
            textFieldDescription.setInputPrompt("Description");
            textFieldDescription.setWidth("100%");
            Button addSumbit = new Button("Ajouter");
            
            addLayout.addComponent(textFieldUrl);
            addLayout.addComponent(textFieldName);
            addLayout.addComponent(textFieldDescription);
            addLayout.addComponent(addSumbit);
            
            addLayout.setExpandRatio(textFieldUrl, 1);
            addLayout.setExpandRatio(textFieldName, 1);
            addLayout.setExpandRatio(textFieldDescription, 1);
            addLayout.setExpandRatio(addSumbit, 0);
            
            addLayout.setComponentAlignment(textFieldUrl, Alignment.MIDDLE_CENTER);
            addLayout.setComponentAlignment(textFieldName, Alignment.MIDDLE_CENTER);
            addLayout.setComponentAlignment(textFieldDescription, Alignment.MIDDLE_CENTER);
            addLayout.setComponentAlignment(addSumbit, Alignment.MIDDLE_CENTER);
            
            addComponent(addLayout);
            
            feeds.setWidth("100%");
            feeds.setHeightUndefined();
            feeds.setPageLength(0);
            feeds.addContainerProperty("Url", TextField.class, "");
            feeds.addContainerProperty("Nom", TextField.class, "");
            feeds.addContainerProperty("Description", TextField.class, "");
            feeds.addContainerProperty("Action", HorizontalLayout.class, "");
            
            List<Feed> findAll = feedBuisness.findAll();
            findAll.stream().forEach((feed) -> {
                System.out.println("feed = " + feed);
                feeds.addItem(feedToTableRow(feed), feed.getId());
            });
            
            addComponent(feeds);
            
            addSumbit.addClickListener((eventClick) -> {
                Feed feed = new Feed();
                feed.setUrl(textFieldUrl.getValue());
                feed.setName(textFieldName.getValue());
                feed.setDescription(textFieldDescription.getValue());
                try {
                    feed = feedBuisness.add(feed);
                    feeds.addItem(feedToTableRow(feed), feed.getId());
                    showSuccessNotification("Ajout du flux réussi");
                } catch(Exception e) {
                    showErrorNotification("Ajout du flux échec", e.getMessage());
                }
            });

        }
        
    }
    
    public Object[] feedToTableRow(Feed feed) {
        TextField url = new TextField();
        url.setValue(feed.getUrl());
        url.setWidth("100%");

        TextField name = new TextField();
        name.setValue(feed.getName());
        name.setWidth("100%");

        TextField description = new TextField();
        description.setValue(feed.getDescription());
        description.setWidth("100%");

        Button update = new Button(FontAwesome.ROTATE_RIGHT);
        Button save = new Button(FontAwesome.SAVE);
        Button markToRead = new Button(FontAwesome.STAR);
        Button clean = new Button(FontAwesome.ERASER);
        Button delete = new Button(FontAwesome.TRASH_O);
        update.setDescription("Récupération des articles");
        save.setDescription("Sauvegarde du flux");
        clean.setDescription("Supprimer tous les articles");
        delete.setDescription("Supprimer le flux");
        HorizontalLayout action = new HorizontalLayout(update, save, markToRead, clean, delete);
        action.setWidth("100%");
        action.setComponentAlignment(update, Alignment.MIDDLE_CENTER);
        action.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
        action.setComponentAlignment(markToRead, Alignment.MIDDLE_CENTER);
        action.setComponentAlignment(clean, Alignment.MIDDLE_CENTER);
        action.setComponentAlignment(delete, Alignment.MIDDLE_CENTER);
        
        update.addClickListener((eventClick) -> {
            try {
                List<FeedItem> refreshFeedItems = feedBuisness.refreshFeedItems(feed.getId());
                showSuccessNotification("Récupération de " + refreshFeedItems.size() + " nouveaux article réussi pour le flux \"" + feed.getName() + "\"");
            } catch(Exception e) {
                showErrorNotification("Récupération des nouveaux article en échec pour le flux \"" + feed.getName() + "\"", e.getLocalizedMessage());
            }
        });
        
        clean.addClickListener((eventClick) -> {
            try {
                int deleteFeedItems = feedItemBuisness.clean(feed.getId());
                showSuccessNotification("Suppression des articles du flux \"" + feed.getName() + "\" réussi de " + deleteFeedItems + " élément(s)");
            } catch(Exception e) {
                showErrorNotification("Suppression des articles du flux \"" + feed.getName() + "\" échéc", e.getLocalizedMessage());
            }
            
        });
        
        markToRead.addClickListener((eventClient) -> {
            try {
                int markToReadCount = feedItemBuisness.maskAllFeedItemToRead(feed.getId());
                showSuccessNotification("Marqué les flux comme lu pour \"" + feed.getName() + "\" réussi pour " + markToReadCount + " élément(s)");
            } catch(Exception e) {
                showErrorNotification("Marqué les flux comme lu pour \"" + feed.getName() + "\" échéc", e.getLocalizedMessage());
            }
        });
        
        save.addClickListener((eventClick) -> {
            try {
                feed.setUrl(url.getValue());
                feed.setDescription(description.getValue());
                feed.setName(name.getValue());
                Feed feedUpdated = feedBuisness.update(feed);
                showSuccessNotification("Mise à jour du flux \"" + feedUpdated.getName() + "\" réussi");
            } catch(Exception e) {
                showErrorNotification("Mise à jour du flux \"" + feed.getName() + "\" échec", e.getMessage());
            }
        });
        
        delete.addClickListener((eventClick) -> {
            try {
                feedBuisness.delete(feed.getId());
                feeds.removeItem(feed.getId());
                showSuccessNotification("Suppression du flux \"" + feed.getName() + "\" réussi");
            } catch(Exception e) {
                showErrorNotification("Suppression du flux \"" + feed.getName() + "\" échec", e.getMessage());
            }
        });
        
        return new Object[] { url, name, description, action };
    }
    
    public void showSuccessNotification(String title) {
        Notification notification = new Notification(title, Notification.Type.HUMANIZED_MESSAGE);
        notification.setDelayMsec(3000);
        notification.setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
        notification.show(getUI().getPage());
    }
    
    public void showErrorNotification(String title, String detail) {
        Notification notification = new Notification(title, detail, Notification.Type.ERROR_MESSAGE);
        notification.setDelayMsec(3000);
        notification.show(getUI().getPage());
    }
    
}
