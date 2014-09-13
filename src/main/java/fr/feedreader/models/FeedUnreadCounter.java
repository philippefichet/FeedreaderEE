package fr.feedreader.models;

import javax.persistence.Entity;

public class FeedUnreadCounter {
	private Feed feed;
	private Long counter;
	
	public FeedUnreadCounter(Feed feed, Long counter) {
		super();
		this.feed = feed;
		this.counter = counter;
	}
	public Feed getFeed() {
		return feed;
	}
	public Long getCounter() {
		return counter;
	}
	
}
