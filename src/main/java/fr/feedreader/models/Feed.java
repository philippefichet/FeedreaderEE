package fr.feedreader.models;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(name = Feed.entityGraphError, attributeNodes = {@NamedAttributeNode("error")}),
    @NamedEntityGraph(name = Feed.entityGraphFeedItems, attributeNodes = {@NamedAttributeNode("feedItems")})
})
@NamedQueries({
    @NamedQuery(name = Feed.findAll, query = "SELECT f FROM Feed f LEFT JOIN FETCH f.error"),
    @NamedQuery(name = Feed.getUnread, query = "SELECT NEW fr.feedreader.models.FeedUnreadCounter(fi.feed.id, count(fi)) FROM FeedItem fi WHERE (fi.readed = FALSE OR fi.readed IS NULL) GROUP BY fi.feed.id")
})
public class Feed {

    public static final String findAll = "fr.feedreader.models.Feed.findAll";
    public static final String getUnread = "fr.feedreader.models.Feed.getUnread";
    public static final String entityGraphError = "fr.feedreader.models.Feed.entityGraphError";
    public static final String entityGraphFeedItems = "fr.feedreader.models.Feed.entityGraphFeedItems";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String url;
    private String description;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL)
    @OrderBy(value = "id DESC")
    private List<FeedItem> feedItems;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    private Boolean enable = Boolean.TRUE;
    
    @OneToOne(mappedBy = "feed", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FeedHasError error;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FeedItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean isEnable() {
        if (enable == null) {
            return Boolean.FALSE;
        }
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Feed other = (Feed) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Feed{" + "id=" + id + ", name=" + name + ", url=" + url + ", description=" + description + ", lastUpdate=" + lastUpdate + ", enable=" + enable + '}';
    }

    public FeedHasError getError() {
        return error;
    }

    public void setError(FeedHasError error) {
        this.error = error;
    }
}
