package app.there.shouldbe;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class Settings extends PreferenceActivity {
	
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
    private final String TWITTER_CALLBACK_URL = "shouldbe://callback_settings";
    private final String URL_TWITTER_AUTH = "auth_url";
    private final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    private final String TWITTER_USER_ID = "twitter_user_id";
	
    private Twitter twitter;
	private static RequestToken requestToken;
	private SharedPreferences mSharedPreferences;
	
	private ConnectionDetector cd;
	private Preference twitterPref;
	private Preference twitterLogOutPref;
	private PreferenceScreen mPrefScreen;
	private AlertDialogManager alert = new AlertDialogManager();
	
    
	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		getPreferenceManager().setSharedPreferencesName("shouldbe_prefs"); 
		addPreferencesFromResource(R.xml.preferences);
		
		cd = new ConnectionDetector(this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(this, "Internet Connection Error", "Please establish Internet connection", false);
			return;
		}
		
		mSharedPreferences = getApplicationContext().getSharedPreferences("shouldbe_prefs", MODE_PRIVATE); 
		
		mPrefScreen = getPreferenceScreen();
		twitterPref = findPreference(TWITTER_PREF_KEY);
		twitterLogOutPref = findPreference(TWITTER_LOGOUT_PREF_KEY);
		
		if (!isTwitterAlreadyLoggedIn()) {
			mPrefScreen.removePreference(twitterLogOutPref);
			mPrefScreen.addPreference(twitterPref);
		}
		else {
			mPrefScreen.removePreference(twitterPref);
			mPrefScreen.addPreference(twitterLogOutPref);
		}
		
		
		twitterPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String key = preference.getKey();
				if (key.equals(TWITTER_PREF_KEY)) {
					OAuthLogin();
					return true;
				}
				return false;
			} 
		});
		
		twitterLogOutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				String key = preference.getKey();
				if (key.equals(TWITTER_LOGOUT_PREF_KEY)) {
					logoutFromTwitter();
					return true;
				}
				return false;
			}
		});
		
	}
	
	public void OAuthLogin() {
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
			String authURL = requestToken.getAuthenticationURL();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
		}
		catch (TwitterException e) {
			Toast.makeText(this,  "Login Failed", Toast.LENGTH_LONG).show();
			Log.e("in Settings.OauthLogin", e.getMessage());
		}
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
					Log.d("Settings.onResume", "token = " + atoken);
					Log.d("Settings.onResume", "secret = " + secret);
					
					Editor e = mSharedPreferences.edit();
					e.putString(PREF_KEY_OAUTH_TOKEN, atoken);
					e.putString(PREF_KEY_OAUTH_SECRET, secret);
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit();
					Toast.makeText(this, "You are signed in!", Toast.LENGTH_LONG).show();
					mPrefScreen.addPreference(twitterLogOutPref);
					mPrefScreen.removePreference(twitterPref);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		try {
			String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			String token = accessToken.getToken();
			String secret = accessToken.getTokenSecret();
			Log.d("Settings.onNewIntent", "token = " + token);
			Log.d("Settings.onNewIntent", "secret = " + secret);
			
			Editor e = mSharedPreferences.edit();
			e.putString(PREF_KEY_OAUTH_TOKEN, token);
			e.putString(PREF_KEY_OAUTH_SECRET, secret);
			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			e.commit();
		}
		catch (Exception e) {
			Log.e("Settings.onNewIntent", e.getMessage());
		}
	}
	
	/**
	 *  Checks the shared preferences to see if the Twitter login boolean is set
	 *  @return returns true if the boolean is set, false otherwise
	 */
	public boolean isTwitterAlreadyLoggedIn() {
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}
	
	/**
	 *  Logs the user out of Twitter <br>
	 *  Deletes all stored information for the user <br>
	 *  Enables the login preference
	 *  <br> Disables the log out preference 
	 */
	private void logoutFromTwitter() {
		// clear shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.putBoolean(PREF_KEY_TWITTER_LOGIN, false); 
		e.commit();
		mPrefScreen.removePreference(twitterLogOutPref);
		mPrefScreen.addPreference(twitterPref);
	}
}
