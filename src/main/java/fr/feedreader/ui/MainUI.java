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
import com.vaadin.cdi.UIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.views.FeedItemDetailView;
import fr.feedreader.views.FeedItemView;
import fr.feedreader.views.FeedView;
import javax.inject.Inject;

/**
 *
 * @author philippe
 */
@CDIUI(value = "/v")
@Theme(value = "feedreader-dark")
public class MainUI  extends UI {
    
    @Inject
    private CDIViewProvider viewProvider;
    
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
    }
}
