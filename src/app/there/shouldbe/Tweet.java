package app.there.shouldbe;

import android.util.Log;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class Tweet {
   private String name;
   private String message;
   private String date;
   private long id;
   private int favoriteCount;
   private Status status;
   private AsyncTwitter twitter;
   private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
	private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
	private final String ACCESS_KEY = "2360041674-Z2lfohxkkx3ZCeNEldLwLP81VXk8eB6rH7PKMsc";
	private final String ACCESS_SECRET = "gDznLbH7tnYXwjaW53uqon33pKMcrIV6OYxVi2Y6nU7Zh";

   public Tweet(Status st) 
   {
      id = st.getId();
      name = st.getUser().getScreenName();
      message = st.getText();
      date = st.getCreatedAt().toString().substring(0, 10);
      favoriteCount = st.getFavoriteCount();
      status=st;
      
      setAdapter();
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
	}
   
   public void setName(String _n){name = _n;}
   public void setMessage(String _m){message = _m;}
   public void setDate(String _d){date = _d;}
   public void setLikeCount(Integer _l){}

   public long getId(){return id;}
   public String getName(){return name;}
   public String getMessage(){return message;}
   public String getDate(){return date;}
   public Integer getLikeCount(){return status.getFavoriteCount();} 
   
   @Override
   public String toString() {
	   return id + " " + name + " " + message + " " + date;
   }
   
   public String onFavoriteClick(){
	// Perform action on click
   	 Log.d("*LIKEBUTTON*", "like button clicked on " + this.getMessage());
  	 twitter.createFavorite(this.getId());
  	 String count = String.valueOf(this.getLikeCount()+1);
  	 Log.d("LIKEBUTTON~~", "count = " + count);
  	 return count;
   }
}