package com.indukuri.mtdlive;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.MapsInitializer;

public class MapActivity extends ActionBarActivity {
	
	private static RelativeLayout splash ;
	private static ActionBar abar ;
	private static boolean splashOn ;
	
	public static MapFragment map ;
	public static StopDetailsFragment stopInfo ;
	
	public static ViewPager pager ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_map);
		MapsInitializer.initialize(this);
		// INIT Firebase
		Firebase.setAndroidContext(this);
		// Open splash screen until init is done
		abar = getSupportActionBar() ;	abar.hide() ;
		splash = (RelativeLayout) findViewById(R.id.splashRelLayout) ;
		splashOn = true ;
		// Setup view pager TODO
		pager = (ViewPager) findViewById(R.id.pager) ;
		pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager())) ;
		pager.setPageTransformer(true, new DepthPageTransformer()) ;
		
		// Don't hang at splash.  Leave no matter what after 5 seconds.
		handler.postDelayed(takeOffSplash, 5*1000) ;
	}
	
	protected void onPause() {
		super.onPause();
		Firebase.goOffline() ;
	}
	protected void onResume(){
		super.onResume() ;
		Firebase.goOnline() ;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		String stop_id = null ;
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Toast.makeText(this, "Select a suggestion", Toast.LENGTH_SHORT).show() ;
        } else { 
        	stop_id = intent.getData().getLastPathSegment() ;
        	Log.e("STOP_HERE", stop_id) ;
        } 
		
		if(stop_id != null) map.showMarker(stop_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mapactionbarmenu, menu);

	    // Set up search view box
	    SearchManager searchManager =
	            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setSubmitButtonEnabled(false);
	    searchView.setIconifiedByDefault(true);
	    searchView.setQueryRefinementEnabled(false);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			return false;
		case R.id.action_compose:
			composeMessage();
			return true;
		case R.id.action_about:
			showLegal();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showLegal() {
		Intent legal = new Intent(this, LegalActivity.class);
		startActivity(legal);
	}

	private void composeMessage() {
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", "vishnui@gmail.com", null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback/Feature Request");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}
	
	public static void takeOffSplash() {
		if(!splashOn) return ;
		splash.animate().alpha(0).setDuration(500).setListener(new AnimatorListener() {
			public void onAnimationStart(Animator animation) {
				abar.show() ;
			}
			public void onAnimationRepeat(Animator animation) {	}
			public void onAnimationEnd(Animator animation) {
				splash.setVisibility(View.GONE) ;
			}
			public void onAnimationCancel(Animator animation) {	}
		}) ;
		splashOn = false ;
	}
	
	private Handler handler = new Handler() ;
	private Runnable takeOffSplash = new Runnable() {
		public void run() {
			takeOffSplash() ;
		}
	};
	
	// VIEW PAGER RELATED STUFF
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public int getCount() {   return 2;  }
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            if(position == 0) return new MapFragment() ;
            else return new StopDetailsFragment() ;
        }
    }
	
	private class DepthPageTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.75f;

	    public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	@Override
	public void onBackPressed() {
		if(pager.getCurrentItem() > 0) pager.setCurrentItem(pager.getCurrentItem() - 1) ;
		else super.onBackPressed();
	}
}