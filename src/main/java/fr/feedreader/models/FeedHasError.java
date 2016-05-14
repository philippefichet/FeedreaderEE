/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.models;

import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author philippefichet
 */
@Entity
@NamedQueries({
    @NamedQuery(name = FeedHasError.findAll, query = "SELECT fe FROM FeedHasError fe JOIN FETCH fe.feed")
})
@Table(name = "FEED_HAS_ERROR")
public class FeedHasError {
    public static final String findAll = "fr.feedreader.models.FeedHasError.findAll";
    
    @Id
    @Column(name = "FEED_ID")
    private Integer id;
    
    private String error;

    @OneToOne
    @JoinColumn(insertable = false, updatable = false)
    private Feed feed;

    public FeedHasError() {
    }
    public FeedHasError(Integer id, String error) {
        this.id = id;
        this.error = error;
    }
    
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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

    @Override
    public String toString() {
        return "FeedHasError{" + "id=" + id + ", error=" + error + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
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
        final FeedHasError other = (FeedHasError) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
}
