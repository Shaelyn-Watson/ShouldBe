package app.there.shouldbe;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
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


public class MainActivity extends FragmentActivity {

	  private static final int NUM_PAGES = 2;
	  private ViewPager mPager;  //The ViewPager that hosts the section contents

	  private ScreenSlidePagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);
		
		Log.d("**MAINonCreate", "new main activity");

		final ActionBar actionBar = getActionBar();
	    // Instantiate a ViewPager and a PagerAdapter.
		pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(pagerAdapter);
	    
	    mPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	                    getActionBar().setSelectedNavigationItem(position);
	                }
	            });
	    
	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab arg0,
					android.app.FragmentTransaction arg1) {
				// probably ignore this event
				
			}

			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction arg1) {
				// show the given tab
	        	mPager.setCurrentItem(tab.getPosition());
				
			}

			@Override
			public void onTabUnselected(Tab arg0,
					android.app.FragmentTransaction arg1) {
				// hide the given tab
				
			}
	    };
	    
	    actionBar.addTab(actionBar.newTab()
                .setText("Wall")
                .setTabListener(tabListener));
//	    Button mapButton = (Button) findViewById(R.id.actionMapButton);
//	    mapButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
//            	tapLocation(v);
//            }
//        });
	    actionBar.addTab(actionBar.newTab()
                .setText("Post")
                .setTabListener(tabListener));
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
			startActivity(new Intent(this, TapActivity.class)); 
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
	    		return new ShouldBeFragment();
	    	}
	    }
	
	    @Override
	    public int getCount() {
	        return NUM_PAGES;
	    }
	}


}