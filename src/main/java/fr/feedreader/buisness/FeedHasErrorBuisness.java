/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.buisness;

import fr.feedreader.models.FeedHasError;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author philippefichet
 */
@Stateless
public class FeedHasErrorBuisness {
    @PersistenceContext(unitName = "FeedReaderPU")
    private EntityManager em;
    
    @Inject
    private FeedBuisness feedBuisness;
    
    public void updateErrorOnAllFeeds(List<FeedHasError> feedOnError) {
        TypedQuery<FeedHasError> feedsOnError = em.createNamedQuery(FeedHasError.findAll, FeedHasError.class);
        feedsOnError.getResultList().forEach((fe) -> {
            if (!feedOnError.contains(fe)) {
                LogManager.getLogger(getClass()).debug("Suppression de l'erreur du flux " + fe.getId() + " qui était en erreur \"" + fe.getError() + "\"");
                em.remove(fe);
            }
        });
        
        feedOnError.forEach((fe) -> {
            LogManager.getLogger(getClass()).debug("Ajout de l'erreur du flux " + fe.getId() + " qui était en erreur \"" + fe.getError() + "\"");
            em.merge(fe);
        });
    }
}
