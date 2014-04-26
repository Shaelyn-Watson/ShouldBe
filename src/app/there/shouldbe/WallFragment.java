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
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WallFragment extends ListFragment { 	  

			private AsyncTwitter twitter;
			private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
			private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
			private final String ACCESS_KEY = "2360041674-Z2lfohxkkx3ZCeNEldLwLP81VXk8eB6rH7PKMsc";
			private final String ACCESS_SECRET = "gDznLbH7tnYXwjaW53uqon33pKMcrIV6OYxVi2Y6nU7Zh";
			private final long SHOULDBE_TWITTER_ID = 2360041674L; // L is required for long value - not a part of ID
			ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
		  
			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
				
		        setAdapter();
			}
		  
//		  @Override  
//		  public void onListItemClick(ListView l, View v, int position, long id) {     
//		  }  
		  
		  @Override  
		  public View onCreateView(LayoutInflater inflater, ViewGroup container,  
		    Bundle savedInstanceState) {  
		   ArrayAdapter<Tweet> adapter = new ArrayAdapter<Tweet>(  
		     inflater.getContext(), android.R.layout.simple_list_item_1,  
		     tweetList);  
		   setListAdapter(adapter);  
		   return super.onCreateView(inflater, container, savedInstanceState);  
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
			
			/**
			 * Force refresh twitter feed TODO
			 */
			@Override
			public void onResume() {						
				super.onResume();
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
				
				//TODO not sorting by date correctly in list fragment feed view
				Collections.sort(tweetList, new Comparator<Tweet>() {
					public int compare(Tweet t1, Tweet t2) {
						return t1.getDate().compareTo(t2.getDate());
					}
				});
			
				final TweetAdapter<Tweet> adapter = new TweetAdapter<Tweet>(getActivity(), tweetList);
		      
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setListAdapter(adapter);
					}
				});
			}
			
//			public void openTweetActivity(View view) {
//				Intent intent = new Intent(getActivity(), TweetActivity.class);
//				intent.putExtra("sender", TwitterFeedActivity.class);
//				startActivity(intent);
//			}
}
