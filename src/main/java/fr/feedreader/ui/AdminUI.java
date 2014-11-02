/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import fr.feedreader.views.admin.FeedAdminView;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

/**
 *
 * @author philippe
 */
@CDIUI(value = "/admin/v")
@Theme(value = "feedreader-dark")
public class AdminUI  extends UI {
    
    @Inject
    private CDIViewProvider viewProvider;
    
    @Override
    public void init(VaadinRequest request) {
        System.out.println("adminViewProvider = " + viewProvider);
        if (getSession().getAttribute("MobileBoostrapListener") == null) {
            getSession().addBootstrapListener(new MobileBoostrapListener());
            getSession().setAttribute("MobileBoostrapListener", true);
        }
        
        Navigator navigator = new Navigator(this, this);
        navigator.addView("", getView(FeedAdminView.class));
    }
    
    private View getView(Class clazz) {
        BeanManager beanManager = CDI.current().getBeanManager();
        Bean<View> bean = (Bean<View>)beanManager.resolve(beanManager.getBeans(clazz));
        return (View) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
    }
}
