/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.views;

import com.vaadin.ui.UI;

/**
 *
 * @author glopinous
 */
public class Utils {
    public static boolean isSmall(UI ui) {
        return ui.getPage().getBrowserWindowWidth() < 601;
    }
}
