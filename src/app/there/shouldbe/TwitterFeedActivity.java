package app.there.shouldbe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TwitterFeedActivity extends ListActivity {

	private ListView view;
	private ArrayList tweets;
	private ArrayAdapter<Status> adapter;
	private AsyncTwitter twitter;
	private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
	private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
	private final String ACCESS_KEY = "2360041674-Z2lfohxkkx3ZCeNEldLwLP81VXk8eB6rH7PKMsc";
	private final String ACCESS_SECRET = "gDznLbH7tnYXwjaW53uqon33pKMcrIV6OYxVi2Y6nU7Zh";
	private final long SHOULDBE_TWITTER_ID = 2360041674L; // L is required for long value - not a part of ID
	private ConnectionDetector cd;
	private AlertDialogManager alert;
	ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_feed);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        view = getListView();
        setAdapter();
        
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	this.finish();
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void setAdapter() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey(CONSUMER_KEY)
	            .setOAuthConsumerSecret(CONSUMER_SECRET)
	            .setOAuthAccessToken(ACCESS_KEY)
	            .setOAuthAccessTokenSecret(ACCESS_SECRET);
	    AsyncTwitterFactory tf = new AsyncTwitterFactory(cb.build());
		twitter = tf.getInstance();
		getTimeLine();
	}
	
	public void getTimeLine() {
		try {
			TwitterListener listener = new TwitterAdapter() {
				@Override
				public void gotUserTimeline(ResponseList<Status> statuses) {
					for(Status st : statuses) {
						tweetList.add(new Tweet((st)));
					}
				}
				
				@Override
				public void gotMentions(ResponseList<Status> statuses) {
					for(Status st: statuses) {
						tweetList.add(new Tweet((st)));
					}
					displayTwitter(tweetList);
				}

				@Override
				public void onException(TwitterException e, TwitterMethod method) {
				    if(method == TwitterMethod.HOME_TIMELINE) {
						e.printStackTrace();
					} 
				    else {
					    e.printStackTrace();
					}
				}
			};
			
			twitter.addListener(listener);
//			twitter.getUserTimeline(SHOULDBE_TWITTER_ID);
			Paging paging = new Paging();
			paging.setCount(20);
			twitter.getMentions(paging);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to get timeline " + ex.getMessage());
		}
	}
	
	private void displayTwitter(final ArrayList<Tweet> tweetList) {
		Collections.sort(tweetList, new Comparator<Tweet>() {
			public int compare(Tweet t1, Tweet t2) {
				return t1.getDate().compareTo(t2.getDate());
			}
		});
	
		final TweetAdapter<Tweet> adapter = new TweetAdapter<Tweet>(this, tweetList);
      
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setListAdapter(adapter);
			}
		});
	}
	
	public void openTweetActivity(View view) {
		Intent intent = new Intent(this, TweetActivity.class);
		startActivity(intent);
	}
}
