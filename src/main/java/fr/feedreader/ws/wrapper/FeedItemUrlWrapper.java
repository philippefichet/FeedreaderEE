/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.ws.wrapper;

/**
 *
 * @author philippe
 */
public class FeedItemUrlWrapper {
    private String toRead = null;
    private String reverseReaded = null;

    public FeedItemUrlWrapper(String toRead, String reverseReaded) {
        this.toRead = toRead;
        this.reverseReaded = reverseReaded;
    }

    public String getToRead() {
        return toRead;
    }

    public String getReverseReaded() {
        return reverseReaded;
    }
}
