package app.there.shouldbe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends FragmentActivity {

	  private static final int NUM_PAGES = 2;
	  private ViewPager mPager;  //The ViewPager that hosts the section contents

	  private ScreenSlidePagerAdapter pagerAdapter;
	  private LinearLayout topLevelLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_overlay);

		Log.d("mainOnCreate", "new main activity");
		
	    // Instantiate a ViewPager and a PagerAdapter.
		pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(pagerAdapter);

	    topLevelLayout = (LinearLayout) findViewById(R.id.top_layout);
        
       if (isFirstTime()) {
        	topLevelLayout.setVisibility(View.INVISIBLE);
        }
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
		if(item.getItemId() == R.id.settings){
			startActivity(new Intent(this, Settings.class)); 
			return true;
		}
		if(item.getItemId() == R.id.mapActionButton){
			startActivity(new Intent(this, MainMapActivity.class)); 
			return true;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
	    if (mPager.getCurrentItem() == 0) {
	        // If the user is currently looking at the first step, allow the system to handle the
	        // Back button. This calls finish() on this activity and pops the back stack.
	        super.onBackPressed();
	    } else {
	        // Otherwise, select the previous step.
	    	//Toast.makeText(MainActivity.this, " back to old frag " + String.valueOf(mPager.getCurrentItem() - 1), Toast.LENGTH_SHORT).show();
	        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
	    }
	}

	/**
	 * A simple pager adapter that represents ScreenSlidePageFragment objects, in
	 * sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
	    public ScreenSlidePagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	    	if(position == 0){
	    		//wall activity
	    		return new WallFragment();
	    	}
	    	else{
	    		return new WhatShouldBeFragment();
	    	}
	    }

	    @Override
	    public int getCount() {
	        return NUM_PAGES;
	    }
	}

	 private boolean isFirstTime() {
	     SharedPreferences preferences = getSharedPreferences("shouldbe_prefs", MODE_PRIVATE);
	     boolean ranBefore = preferences.getBoolean("FirstTime", false);
	     if (!ranBefore) {

	         Editor editor = preferences.edit();
	         editor.putBoolean("FirstTime", true); // set to false if twitter not logged in
	         editor.commit();
	         topLevelLayout.setVisibility(View.VISIBLE);
	         topLevelLayout.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				topLevelLayout.setVisibility(View.INVISIBLE);
				return false;
			}

         });
     

		}
		return ranBefore;

	}


}