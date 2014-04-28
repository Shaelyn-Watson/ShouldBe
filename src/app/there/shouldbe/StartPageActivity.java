package app.there.shouldbe;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class StartPageActivity extends Activity{ 
	ImageButton loginWithTwitter;
	
	private final String CONSUMER_KEY = "7TKDKSkU8e1DiF2oLTdA";
	private final String CONSUMER_SECRET = "jRtJeN2NSXmxfAB8TV2YtS11dYrVkHQ8mG7tOxOXXw";
	private final long SHOULDBE_TWITTER_ID = 2360041674L; // L is required for long value - not a part of ID
	private final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String PREF_REQUEST_TOKEN = "request_token";
    private final String PREF_KEY_TWITTER_LOGIN = "isTwitterLoggedIn";
    private final String TWITTER_CALLBACK_URL = "shouldbe://callback_startpage";
    private final String URL_TWITTER_AUTH = "auth_url";
    private final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private final String URL_TWITTER_OAUTH_TOKEN = "oauth_token"; 
    private final String TWITTER_USER_ID = "twitter_user_id";
    private SharedPreferences mSharedPreferences;
	private ConnectionDetector cd;
	private AlertDialogManager alert = new AlertDialogManager();
	private Twitter twitter;
	private static RequestToken requestToken;
	private ProgressDialog pDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        loginWithTwitter  = (ImageButton) findViewById(R.id.login);
        mSharedPreferences = getApplicationContext().getSharedPreferences("shouldbe_prefs", MODE_PRIVATE);
        
        // if the user is already signed in, don't show this page
        if (isTwitterAlreadyLoggedIn()) {
        	Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
        	startActivity(intent);
        	this.finish();
        }
        
		loginWithTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			  new LoginWithTwitter().execute("twitter");
			}
 
		});

    }
	
	@Override 
	protected void onResume() {
		super.onResume();
		
		if (this.getIntent() != null && this.getIntent().getData() != null) {
			Uri uri = this.getIntent().getData();
			
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER); 
				String token = uri.getQueryParameter(URL_TWITTER_OAUTH_TOKEN);
				try {
					Twitter t = new TwitterFactory().getInstance();
					t.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					requestToken = new RequestToken(token, CONSUMER_SECRET);
					AccessToken accessToken = t.getOAuthAccessToken(requestToken, verifier);
					
					String atoken = accessToken.getToken();
					String secret = accessToken.getTokenSecret();
					Log.d("StartPageActivity.onResume", "token = " + atoken);
					Log.d("StartPageActivity.onResume", "secret = " + secret);
					
					Editor e = mSharedPreferences.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, atoken);
					e.putString(PREF_KEY_OAUTH_SECRET, secret);
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit();
					Toast.makeText(this, "You are signed in!", Toast.LENGTH_LONG).show();
					
					// start MainActivity
		        	Intent intent = new Intent(StartPageActivity.this, MainActivity.class);
		        	startActivity(intent);
		        	this.finish();
					
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *  Checks the shared preferences to see if the Twitter login boolean is set
	 *  @return returns true if the boolean is set, false otherwise
	 */
	public boolean isTwitterAlreadyLoggedIn() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	
	class LoginWithTwitter extends AsyncTask<String, Void, String> {
		Intent twitterIntent = null;
		Context twitterContext = null;
		 
		@Override 
		protected void onPreExecute() {
			super.onPreExecute();
            pDialog = new ProgressDialog(StartPageActivity.this);
            pDialog.setMessage("Redirecting to Twitter Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = params[0]; 
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			try {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET); 
				requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				String authURL = requestToken.getAuthenticationURL();
				twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(authURL));
				StartPageActivity.this.startActivity(twitterIntent);
			}
			catch (TwitterException e) {
				Toast.makeText(StartPageActivity.this,  "Login Failed", Toast.LENGTH_LONG).show();
				Log.e("in StartPage.OauthLogin", e.getMessage());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String s) {
			twitterIntent = StartPageActivity.this.getIntent();
			if (twitterIntent != null && twitterIntent.getData() != null) {
				Uri uri = twitterIntent.getData();
				
				if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
					String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER); 
					String token = uri.getQueryParameter(URL_TWITTER_OAUTH_TOKEN);
					try {
						Twitter t = new TwitterFactory().getInstance();
						t.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
						requestToken = new RequestToken(token, CONSUMER_SECRET);
						AccessToken accessToken = t.getOAuthAccessToken(requestToken, verifier);
						
						String atoken = accessToken.getToken();
						String secret = accessToken.getTokenSecret();
						Log.d("StartPageActivity.onResume", "token = " + atoken);
						Log.d("StartPageActivity.onResume", "secret = " + secret);
						
						Editor e = mSharedPreferences.edit();
						e.putString(PREF_KEY_OAUTH_TOKEN, atoken);
						e.putString(PREF_KEY_OAUTH_SECRET, secret);
						e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
						e.commit();
						Toast.makeText(StartPageActivity.this, "You are signed in!", Toast.LENGTH_LONG).show();
						
						// start MainActivity
			        	Intent in = new Intent(StartPageActivity.this, MainActivity.class);
			        	startActivity(in);
			        	pDialog.dismiss();
			        	StartPageActivity.this.finish();
						
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		
	}
	
}
