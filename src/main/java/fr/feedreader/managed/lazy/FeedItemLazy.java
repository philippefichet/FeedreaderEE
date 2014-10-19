/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader.managed.lazy;

import fr.feedreader.buisness.FeedItemBuisness;
import fr.feedreader.models.FeedItem;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author philippe
 */
public class FeedItemLazy extends LazyDataModel<FeedItem>{

    private FeedItemBuisness feedItemBuisness = null;
    private Integer feedId = null;
    
    public FeedItemLazy(FeedItemBuisness feedItemBuisness, Integer feedId) {
        super();
        this.feedItemBuisness = feedItemBuisness;
        this.feedId = feedId;
    }

    @Override
    public FeedItem getRowData(String rowKey) {
        return feedItemBuisness.find(Integer.parseInt(rowKey));
    }
 
    @Override
    public Object getRowKey(FeedItem feedItem) {
        return feedItem.getId();
    }
    
    @Override
    public List<FeedItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        List<FeedItem> feedItems = feedItemBuisness.findAll(feedId, Math.floorDiv(first, 20) + 1);
        setPageSize(20);
        setRowCount(feedItemBuisness.getCount(feedId).intValue());
        return feedItems;
    }
}
