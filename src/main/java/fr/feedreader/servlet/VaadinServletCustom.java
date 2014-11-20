/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.servlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author philippe
 */
@WebServlet(urlPatterns = {"/v/*", "/VAADIN/*"}, initParams = {
    @WebInitParam(name = "UIProvider", value = "com.vaadin.cdi.CDIUIProvider")
}, asyncSupported = true, loadOnStartup = 1)
@VaadinServletConfiguration(ui = fr.feedreader.ui.MainUI.class, productionMode = true, heartbeatInterval = 240)
public class VaadinServletCustom extends VaadinServlet {
    
}
