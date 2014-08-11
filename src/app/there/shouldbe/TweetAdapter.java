package app.there.shouldbe;

import java.util.ArrayList;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class TweetAdapter<T> extends BaseAdapter {

	private Activity activity;
	private ArrayList<T> data;
	private static LayoutInflater inflater = null;
	private Tweet tempValues = null;
	private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
	private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
	private final String ACCESS_KEY = "2360041674-Z2lfohxkkx3ZCeNEldLwLP81VXk8eB6rH7PKMsc";
	private final String ACCESS_SECRET = "gDznLbH7tnYXwjaW53uqon33pKMcrIV6OYxVi2Y6nU7Zh";
	private AsyncTwitter twitter;
	

	public TweetAdapter(Activity a, ArrayList<T> d) {
		
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setAdapter();
	}

	/******** What is the size of Passed Arraylist Size ************/
	@Override
	public int getCount() {

		if(data.size() <= 0)
			return 1;
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public TextView date;
		public TextView message;
		public TextView name;
		public ImageButton likeButton;
		public TextView likeCount;

	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		View vi = convertView;
		View listMenu;
		final ViewHolder holder;

		if(convertView == null) {

			/* Inflate file for each row */
			vi = inflater.inflate(R.layout.wall_list_item, null);
			listMenu = inflater.inflate(R.layout.post_menu, null);

			/* Main wall content */
			holder = new ViewHolder();
			holder.date = (TextView) vi.findViewById(R.id.tweet_date);
			holder.message = (TextView) vi.findViewById(R.id.tweet_message);
			/* Wall menu */
			holder.name = (TextView) vi.findViewById(R.id.tweet_name1);
			holder.likeButton = (ImageButton) vi.findViewById(R.id.wall_like_button1);
			holder.likeCount = (TextView) vi.findViewById(R.id.wall_like_count1);
			
						

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if(data.size() <= 0) {
			holder.date.setText("No Data");

		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = (Tweet) data.get(position);

			/************ Set Model values in Holder elements ***********/

			holder.date.setText(tempValues.getDate());
			holder.message.setText(tempValues.getMessage());
			holder.name.setText("@" + tempValues.getName());
			holder.likeCount.setText(String.valueOf(tempValues.getLikeCount()));
			holder.likeButton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 Tweet t = (Tweet) data.get(position);
	            	 holder.likeCount.setText(t.onFavoriteClick());
	             }
	         });

		}
		return vi;
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
}