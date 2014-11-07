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
import fr.feedreader.models.FeedItem;
import fr.feedreader.models.FeedUnreadCounter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
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
    
    private static Logger logger = LogManager.getLogger(FeedBuisness.class);
    
    public List<Feed> findAll() {
        TypedQuery<Feed> query = em.createNamedQuery(Feed.findAll, Feed.class);
        return query.getResultList();
    }

    public Feed add(Feed feed) {
        em.persist(feed);
        return feed;
    }

    public Feed find(Integer id) {
        return em.find(Feed.class, id);
    }

    public Feed find(Integer id, boolean getFeedItems) {
        Feed feed = find(id);
        if (getFeedItems) {
            feed.getFeedItems().size();
        }
        return feed;
    }

    public Feed update(Feed feed) {
        return em.merge(feed);
    }

    public void delete(Feed feed) {
        em.remove(feed);
    }
    
    public void delete(Integer feedId) {
        em.remove(find(feedId));
    }

    /**
     * Met à jour les articles du flux à partir de son url
     *
     * @param em Unité de persistance
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
                    logger.error("Erreur dans la récupération d'un flux " + id + ", \"" + feedItem.getFeedItemId() + "\"");
                    logger.error(e.getLocalizedMessage());
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

    public List<FeedItem> getFeedItems(Integer id) throws ParserConfigurationException, SAXException, IOException {
        Feed feed = find(id);
        return feed.getFeedItems();
    }
    public List<FeedItem> getFeedItems(URI uri) throws IOException, IllegalArgumentException, FeedException, URISyntaxException {
        return getFeedItems(uri.toURL().toString());
    }

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

    public Map<Feed, Long> countUnread() {
        Map<Feed, Long> counter = new HashMap<>();
        Query query = em.createQuery("SELECT NEW fr.feedreader.models.FeedUnreadCounter(fi.feed, count(fi)) FROM FeedItem fi WHERE (fi.readed = FALSE OR fi.readed IS NULL) GROUP BY fi.feed.id");
        List<FeedUnreadCounter> counters = query.getResultList();
        for (FeedUnreadCounter feedUnreadCounter : counters) {
            counter.put(feedUnreadCounter.getFeed(), feedUnreadCounter.getCounter());
        }
        return counter;
    }

    public Long countUnread(Integer feedId) {
        Map<Feed, Long> counter = new HashMap<>();
        TypedQuery<Long> query = em.createQuery("SELECT count(*) FROM FeedItem fi WHERE (fi.readed = FALSE OR fi.readed IS NULL) AND fi.feed.id = :feedId", Long.class);
        query.setParameter("feedId", feedId);
        return query.getSingleResult();
    }
    
    public Map<Feed, List<FeedItem>> parallelUpdateAllFeed() {
        if (logger.isInfoEnabled()) {
            logger.info("parallelUpdateAllFeed");
        }
        List<Feed> feeds = findAll();
        Map<Feed, List<FeedItem>> feedsUpdated = new ConcurrentHashMap<>();
        feeds.stream().parallel().forEach((feed) -> {
            try {
                feedsUpdated.put(feed, getFeedItems(feed.getUrl()));
            } catch (IOException ex) {
                logger.fatal(ex);
            } catch (IllegalArgumentException ex) {
                logger.fatal(ex);
            } catch (FeedException ex) {
                logger.fatal(ex);
            } catch (URISyntaxException ex) {
                logger.fatal(ex);
            }
        });
        
        Map<Feed, List<FeedItem>> feedForUpdate = new HashMap<>();
        feedsUpdated.forEach((feed, newFeeds) -> {
            feedForUpdate.put(feed, new ArrayList<>());

            newFeeds.stream().forEach((newFeed) -> {
                TypedQuery<FeedItem> createNamedQuery = em.createNamedQuery(FeedItem.searchByFeedIdAndFeedItemId, FeedItem.class);
                createNamedQuery.setParameter("feedId", feed.getId());
                createNamedQuery.setParameter("feedItemId", newFeed.getFeedItemId());
                try {
                    FeedItem feedItem = createNamedQuery.getSingleResult();
                    // Mise à jour du flux
                    feedItem.setEnclosure(newFeed.getEnclosure());
                    feedItem.setLink(newFeed.getLink());
                    feedItem.setSummary(newFeed.getSummary());
                    feedItem.setTitle(newFeed.getTitle());
                    feedItem.setUpdated(newFeed.getUpdated());
                    feedItemBuisness.update(feedItem);
                    logger.info("Article existant : " + newFeed.getFeedItemId());
                } catch(NoResultException e) {
                    logger.info("Nouvelle article : " + newFeed.getFeedItemId());
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
                    logger.error("Erreur lors de la récumération du premier flux pour \"" + feed.getName() + "\"");
                    logger.error(e1.getLocalizedMessage());
                    logFeed(feed);
                    logFeedItem(feedItem);
                }
            } catch (Exception e) {
                logger.error("Erreur lors de la mise à jour du flux : \"" + feed.getName() + "\"");
                logger.error(e.getLocalizedMessage());
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

