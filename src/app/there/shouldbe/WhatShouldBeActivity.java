package app.there.shouldbe;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


public class WhatShouldBeActivity extends Activity {
	
	private final String TWITTER_PREF_KEY = "twitter_login";
	private final String TWITTER_LOGOUT_PREF_KEY = "twitter_logout";
	private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
	private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
	private final long SHOULDBE_TWITTER_ID = 2360041674L; // L is required for long value - not a part of ID
	private final String PREFERENCE_NAME = "twitter_oauth";
	private final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String PREF_REQUEST_TOKEN = "request_token";
    private final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    private final String TWITTER_CALLBACK_URL = "shouldbe://tweet";
    private final String URL_TWITTER_AUTH = "auth_url";
    private final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    private ProgressDialog pDialog;
    private SharedPreferences mSharedPreferences;
    private EditText txtUpdate;
    private Bundle extras;
    private Class<?> callingActivity;
    private String txtUpdateText;
    private Double shouldbeLat;
    private Double shouldbeLong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tweet);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		 
		Button tweetButton = (Button) findViewById(R.id.postButton);
		tweetButton.setOnClickListener(new postShouldBe());
		txtUpdate = (EditText) findViewById(R.id.tweetET);
		txtUpdate.addTextChangedListener(new EditTextChanged());
		txtUpdate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				txtUpdate.setHint("");
			}
		});
		mSharedPreferences = getApplicationContext().getSharedPreferences("shouldbe_prefs", MODE_PRIVATE);
		
		// Will be used for adding GeoLocation to shouldbe post
		Intent intent = getIntent();
		shouldbeLat = intent.getDoubleExtra("lat", 0.0);
		Log.d("WHATSHOULDBEACTIVITY", shouldbeLat+"");
		shouldbeLong = intent.getDoubleExtra("long", 0.0);
		Log.d("WHATSHOULDBEACTIVITY", shouldbeLong+"");
	} 

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);

		return true;
	}
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	startActivity(new Intent(this, MainMapActivity.class)); 
            return true;
        }
        if(item.getItemId() == R.id.settings){
			startActivity(new Intent(this, Settings.class)); 
			return true;
		}
        return super.onOptionsItemSelected(item);
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tweet,
					container, false);
			return rootView;
		}
	}
	
	public void postTweet(View view) {
		if (isTwitterLoggedIn()) {
			String status = "@shouldBeApp There #shouldbe " + txtUpdate.getText().toString();
			Log.d("STATUS", status);
			if (status.trim().length() > 0) {
				new updateTwitterStatus().execute(status);
			}
			else {
				Toast.makeText(this, "Please enter a status", Toast.LENGTH_LONG);
			}
		}
		else {
			Toast.makeText(this,  "Please Login to Twitter using settings", Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean isTwitterLoggedIn() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
	
	class updateTwitterStatus extends AsyncTask<String, String, String> {

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WhatShouldBeActivity.this);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Places JSON
         * */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            if (shouldbeLat != 0.0 && shouldbeLong != 0.0) {
            	status += " \n " + shouldbeLat + "," + shouldbeLong;
            	Log.d("Status", status);
            }
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(CONSUMER_KEY);
                builder.setOAuthConsumerSecret(CONSUMER_SECRET);
                 
                // Access Token 
                String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
                 
                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
                 
                // Update status
                twitter4j.Status response = twitter.updateStatus(status);
                 
                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog and show
         * the data in UI Always use runOnUiThread(new Runnable()) to update UI
         * from background thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
        	try {
        		pDialog.dismiss();
        		pDialog = null;
        	}
        	catch (Exception e) {
        		// nothing
        	}
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "#shouldbe tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                    
                    if (!txtUpdateText.isEmpty()) {
                    	Intent returnIntent = new Intent();
                    	returnIntent.putExtra("status", txtUpdateText);
                    	setResult(RESULT_OK, returnIntent);
                    }
                    WhatShouldBeActivity.this.finish();
                }
            });
        }
		
	}
	
	private class postShouldBe implements OnClickListener {

		@Override
		public void onClick(View v) {
			postTweet(v);
			
		}
		
	}
	
	private class EditTextChanged implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do nothing
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// do nothing
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// save string
			txtUpdateText = s.toString();
			Log.d("TapActivity.EditTextChanged.afterTextChanged", "s.toString() = " + s.toString());
		}
		
	}

}