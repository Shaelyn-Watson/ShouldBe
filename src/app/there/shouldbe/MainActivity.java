package app.there.shouldbe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.settings: 
			 startActivity(new Intent(this, Settings.class)); 
			 return true; 
		default:
			break;
		}
		
		return false;
	}
	
	/** Called when the user clicks the Tap Location button */
	public void tapLocation(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, TapActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the General Post button */
	public void postWall(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, GeneralPostActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Twitter Feed button */
	public void openTwitterFeed(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, TwitterFeedActivity.class);
		startActivity(intent);
	}

}
