package app.there.shouldbe;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;


public class MainActivity extends FragmentActivity {

	  private static final int NUM_PAGES = 2;
	  private PagerAdapter mPagerAdapter;   //The adapter definition of the fragments
	  private ViewPager mPager;  //The ViewPager that hosts the section contents

	  private ScreenSlidePagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);

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
	    //mPager.setAdapter(mPagerAdapter);
	    //mPager.setCurrentItem(0);
	    
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

	    // Add 3 tabs, specifying the tab's text and TabListener
	    for (int i = 0; i < 2; i++) {
	        actionBar.addTab(
	                actionBar.newTab()
	                        .setText("Tab " + (i + 1))
	                        .setTabListener(tabListener));
	    }
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