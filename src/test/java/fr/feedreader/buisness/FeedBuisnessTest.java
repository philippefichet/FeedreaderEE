/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import com.rometools.rome.io.FeedException;
import fr.feedreader.Bootstrap;
import fr.feedreader.Witness;
import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedItem;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fr.feedreader.junit.Assert.*;
import fr.feedreader.junit.Utils;
import java.net.URL;
import static org.junit.Assert.*;

/**
 *
 * @author philippe
 */
@RunWith(Arquillian.class)
public class FeedBuisnessTest {
    
    @Inject
    private Utils utils;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
            .addPackage("fr.feedreader.junit")
            .addPackage("fr.feedreader.buisness")
            .addPackage("fr.feedreader.hibernate")
            .addPackage("fr.feedreader.models")
            .deleteClass("fr.feedreader.buisness.TimerBuisness")
            .addClass(Witness.class)
            .addAsResource("META-INF/persistence-arquillian.xml", "META-INF/persistence.xml")
            .addAsResource("fr/feedreader/liquibase/", "fr/feedreader/liquibase/")
            .addAsResource("META-INF/services/org.hibernate.integrator.spi.Integrator", "META-INF/services/org.hibernate.integrator.spi.Integrator")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        war.addAsResource(new File("./src/test/resources/atom.atom"), "fr/feedreader/junit/atom.atom");
        war.addAsResource(new File("./src/test/resources/developpez.atom"), "fr/feedreader/junit/developpez.atom");
        war.addAsResource(new File("./src/test/resources/developpez-update.atom"), "fr/feedreader/junit/developpez-update.atom");
        war.addAsResource(new File("./src/test/resources/linuxfr.atom"), "fr/feedreader/junit/linuxfr.atom");
        war.addAsResource(new File("./src/test/resources/rss.rss"), "fr/feedreader/junit/rss.rss");
        
        try {
            return Bootstrap.addAsLibrary(war);
        } catch(Exception e) {
            return war;
        }
    }
    
    @Test
    public void addFeed() {
        assertNotNull(feedBuisness);
        Feed feed = new Feed();
        feed.setDescription("Test Description");
        feed.setName("test Name");
        feed.setUrl("http://www.developpez.com/index/atom");
        feedBuisness.add(feed);
        assertTrue("feed.getId() : " + feed.getId(), feed.getId() != null);
    }
    
    @Test
    public void getFeedAtom() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        URL url = getClass().getResource("/fr/feedreader/junit/atom.atom");
        List<FeedItem> feedItems = feedBuisness.getFeedItems(url.toString());
        int size = feedItems.size();
        assertTrue("Total : " + size + " au lieu de 20", size == 20);
        int i = 0;
        assertNotNull(feedItems.get(i).getUpdated());
        assertEquals("Tor veut créer une messagerie instantanée anonyme, une première version bêta pour le 31 mars 2014", feedItems.get(i++).getTitle());
        assertNotNull(feedItems.get(i).getSummary());
        assertStartsWith("<b>Crytek met à disposition Renderdoc", feedItems.get(i).getSummary());
        assertEquals("Crytek met à disposition Renderdoc, un outil de débogage graphique intégrant un débogueur de shaders et de multiples outils d'analyse", feedItems.get(i++).getTitle());
        assertEquals("Qt 5.3 sort en version alpha, avec des améliorations de la stabilité, des performances et facilite la première utilisation du framework", feedItems.get(i++).getTitle());
        assertEquals("Windows XP gagne encore des parts à quelques semaines de la fin de son support, Windows 8.1 progresse lentement", feedItems.get(i++).getTitle());
        assertEquals("Google intègre les menus des restaurants dans les résultats de recherche,  une fonction disponible sur mobile et PC", feedItems.get(i++).getTitle());
        assertEquals("Espionnage : Nokia divulguerait les informations privées à la police et à l'étranger, selon des enquêtes de Helsingin Sanomat", feedItems.get(i++).getTitle());
        assertEquals("Boeing veut concevoir des smartphones espions qui s'autodétruisent lorsqu'on tente de démonter le boîtier", feedItems.get(i++).getTitle());
        assertEquals("Stephen Wolfram présente le langage Wolfram, multi-paradigme et de haut niveau, basé sur plus de 30 ans de recherche", feedItems.get(i++).getTitle());
        assertEquals("ASP.NET : Microsoft dévoile le projet Helios, qui combine les avantages offerts par l'hébergement IIS et Self-Host", feedItems.get(i++).getTitle());
        assertEquals("Netflix hack day : Fitbit détecte lorsque vous dormez,  et met automatiquement vos vidéos sur pause", feedItems.get(i++).getTitle());
        assertEquals("Intégration d'Eclipse Mylyn avec Redmine et Jenkins, un tutoriel de Régis Pouiller", feedItems.get(i++).getTitle());
        assertEquals("Apple : un lancement d'iOS in the Car prévu pour la semaine prochaine ?  Trois constructeurs automobiles en auraient la primeur", feedItems.get(i++).getTitle());
        assertEquals("Telegram Messenger : le nouveau concurrent de WhatsApp ?  L'application mise sur la vitesse et la sécurité des données", feedItems.get(i++).getTitle());
        assertEquals("La génération Y peu soucieuse des données de l'entreprise, d'après une enquête comparative de Fortinet", feedItems.get(i++).getTitle());
        assertEquals("GitHub présente son éditeur de texte pour les développeurs,  Atom est une «variante spécialisée de Chromium»", feedItems.get(i++).getTitle());
        assertEquals("Windows 8 serait plus vulnérable que Windows XP,  d'après un rapport de Secunia", feedItems.get(i++).getTitle());
        assertEquals("83% de possesseurs de smartphones l'utilisent en attendant quelqu'un, d'après une étude de InMobi", feedItems.get(i++).getTitle());
        assertEquals("Google Glass : une femme du programme Explorer se fait agresser,  ses assaillants n'ont pas supporté d'être filmés", feedItems.get(i++).getTitle());
        assertEquals("Chrome 34 bêta met le cap sur le responsive design avec le support de srcset,  Google améliore au passage son API Web audio", feedItems.get(i++).getTitle());
        assertEquals("Apprentissage de Qemu/LibVirt par l'exemple, par Nicolas Hennion", feedItems.get(i++).getTitle());
    }
    
    @Test
    public void getFeedRss() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        URL url = getClass().getResource("/fr/feedreader/junit/rss.rss");
        List<FeedItem> feedItems = feedBuisness.getFeedItems(url.toString());
        int size = feedItems.size();
        assertTrue("Total : " + size + " au lieu de 20", size == 20);
        int i = 0;
        assertNotNull(feedItems.get(i).getUpdated());
        assertEquals("La notion de BOM avec Maven", feedItems.get(i).getTitle());
        assertNotNull(feedItems.get(i).getSummary());
        assertStartsWith("\n        <p>Maven est une solution de gestion de production de projets", feedItems.get(i).getSummary());
    }
    
    @Test
    public void getFeedAtomLinuxFr() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        URL url = getClass().getResource("/fr/feedreader/junit/linuxfr.atom");
        List<FeedItem> feedItems = feedBuisness.getFeedItems(url.toString());
        int size = feedItems.size();
        assertTrue("Total : " + size + " au lieu de 15", size == 15);
        int i = 0;
        assertNotNull(feedItems.get(i).getUpdated());
        assertNull(feedItems.get(i).getSummary());
        assertEquals("Apache-OFBiz s'invite dans l'usine", feedItems.get(i++).getTitle());
        assertEquals("PSES2014 - proposez vos hacks", feedItems.get(i++).getTitle());
        assertEquals("L'ERP OpenConcerto disponible en version 1.3", feedItems.get(i++).getTitle());
        assertEquals("ChiffrofÃªte Ã  Numa Paris le 29 mars 2014", feedItems.get(i++).getTitle());
        assertEquals("Install Party GNU/Linux le 29 mars 2014 Ã  Marseille", feedItems.get(i++).getTitle());
        assertEquals("Chiffrement, vie privÃ©e et logiciel libre", feedItems.get(i++).getTitle());
        assertEquals("DÃ©ploiement automatisÃ© dâ€™applications Ruby on Rails Ã  Mons le 20 mars", feedItems.get(i++).getTitle());
        assertEquals("Appel Ã  dons pour le logiciel de montage vidÃ©o Pitivi", feedItems.get(i++).getTitle());
        assertEquals("Caranille 4.5.1, un Ã©diteur de MMORPG en PHP", feedItems.get(i++).getTitle());
        assertEquals("Un DevCamp Alternc Ã  Paris du 26 au 28 mars", feedItems.get(i++).getTitle());
        assertEquals("Sortie de Beyond Linux From Scratch v7.5 (et elle est traduite !)", feedItems.get(i++).getTitle());
        assertEquals("Le Translathon 2014, session de traduction de GNOME Ã  Paris", feedItems.get(i++).getTitle());
        assertEquals("Nouveau site communautaire de la forge libre Tuleap", feedItems.get(i++).getTitle());
        assertEquals(" FOSS4G-fr 2014 - appel Ã  propositions", feedItems.get(i++).getTitle());
        assertEquals("ApÃ©ro Python/PHP Ã  Lyon le mardi 25 mars", feedItems.get(i++).getTitle());
    }
    
    @Test
    public void refresh() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        Feed diablo3 = new Feed();
        diablo3.setDescription("Diablo 3");
        diablo3.setUrl("http://eu.battle.net/d3/fr/feed/news");
        feedBuisness.add(diablo3);
        assertTrue(diablo3.getId() > 0);
        List<FeedItem> refreshFeedItems = feedBuisness.refreshFeedItems(diablo3.getId());
        assertTrue(refreshFeedItems.size() > 0);
    }
    
    @Test
    public void refreshResult() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        URL developpezUrl = getClass().getResource("/fr/feedreader/junit/developpez.atom");
        URL developpezUpdateUrl = getClass().getResource("/fr/feedreader/junit/developpez-update.atom");
        
        // Création du flux de test basé sur un fichier de test
        Feed developpez = new Feed();
        developpez.setDescription("Developpez");
        developpez.setUrl(developpezUrl.toString());
        developpez = feedBuisness.add(developpez);
        assertNotNull(developpez.toString(), developpez.getId());

        // Test de mise à jour
        List<FeedItem> refreshFeedItems = feedBuisness.refreshFeedItems(developpez.getId());
        assertTrue(refreshFeedItems.size() == 20);
        refreshFeedItems = feedBuisness.refreshFeedItems(developpez.getId());
        assertTrue(refreshFeedItems.size() == 0);

        // Vérification de la mise à jour en parallele
        Map<Feed, List<FeedItem>> feedUpdates = feedBuisness.parallelUpdateAllFeed();
        feedUpdates.forEach((Feed feed, List<FeedItem> itemsUpdate) -> {
            assertNotNull(itemsUpdate);
            assertNull(feed.getError());
            assertTrue(itemsUpdate.size() + " au lieu de 20 pour le flux : " + feed.toString(), itemsUpdate.size() == 20);
        });

        // Changement de l'url pour simulation de mise à jour
        developpez.setUrl(developpezUpdateUrl.toString());
        feedBuisness.update(developpez);

        // Vérification de la mise à jour en parallele
        feedUpdates = feedBuisness.parallelUpdateAllFeed();
        List<FeedItem> feedItemUpdated = feedUpdates.get(developpez);
        assertNotNull(feedItemUpdated);
        assertTrue(feedItemUpdated.size() == 13);
        feedItemUpdated.forEach((FeedItem feedItem) -> {
            assertNotNull(feedItem.getId());
            FeedItem tmp = feedItemBuisness.find(feedItem.getId());
            assertNotNull(tmp);
        });

        List<FeedItem> findAllPage1 = feedItemBuisness.findAll(developpez.getId(), 1);
        List<FeedItem> findAllPage2 = feedItemBuisness.findAll(developpez.getId(), 2);
        assertTrue(findAllPage1.size() == 20);
        assertTrue(findAllPage2.size() == 13);
        developpez = feedBuisness.find(developpez.getId(), true);
        assertNotNull(developpez);
        assertNotNull(developpez.getFeedItems());
        assertTrue(developpez.getFeedItems().size() == (20+13));
    }
    
        
    @Test
    public void getCountUnReadFeed() throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        Feed developpez = utils.loadDeveloppez();
        feedBuisness.refreshFeedItems(developpez.getId());
        Long count = feedBuisness.countUnread(developpez.getId());
        assertTrue(count + " au lieu de 20", count == 20L);
        System.out.println(utils);
    }
    
}
