/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.junit;

import static org.junit.Assert.*;

/**
 *
 * @author glopinous
 */
public class Assert {
    public static void assertStartsWith(String start, String complete) {
        String check = complete.substring(0, start.length());
        assertEquals(start, check);
    }
}
