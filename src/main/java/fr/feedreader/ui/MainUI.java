/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.UIScoped;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
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
@Theme(value = "feedreader")
public class MainUI  extends UI {
    
    @Inject
    private CDIViewProvider viewProvider;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Override
    public void init(VaadinRequest request) {
        
        if (getSession().getAttribute("MainBootstrapListener") == null) {
            getSession().addBootstrapListener(new MainBootstrapListener());
            getSession().setAttribute("MainBootstrapListener", true);
        }
        
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        navigator.addView("", FeedView.class);
        navigator.addView("feedItem", FeedItemView.class);
        navigator.addView("feedItemDetail", FeedItemDetailView.class);

//        VerticalLayout vl = new VerticalLayout();
//        System.out.println("viewProvider = " + viewProvider);
//        System.out.println("feedBuisness = " + feedBuisness);
//        for (int i = 0; i < 200; i++) {
//            Button b = new Button("test");
//            b.addClickListener((listener) -> {
//                System.out.println("viewProvider = " + viewProvider);
//                System.out.println("feedBuisness = " + feedBuisness);
//            });
//            vl.addComponent(b);
//        }
//        setContent(vl);
    }
}
