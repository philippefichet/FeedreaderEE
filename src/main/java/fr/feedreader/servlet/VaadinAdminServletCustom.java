/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.servlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.cdi.server.VaadinCDIServlet;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author philippe
 */
@WebServlet(urlPatterns = {"/admin/v/*"}, asyncSupported = true, loadOnStartup = 1)
@VaadinServletConfiguration(ui = fr.feedreader.ui.AdminUI.class, productionMode = true)
public class VaadinAdminServletCustom extends VaadinCDIServlet {
    
}
