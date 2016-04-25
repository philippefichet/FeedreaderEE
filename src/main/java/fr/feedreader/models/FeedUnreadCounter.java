package fr.feedreader.models;

public class FeedUnreadCounter {

    private Integer feedId;
    private Long counter;

    public FeedUnreadCounter(Integer feedId, Long counter) {
        super();
        this.feedId = feedId;
        this.counter = counter;
    }

    public Integer getFeedId() {
        return feedId;
    }

    public Long getCounter() {
        return counter;
    }

}
