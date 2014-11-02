/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.feedreader.ui;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapPageResponse;

/**
 *
 * @author philippefichet
 */
public class MobileBoostrapListener implements com.vaadin.server.BootstrapListener {

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        System.out.println("modifyBootstrapFragment = " + response);
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        System.out.println("modifyBootstrapPage = " + response);
        response.getDocument().head().prependElement("meta").attr("name", "viewport").attr("content", "width=device-width,initial-scale=1.0");
    }
}
