/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.feedreader;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import fr.feedreader.Witness;

/**
 *
 * @author philippe
 */
@RunWith(Arquillian.class)
public class WitnessTest {
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(Witness.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    
    @Test
    public void witness() {
        
    }
}
