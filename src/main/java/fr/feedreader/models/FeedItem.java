package fr.feedreader.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries({
    @NamedQuery(name = FeedItem.markToRead, query = "UPDATE FROM FeedItem fi SET fi.readed = true WHERE fi.feed.id = :feedId AND (fi.readed = false OR fi.readed = NULL)"),
    @NamedQuery(name = FeedItem.deleteFromFeedId, query = "DELETE FROM FeedItem fi WHERE fi.feed.id = :feedId"),
    @NamedQuery(name = FeedItem.searchByFeedIdAndFeedItemId, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId AND fi.feedItemId = :feedItemId"),
    @NamedQuery(name = FeedItem.findAllByFeedId, query = "SELECT fi FROM FeedItem fi WHERE fi.feed.id = :feedId ORDER BY fi.id DESC"),
    @NamedQuery(name = FeedItem.countByFeedId, query = "SELECT COUNT(fi) FROM FeedItem fi WHERE fi.feed.id = :feedId")
})
public class FeedItem {

    /**
     * Requête récupérant un article d'un flux
     * @param feedId Identifiant du flux
     * @raram feedItemId Idenfiant de l'article de flux à récupérer
     */
    public final static String searchByFeedIdAndFeedItemId = "fr.feedreader.models.FeedItem.searchByFeedIdAndFeedItemId";
    public final static String findAllByFeedId = "fr.feedreader.models.FeedItem.findAllByFeedId";
    public final static String countByFeedId = "fr.feedreader.models.FeedItem.count";
    public final static String deleteFromFeedId = "fr.feedreader.models.FeedItem.deleteFromFeedId";
    public final static String markToRead = "fr.feedreader.models.FeedItem.markToRead";

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

    @ManyToOne
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

}
