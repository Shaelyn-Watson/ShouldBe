package app.there.shouldbe;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class Settings extends PreferenceActivity {
	
	private final String TWITTER_PREF_KEY = "twitter_login";
	private final String CONSUMER_KEY = "dKC1yliVp7K7jLfdNWkKg";
	private final String CONSUMER_SECRET = "RyN6VkkM6sue9mwtjLKTdLDc22YdPesUpkfEl5qBYM";
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
	
    private AsyncTwitter twitter;
	private RequestToken requestToken;
	private SharedPreferences mSharedPreferences;
	private String reqToken;
	
	private ConnectionDetector cd;
	private Preference twitterPref;
	private AlertDialogManager alert = new AlertDialogManager();
	
    
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		getPreferenceManager().setSharedPreferencesName("shouldbe_prefs"); 
		addPreferencesFromResource(R.xml.preferences);
		final SharedPreferences prefs = getSharedPreferences("shouldbe_prefs", MODE_PRIVATE); 
		
		cd = new ConnectionDetector(this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(this, "Internet Connection Error", "Please establish Internet connection", false);
			return;
		}
		
		mSharedPreferences = this.getSharedPreferences("shouldbe_prefs", 0);
		reqToken = mSharedPreferences.getString(PREF_REQUEST_TOKEN, ""); // retrieve request token 
		
		twitterPref = findPreference("twitter_login");
		
		twitterPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String key = preference.getKey();
				if (key.equals(TWITTER_PREF_KEY)) {
					loginWithTwitter();
					return true;
				}
				return false;
			}
		});
		
		if (!isTwitterAlreadyLoggedIn()) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey(CONSUMER_KEY)
            	.setOAuthConsumerSecret(CONSUMER_SECRET);
			AsyncTwitterFactory tf = new AsyncTwitterFactory(cb.build());
			twitter = tf.getInstance();
			
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth Verifier
				final String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(reqToken, verifier);
					twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					twitter.setOAuthAccessToken(accessToken);
					Editor e = mSharedPreferences.edit();
					
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.remove(PREF_REQUEST_TOKEN);
					e.commit(); // save changes
					
					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
				}
				catch(Exception ex) {
					ex.printStackTrace();
					Log.e("Twitter Login Error", "> " + ex.getMessage());
				}
			}
		}
		
		
	}
	
	
	public void loginWithTwitter() {
		//check if already logged in
		if (!isTwitterAlreadyLoggedIn()) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setOAuthConsumerKey(CONSUMER_KEY)
            	.setOAuthConsumerSecret(CONSUMER_SECRET);
			AsyncTwitterFactory tf = new AsyncTwitterFactory(cb.build());
			twitter = tf.getInstance();
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run () {
					try {
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						
						Editor e = mSharedPreferences.edit();
						e.putString(PREF_REQUEST_TOKEN, requestToken.getToken()); // save current request token
						e.commit(); // save changes
						
						Settings.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			thread.start();
			
		}
		else {
			Toast.makeText(getApplicationContext(), "Already logged into Twitter!", Toast.LENGTH_LONG).show();
		}
	}
	
	public boolean isTwitterAlreadyLoggedIn() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
}
