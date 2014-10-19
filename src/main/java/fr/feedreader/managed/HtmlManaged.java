/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.managed;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author philippe
 */
@ManagedBean
@RequestScoped
public class HtmlManaged {
    public String badge(Object o) {
        if (o != null) {
            return "<span class=\"badge\">" + o.toString() + "</span>";
        } else {
            return "";
        }
    }
}
