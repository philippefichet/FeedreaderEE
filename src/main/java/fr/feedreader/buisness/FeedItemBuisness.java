package fr.feedreader.buisness;

import fr.feedreader.models.Feed;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.feedreader.models.FeedItem;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityGraph;
import javax.persistence.PersistenceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class FeedItemBuisness {

    @PersistenceContext(unitName = "FeedReaderPU")
    private EntityManager em;

    private Logger logger = LogManager.getLogger(FeedItemBuisness.class);
    private Integer itemPerPage = 20;

    public FeedItem create(FeedItem feedItem) {
        try {
            em.persist(feedItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("**********************");
            logger.debug("feedItem.getEnclosure : " + feedItem.getEnclosure());
            logger.debug("feedItem.getFeedItemId : " + feedItem.getFeedItemId());
            logger.debug("feedItem.getLink : " + feedItem.getLink());
            logger.debug("feedItem.getSummary.length : " + feedItem.getSummary().length());
            logger.debug("feedItem.getSummary : " + feedItem.getSummary());
            logger.debug("feedItem.getTitle : " + feedItem.getTitle());
            logger.debug("feedItem.getId : " + feedItem.getId());
            logger.debug("feedItem.getReaded : " + feedItem.getReaded());
            logger.debug("feedItem.getUpdated : " + feedItem.getUpdated());
            logger.debug("feedItem.getFeed : " + feedItem.getFeed());
            throw e;
        }
        return feedItem;
    }

    public FeedItem update(FeedItem feedItem) {
        try {
            feedItem = em.merge(feedItem);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("**********************");
            logger.debug("feedItem.getEnclosure : " + feedItem.getEnclosure());
            logger.debug("feedItem.getFeedItemId : " + feedItem.getFeedItemId());
            logger.debug("feedItem.getLink : " + feedItem.getLink());
            logger.debug("feedItem.getSummary.length : " + feedItem.getSummary().length());
            logger.debug("feedItem.getSummary : " + feedItem.getSummary());
            logger.debug("feedItem.getTitle : " + feedItem.getTitle());
            logger.debug("feedItem.getId : " + feedItem.getId());
            logger.debug("feedItem.getReaded : " + feedItem.getReaded());
            logger.debug("feedItem.getUpdated : " + feedItem.getUpdated());
            logger.debug("feedItem.getFeed : " + feedItem.getFeed());
            throw e;
        }
        return feedItem;
    }

    public FeedItem setReaded(Integer feedItemId, Boolean readed) {
        FeedItem feedItem = find(feedItemId);
        feedItem.setReaded(readed);
        return update(feedItem);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public FeedItem find(Integer feedItemId) {
        Map<String, Object> hints = new HashMap<>();
        EntityGraph<?> entityGraph = em.getEntityGraph(FeedItem.entityGraphFeed);
        hints.put("javax.persistence.fetchgraph", entityGraph);
        return em.find(FeedItem.class, feedItemId, hints);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long getCount(Integer feedId) {
        TypedQuery<Long> query = em.createNamedQuery(FeedItem.countByFeedId, Long.class);
        query.setParameter("feedId", feedId);
        return query.getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long getCount(Integer feedId, boolean noReadedOnly) {
        TypedQuery<Long> query = em.createNamedQuery(FeedItem.countByFeedIdReaded, Long.class);
        query.setParameter("feedId", feedId);
        query.setParameter("readed", noReadedOnly);
        return query.getSingleResult();
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long getTotalPage(Integer feedId) {
        return (getCount(feedId) / itemPerPage) + 1;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long getTotalPage(Integer feedId, boolean noReadedOnly) {
        return (getCount(feedId, noReadedOnly) / itemPerPage) + 1;
    }

    /**
     * Récupére tous les article d'un flux pour une page donnée
     * @param feedId Identifiant du flux
     * @param page Page demander
     * @return Liste des articles pour la page demander
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<FeedItem> findAll(Integer feedId, Integer page) {
        TypedQuery<FeedItem> query = em.createNamedQuery(FeedItem.findAllByFeedId, FeedItem.class);
        query.setParameter("feedId", feedId);
        query.setMaxResults(itemPerPage);
        query.setFirstResult((page - 1) * itemPerPage);
        return query.getResultList();
    }

    /**
     * Récupére tous les article lu ou non lu d'un flux pour une page donnée
     * @param feedId Identifiant du flux
     * @param page Page demander
     * @param noReadedOnly récupération des article non lu ou lu
     * @return Liste des articles pour la page demander
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<FeedItem> findAll(Integer feedId, Integer page, boolean noReadedOnly) {
        TypedQuery<FeedItem> query = em.createNamedQuery(FeedItem.findAllByFeedIdReaded, FeedItem.class);
        query.setParameter("feedId", feedId);
        query.setParameter("readed", noReadedOnly);
        query.setMaxResults(itemPerPage);
        query.setFirstResult((page - 1) * itemPerPage);
        return query.getResultList();
    }

    /**
     * Supprime les articles
     * @param id Identifiant du flux dont les articles doivent être supprimer
     * @return Nombre d'article supprimer
     */
    public int clean(Integer id) {
        return em.createNamedQuery(FeedItem.deleteFromFeedId).setParameter("feedId", id).executeUpdate();
    }

    public int maskAllFeedItemToRead(Integer feedId) {
        return em.createNamedQuery(FeedItem.markToRead).setParameter("feedId", feedId).executeUpdate();
    }

}
