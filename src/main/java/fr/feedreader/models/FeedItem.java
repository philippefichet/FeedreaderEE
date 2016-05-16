package fr.feedreader.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;

@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(name = FeedItem.entityGraphFeed, attributeNodes = {@NamedAttributeNode("feed")})
})
@NamedQueries({
    @NamedQuery(name = FeedItem.markToRead, query = "UPDATE FROM FeedItem fi SET fi.readed = true WHERE fi.feed.id = :feedId AND (fi.readed = false OR fi.readed = NULL)"),
    @NamedQuery(name = FeedItem.deleteFromFeedId, query = "DELETE FROM FeedItem fi WHERE fi.feed.id = :feedId"),
    @NamedQuery(name = FeedItem.searchByFeedIdAndFeedItemId, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId AND fi.feedItemId = :feedItemId"),
    @NamedQuery(name = FeedItem.searchByFeedIdAndFeedItemIds, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId AND fi.feedItemId IN (:feedItemIds)"),
    @NamedQuery(name = FeedItem.findAllByFeedId, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId ORDER BY fi.id DESC"),
    @NamedQuery(name = FeedItem.findAllByFeedIdReaded, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId AND fi.readed = :readed ORDER BY fi.id DESC"),
    @NamedQuery(name = FeedItem.countByFeedId, query = "SELECT COUNT(fi) FROM FeedItem fi WHERE fi.feed.id = :feedId"),
    @NamedQuery(name = FeedItem.countByFeedIdReaded, query = "SELECT COUNT(fi) FROM FeedItem fi WHERE fi.feed.id = :feedId AND fi.readed = :readed")
})
public class FeedItem {

    /**
     * Requête récupérant un article d'un flux
     * @param feedId Identifiant du flux
     * @raram feedItemId Idenfiant de l'article de flux à récupérer
     */
    public final static String searchByFeedIdAndFeedItemId = "fr.feedreader.models.FeedItem.searchByFeedIdAndFeedItemId";
    public final static String searchByFeedIdAndFeedItemIds = "fr.feedreader.models.FeedItem.searchByFeedIdAndFeedItemIds";
    public final static String findAllByFeedId = "fr.feedreader.models.FeedItem.findAllByFeedId";
    public final static String findAllByFeedIdReaded = "fr.feedreader.models.FeedItem.findAllByFeedIdReaded";
    public final static String countByFeedId = "fr.feedreader.models.FeedItem.count";
    public final static String countByFeedIdReaded = "fr.feedreader.models.FeedItem.countReaded";
    public final static String deleteFromFeedId = "fr.feedreader.models.FeedItem.deleteFromFeedId";
    public final static String markToRead = "fr.feedreader.models.FeedItem.markToRead";
    public final static String entityGraphFeed = "fr.feedreader.models.FeedItem.entityGraphFeed";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 512)
    private String feedItemId;
    private String title;
    @Column(length = 512)
    private String link;
    private String enclosure;

    @Column(length = 1048576)
    private String summary;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    @ManyToOne(fetch = FetchType.LAZY)
    private Feed feed;

    private Boolean readed = null;

    public FeedItem() {
    }
    
    public FeedItem(String feedItemId, String title, String link, String enclosure, String summary, Date updated) {
        this.feedItemId = feedItemId;
        this.title = title;
        this.link = link;
        this.enclosure = enclosure;
        this.summary = summary;
        this.updated = updated;
    }
    
    public String getFeedItemId() {
        return feedItemId;
    }

    public void setFeedItemId(String feedItemId) {
        this.feedItemId = feedItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Boolean getReaded() {
        if (readed == null) {
            readed = Boolean.FALSE;
        }
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FeedItem) {
            return ((FeedItem) obj).getFeedItemId().equals(getFeedItemId());
        }
        return false;
    }
    
    /**
     * Vérifie si deux objet ont une difference
     * @param otherFeedItem FeedItem avec lequel comparer
     * @return 
     */
    public boolean isDifferent(FeedItem otherFeedItem) {
        return 
            !StringUtils.equals(getEnclosure(), otherFeedItem.getEnclosure()) ||
            !StringUtils.equals(getFeedItemId(), otherFeedItem.getFeedItemId()) ||
            !StringUtils.equals(getLink(), otherFeedItem.getLink()) ||
            !StringUtils.equals(getSummary(), otherFeedItem.getSummary()) ||
            !StringUtils.equals(getTitle(), otherFeedItem.getTitle()) ||
            (getUpdated().compareTo(otherFeedItem.getUpdated()) != 0);
    }
}
