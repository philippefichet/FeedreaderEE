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
                em.remove(fe);
            }
        });
        
        feedOnError.forEach((fe) -> {
            em.merge(fe);
        });
    }
}
