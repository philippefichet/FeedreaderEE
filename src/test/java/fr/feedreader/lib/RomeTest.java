/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.lib;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.junit.Test;
import static fr.feedreader.junit.Assert.*;
import static org.junit.Assert.*;

/**
 *
 * @author glopinous
 */
public class RomeTest {

    @Test
    public void descriptionAtomTest() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        File atomFile = new File("./src/test/resources/atom.atom");
        SyndFeedInput input = new SyndFeedInput();
        XmlReader xmlreader = new XmlReader(atomFile.toURI().toURL());
        SyndFeed build = input.build(xmlreader);
        assertStartsWith("atom", build.getFeedType());
        int size = build.getEntries().size();
        assertTrue("Total : " + size + " au lieu de 20", size == 20);
        int i = 0;
        assertEquals("Tor veut créer une messagerie instantanée anonyme, une première version bêta pour le 31 mars 2014", build.getEntries().get(i++).getTitle());
        assertEquals("Crytek met à disposition Renderdoc, un outil de débogage graphique intégrant un débogueur de shaders et de multiples outils d'analyse", build.getEntries().get(i).getTitle());
        assertStartsWith("<b>Crytek met à disposition Renderdoc", build.getEntries().get(i).getDescription().getValue());
    }
    
    @Test
    public void descriptionRssTest() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        File atomFile = new File("./src/test/resources/rss.rss");
        SyndFeedInput input = new SyndFeedInput();
        XmlReader xmlreader = new XmlReader(atomFile.toURI().toURL());
        SyndFeed build = input.build(xmlreader);
        assertStartsWith("rss", build.getFeedType());
        int size = build.getEntries().size();
        assertTrue("Total : " + size + " au lieu de 20", size == 20);
        int i = 0;
        assertEquals("La notion de BOM avec Maven", build.getEntries().get(i).getTitle());
        assertStartsWith("\n        <p>Maven est une solution de gestion de production de projets", build.getEntries().get(i).getDescription().getValue());
    }
    
    @Test
    public void linuxFrTest() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        File atomFile = new File("./src/test/resources/linuxfr.atom");
        SyndFeedInput input = new SyndFeedInput();
        XmlReader xmlreader = new XmlReader(atomFile.toURI().toURL());
        SyndFeed build = input.build(xmlreader);
        assertStartsWith("atom", build.getFeedType());
        int size = build.getEntries().size();
        assertTrue("Total : " + size + " au lieu de 20", size == 15);
        int i = 0;
        assertNull(build.getEntries().get(i).getDescription());
        assertEquals("Apache-OFBiz s'invite dans l'usine", build.getEntries().get(i++).getTitle());
    }
    
    @Test
    public void descriptionDeveloppezTest() throws MalformedURLException, IOException, IllegalArgumentException, FeedException {
        File atomFile = new File("./src/test/resources/developpez-2014-10-08.atom");
        SyndFeedInput input = new SyndFeedInput();
        XmlReader xmlreader = new XmlReader(atomFile.toURI().toURL());
        SyndFeed build = input.build(xmlreader);
        assertStartsWith("atom", build.getFeedType());
        int size = build.getEntries().size();
        assertTrue("Total : " + size + " au lieu de 20", size == 20);
        int i = 0;
        assertEquals("Introduction aux tests unitaires JUnit4 avec Spring, un tutoriel de Régis Pouiller", build.getEntries().get(i++).getTitle());
        assertEquals("Shellshock : Yahoo! Et WinZip auraient été victimes d'attaques par des pirates, Yahoo! dément", build.getEntries().get(i).getTitle());
        assertStartsWith("<b>Shellshock : Yahoo! Et WinZip auraient été victimes", build.getEntries().get(i).getDescription().getValue());
    }
}
