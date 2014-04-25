package app.there.shouldbe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.google.android.gms.maps.MapFragment;

public class MainActivity extends FragmentActivity {

	  public static final int WALL_ACTIVITY = 0;
	  public static final int MAP_ACTIVITY = 1;
	  public static final int TWEET_ACTIVITY = 2;
	  private static final int NUM_PAGES = 2;
	  private PagerAdapter mPagerAdapter;   //The adapter definition of the fragments
	  private ViewPager mPager;  //The ViewPager that hosts the section contents


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_slide);

//	    // Instantiate a ViewPager and a PagerAdapter.
    mPager = (ViewPager) findViewById(R.id.pager);
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);
    mPager.setCurrentItem(0);
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
    		return new WallFragment();
    	}
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}


}