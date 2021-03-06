package fr.feedreader.buisness;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import fr.feedreader.models.Feed;
import fr.feedreader.models.FeedHasError;
import fr.feedreader.models.FeedItem;
import fr.feedreader.models.FeedUnreadCounter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class FeedBuisness {

    @PersistenceContext(unitName = "FeedReaderPU")
    private EntityManager em;
    
    @Inject
    private FeedItemBuisness feedItemBuisness;
    
    @Inject
    private FeedHasErrorBuisness feedHasErrorBuisness;
    
    private static Logger logger = LogManager.getLogger(FeedBuisness.class);
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Feed> findAll() {
        TypedQuery<Feed> query = em.createNamedQuery(Feed.findAll, Feed.class);
        return query.getResultList();
    }

    public Feed add(Feed feed) {
        em.persist(feed);
        return feed;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Feed find(Integer id) {
        Map<String, Object> hints = new HashMap<>();
        EntityGraph<?> entityGraph = em.getEntityGraph(Feed.entityGraphError);
        hints.put("javax.persistence.fetchgraph", entityGraph);
        return em.find(Feed.class, id, hints);
    }

    public Feed update(Feed feed) {
        return em.merge(feed);
    }

    public void delete(Feed feed) {
        em.remove(em.contains(feed) ? feed : em.merge(feed));
    }
    
    public void delete(Integer feedId) {
        em.remove(find(feedId));
    }

    /**
     * Met à jour les articles du flux à partir de son url
     *
     * @param id Identifiant du flux
     * @return Liste des articles mise à jour
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List<FeedItem> refreshFeedItems(Integer id) throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        Feed feed = find(id);
        List<FeedItem> updatedFeedItem = new ArrayList<>();
        if (feed != null) {
            // Reverse car insertion des plus anciens en premier pour tri par feedItem.id
            List<FeedItem> feedItems = Lists.reverse(getFeedItems(feed.getUrl()));
            for (FeedItem feedItem : feedItems) {
                TypedQuery<FeedItem> query = em.createNamedQuery(FeedItem.searchByFeedIdAndFeedItemId, FeedItem.class);
                query.setParameter("feedId", id);
                query.setParameter("feedItemId", feedItem.getFeedItemId());
                FeedItem existing = null;
                try {
                    existing = query.getSingleResult();

                    // Mise a jour d'article existant
                    if (existing.getUpdated() != null && feedItem.getUpdated() != null && existing.getUpdated().before(feedItem.getUpdated())) {
                        updatedFeedItem.add(existing);
                        existing.setReaded(false);
                    }
                    existing.setLink(feedItem.getLink());
                    existing.setSummary(feedItem.getSummary());
                    existing.setTitle(feedItem.getTitle());
                    existing.setUpdated(feedItem.getUpdated());
                    existing.setEnclosure(feedItem.getEnclosure());
                    feedItemBuisness.update(existing);
                // Nouveau articles
                } catch (NoResultException e) {
                    feedItem.setFeed(feed);
                    feedItem.setReaded(false);
                    FeedItem feedItemCreate = feedItemBuisness.create(feedItem);
                    updatedFeedItem.add(feedItemCreate);
                } catch (Exception e) {
                    logger.error("Erreur dans la récupération d'un flux " + id + ", \"" + feedItem.getFeedItemId() + "\"", e);
                    logFeedItem(existing);
                    logFeedItem(feedItem);
                }
            }
            // Mise à jour de la date de récupération du flux
            feed.setLastUpdate(new Date());
            update(feed);
        }
        return updatedFeedItem;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<FeedItem> getFeedItems(Integer id) throws ParserConfigurationException, SAXException, IOException {
        Feed feed = find(id);
        return feed.getFeedItems();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<FeedItem> getFeedItems(URI uri) throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        return getFeedItems(uri.toURL().toString());
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<FeedItem> getFeedItems(String uri) throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        logger.info("Récupération du flux \"" + uri + "\" ...");
        long startAt = System.nanoTime();
        SyndFeedInput input = new SyndFeedInput();
        XmlReader xmlreader = new XmlReader(new URI(uri).toURL());
        SyndFeed build = input.build(xmlreader);
        List<SyndEntry> syndEntries = build.getEntries();
        List<FeedItem> feedItems = syndEntries.stream().map((SyndEntry syndEntry) -> {
            String summary = syndEntry.getDescription() != null ? syndEntry.getDescription().getValue() : null;
            String enclosures = syndEntry.getEnclosures().size() > 0 ? syndEntry.getEnclosures().get(0).getUrl() : null;
            Date updated = syndEntry.getUpdatedDate() == null ? new Date() : syndEntry.getUpdatedDate();
            return new FeedItem(
                syndEntry.getUri(),
                syndEntry.getTitle(),
                syndEntry.getLink(),
                enclosures,
                summary,
                updated
            );
        }).collect(Collectors.toList());
        long endAt = System.nanoTime();
        logger.info("Récupération du flux \"" + uri + "\" en : " + (endAt - startAt) + " ns.");
        return feedItems;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Map<Integer, Long> countUnread() {
        Map<Integer, Long> counter = new HashMap<>();
        Query query = em.createNamedQuery(Feed.getUnread);
        List<FeedUnreadCounter> counters = query.getResultList();
        for (FeedUnreadCounter feedUnreadCounter : counters) {
            counter.put(feedUnreadCounter.getFeedId(), feedUnreadCounter.getCounter());
        }
        return counter;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long countUnread(Integer feedId) {
        Map<Feed, Long> counter = new HashMap<>();
        TypedQuery<Long> query = em.createQuery("SELECT count(*) FROM FeedItem fi WHERE (fi.readed = FALSE OR fi.readed IS NULL) AND fi.feed.id = :feedId", Long.class);
        query.setParameter("feedId", feedId);
        return query.getSingleResult();
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Map<String, FeedItem> getFeedItemFromFeedItemIds(Feed feed, List<String> feedItemIds) {
        if (feedItemIds.isEmpty()) {
            return Collections.emptyMap();
        } else {
            TypedQuery<FeedItem> feeditemFromNewFeed = em.createNamedQuery(FeedItem.searchByFeedIdAndFeedItemIds, FeedItem.class);
            feeditemFromNewFeed.setParameter("feedId", feed.getId());
            feeditemFromNewFeed.setParameter("feedItemIds", feedItemIds);
            return feeditemFromNewFeed.getResultList().stream().collect(
                    Collectors.toMap(
                            fi -> fi.getFeedItemId(),
                            fi -> fi
                    )
            );
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Map<Feed, List<FeedItem>> parallelUpdateAllFeed() {
        boolean debug = logger.isDebugEnabled();
        boolean info = logger.isInfoEnabled();
        if (info) {
            logger.info("parallelUpdateAllFeed");
        }
        List<Feed> feeds = findAll();
        Map<Feed, List<FeedItem>> feedsUpdated = new ConcurrentHashMap<>();
        List<FeedHasError> feedOnError = Collections.synchronizedList(new ArrayList<>());
        feeds.stream().parallel().forEach((feed) -> {
            try {
                feedsUpdated.put(feed, getFeedItems(feed.getUrl()));
                feed.setError(null);
            } catch (Exception ex) {
                logger.error("Erreur sur le flux \"" + feed.getUrl() + "\"", ex);
                feedOnError.add(new FeedHasError(feed.getId(), ex.getLocalizedMessage()));
            }
        });
        
        feedHasErrorBuisness.updateErrorOnAllFeeds(feedOnError);
        
        Map<Feed, List<FeedItem>> feedForUpdate = new HashMap<>();
        feedsUpdated.forEach((feed, newFeeds) -> {
            if (info) {
                logger.info("Comparaison avec les articles déjà présant pour \"" + feed.getName() + "\" avec \"" + newFeeds.size() + "\" article(s) trouvés.");
            }
            feedForUpdate.put(feed, new ArrayList<>());
            List<String> feedItemIds = newFeeds.stream().map(f -> f.getFeedItemId()).collect(Collectors.toList());
            Map<String, FeedItem> feedItemByFeedItemId = getFeedItemFromFeedItemIds(feed, feedItemIds);

            newFeeds.stream().forEach((newFeed) -> {
                if (debug) {
                    logger.debug(feed.getId() + " => " + newFeed.getFeedItemId());
                }
                FeedItem feedItem = feedItemByFeedItemId.get(newFeed.getFeedItemId());
                if (feedItem != null) {
                    if (feedItem.isDifferent(newFeed)) {
                        // Mise à jour du flux
                        feedItem.setEnclosure(newFeed.getEnclosure());
                        feedItem.setLink(newFeed.getLink());
                        feedItem.setSummary(newFeed.getSummary());
                        feedItem.setTitle(newFeed.getTitle());
                        feedItem.setUpdated(newFeed.getUpdated());
                        feedItemBuisness.update(feedItem);
                        if (info) {
                            logger.info("Article existant mis à jour: " + newFeed.getFeedItemId());
                        }
                    }
                    if (info) {
                        logger.info("Article existant : " + newFeed.getFeedItemId());
                    }
                } else {
                    if (info) {
                        logger.info("Nouvelle article : " + newFeed.getFeedItemId());
                    }
                    newFeed.setFeed(feed);
                    feedItemBuisness.create(newFeed);
                    List<FeedItem> newFeedsList = feedForUpdate.get(feed);
                    newFeedsList.add(newFeed);
                    feedForUpdate.put(feed, newFeedsList);
                }
            });
        });
        
        return feedForUpdate;
    }

    public Map<Feed, List<FeedItem>> updateAllFeed() {
        List<Feed> feeds = findAll();
        // Liste des nouveaux flux avec les articles
        Map<Feed, List<FeedItem>> feedsUpdated = new HashMap<>();
        for (Feed feed : feeds) {
            // Récupération de l'article le plus récent
            TypedQuery<FeedItem> lastItemQuery = em.createNamedQuery(FeedItem.findAllByFeedId, FeedItem.class);
            lastItemQuery.setParameter("feedId", feed.getId());
            lastItemQuery.setMaxResults(1);
            FeedItem feedItem = null;
            try {
                feedItem = lastItemQuery.getSingleResult();
                if (feedItem.getUpdated() != null) {
                    logger.info("Dernier flux récupérer pour \"" + feed.getName() + "\" : " + feedItem.getUpdated().toString());
                } else {
                    logger.info("Dernier flux récupérer pour \"" + feed.getName() + "\" : null");
                }

                // Récupération des de nouveau article 
                List<FeedItem> feedItems = refreshFeedItems(feed.getId());
                feedsUpdated.put(feed, feedItems);
            } catch (javax.persistence.NoResultException e) {
                logger.info("Premier flux récupérer pour \"" + feed.getName() + "\"");
                try {
                    List<FeedItem> feedItems = refreshFeedItems(feed.getId());
                    feedsUpdated.put(feed, feedItems);
                } catch (Exception e1) {
                    logger.error("Erreur lors de la récumération du premier flux pour \"" + feed.getName() + "\"", e1);
                    logFeed(feed);
                    logFeedItem(feedItem);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la mise à jour du flux : \"" + feed.getName() + "\"", e);
                logFeed(feed);
                logFeedItem(feedItem);
            }
        }
        return feedsUpdated;
    }
    
    private void logFeed(Feed feed) {
        if (feed == null) {
            logger.error("feed null");
        } else {
            logger.error("feedItem :");
            logger.error("\t - getUrl() : " + StringUtils.length(feed.getUrl()));
            logger.error("\t - getName() : " + StringUtils.length(feed.getName()));
            logger.error("\t - getDescription() : " + StringUtils.length(feed.getDescription()));
        }
    }
    
    private void logFeedItem(FeedItem feedItem) {
        if (feedItem == null) {
            logger.error("feedItem null");
        } else {
            logger.error("feedItem :");
            logger.error("\t - getTitle() : " + StringUtils.length(feedItem.getTitle()));
            logger.error("\t - getEnclosure() : " + StringUtils.length(feedItem.getEnclosure()));
            logger.error("\t - getFeedItemId() : " + StringUtils.length(feedItem.getFeedItemId()));
            logger.error("\t - getLink() : " + StringUtils.length(feedItem.getLink()));
            logger.error("\t - getSummary() : " + StringUtils.length(feedItem.getSummary()));
        }
    }
}

