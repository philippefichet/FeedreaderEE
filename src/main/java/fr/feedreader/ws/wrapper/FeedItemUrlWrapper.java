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

    public FeedItemUrlWrapper(HttpServletRequest request, FeedItem feedItem) {
        this.toRead = request.getContextPath() + "/ws/feedItem/" + feedItem.getId() + "/readed/true";
        this.reverseReaded = request.getContextPath() + "/ws/feedItem/" + feedItem.getId() + "/readed/" + (feedItem.getReaded() ? Boolean.FALSE.toString() : Boolean.TRUE.toString());
    }

    public String getToRead() {
        return toRead;
    }

    public String getReverseReaded() {
        return reverseReaded;
    }
}
