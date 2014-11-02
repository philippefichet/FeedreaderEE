/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.views.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.models.Feed;
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
    
    @PostConstruct
    private void init() {
        test = System.currentTimeMillis();
    }
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println("init = " + init);
        if(init) {
            
        } else {
            removeAllComponents();
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
            
            Table feeds = new Table();
            feeds.setWidth("100%");
            feeds.setHeightUndefined();
            feeds.setPageLength(0);
            feeds.addContainerProperty("Url", TextField.class, "");
            feeds.addContainerProperty("Nom", TextField.class, "");
            feeds.addContainerProperty("Description", TextField.class, "");
            
            List<Feed> findAll = feedBuisness.findAll();
            findAll.stream().forEach((feed) -> {
                System.out.println("feed = " + feed);
                TextField url = new TextField();
                url.setValue(feed.getUrl());
                url.setWidth("100%");
                TextField name = new TextField();
                name.setValue(feed.getName());
                name.setWidth("100%");
                TextField description = new TextField();
                description.setValue(feed.getDescription());
                description.setWidth("100%");
                feeds.addItem(new Object[] { url, name, description }, feed.getId());
            });
            
            addComponent(feeds);
        }
    }
    
}
