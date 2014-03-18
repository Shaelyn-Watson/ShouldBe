package app.there.shouldbe;


import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TweetEntity;

public class TwitterFeed {
	
	private Twitter twitter;
	
	public TwitterFeed() {
		twitter = new TwitterFactory().getInstance();
	}
	
	public ArrayList getTimeLine(String s) {
		ArrayList tweets = new ArrayList();
		try {
			Query query = new Query(s);
			QueryResult result = twitter.search(query);
			
			tweets = (ArrayList)result.getTweets();
			
		}
		catch(Exception ex) {
			System.out.println("Could not get query results for " + s);
		}
		
		return tweets;
	}

}
