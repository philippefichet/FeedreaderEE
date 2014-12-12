/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.junit;

import fr.feedreader.buisness.FeedBuisness;
import fr.feedreader.models.Feed;
import java.io.File;
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
     * Ajout le flux ./src/test/resources/developpez.atom à la base de données et le renvoie
     * Le flux contient 20 article non lu
     * @return Feed flux provenant de ./src/test/resources/developpez.atom
     */
    public Feed loadDeveloppez () {
        // Vérification du fichier de test
        File developpezFile = new File("./src/test/resources/developpez.atom");
        assertTrue(developpezFile.exists());
        assertTrue(developpezFile.isFile());
        
        // Création du flux de test basé sur un fichier de test
        Feed developpez = new Feed();
        developpez.setDescription("Developpez");
        developpez.setUrl(developpezFile.toURI().toString());
        developpez = feedBuisness.add(developpez);
        return developpez;
    }
}
