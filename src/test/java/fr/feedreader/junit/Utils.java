/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.junit;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.models.Feed;
import java.io.File;
import java.net.URL;
import javax.inject.Inject;
import static org.junit.Assert.*;

/**
 *
 * @author glopinous
 */
public class Utils {
    
    @Inject
    private FeedBuisness feedBuisness;
    
    /**
     * Ajout le flux ./src/test/resources/developpez.atom Ă  la base de donnĂ©es et le renvoie
     * Le flux contient 20 article non lu
     * @return Feed flux provenant de ./src/test/resources/developpez.atom
     */
    public Feed loadDeveloppez () {
        URL url = getClass().getResource("/fr/feedreader/junit/developpez.atom");
        System.out.println("url = " + url.toString());
        
        // Création du flux de test basé sur un fichier de test
        Feed developpez = new Feed();
        developpez.setDescription("Developpez");
        developpez.setUrl(url.toString());
        developpez = feedBuisness.add(developpez);
        return developpez;
    }
}
