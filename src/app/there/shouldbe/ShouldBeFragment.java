package app.there.shouldbe;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ShouldBeFragment extends Fragment {
	
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
    private ImageButton shouldBeButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
	}
	
	@Override  
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,  
	    Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.activity_tweet, container, false);
		shouldBeButton = (ImageButton) view.findViewById(R.id.postTweetButton);
		shouldBeButton.setOnClickListener(new postShouldBe());
		
		txtUpdate = (EditText) view.findViewById(R.id.tweetET);
		mSharedPreferences = this.getActivity().getSharedPreferences("shouldbe_prefs", Context.MODE_PRIVATE);
		
		return view;
	}
	
	@Override
	public void onResume() {						
		super.onResume();
	}
	
	public void postTweet(View view) {
		if (isTwitterLoggedIn()) {
			String status = txtUpdate.getText().toString();
			Log.d("STATUS", status);
			if (status.trim().length() > 0) {
				new updateTwitterStatus().execute(status);
			}
			else {
				Toast.makeText(getActivity(), "Please enter a status", Toast.LENGTH_LONG);
			}
		}
		else {
			Toast.makeText(getActivity(),  "Please Login to Twitter using settings", Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean isTwitterLoggedIn() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
	
	class updateTwitterStatus extends AsyncTask<String, String, String> {

		@Override
        protected void onPreExecute() { 
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
            pDialog.dismiss();
            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "#shouldbe tweeted successfully", Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
	}
	

	
	private class postShouldBe implements OnClickListener {

		@Override
		public void onClick(View v) {
			postTweet(v);
			Intent i = new Intent(getActivity(), MainActivity.class);  //your class
		    startActivity(i);
		    getActivity().finish();
		}
		
	}


}
