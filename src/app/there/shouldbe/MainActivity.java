package app.there.shouldbe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
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

}
