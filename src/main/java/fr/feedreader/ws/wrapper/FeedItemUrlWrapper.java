/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

import fr.feedreader.models.FeedItem;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author philippe
 */
public class FeedItemUrlWrapper {
    private String toRead = null;
    private String reverseReaded = null;

    public FeedItemUrlWrapper(String contextPath, FeedItem feedItem) {
        this.toRead = contextPath + "/ws/feedItem/" + feedItem.getId() + "/readed/true";
        this.reverseReaded = contextPath + "/ws/feedItem/" + feedItem.getId() + "/readed/" + (feedItem.getReaded() ? Boolean.FALSE.toString() : Boolean.TRUE.toString());
    }

    public String getToRead() {
        return toRead;
    }

    public String getReverseReaded() {
        return reverseReaded;
    }
}
