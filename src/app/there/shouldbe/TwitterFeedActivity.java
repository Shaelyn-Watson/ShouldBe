package app.there.shouldbe;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TwitterFeedActivity extends ListActivity {

	private ListView view;
	private ArrayList tweets;
	private ArrayAdapter<String> adapter;
	private TwitterFeed timeline = new TwitterFeed();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_feed);
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.d("TIMELINE", timeline.toString());
        
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
		tweets = timeline.getTimeLine("shouldbeApp");
		Log.d("TWEETS", tweets.toString());
		
		adapter = new ArrayAdapter(this, R.layout.list_item, tweets);
		setListAdapter(adapter);
	}
}
