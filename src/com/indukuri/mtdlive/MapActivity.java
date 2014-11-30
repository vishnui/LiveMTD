package com.indukuri.mtdlive;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends ActionBarActivity {

	private SupportMapFragment mapView;
	private GoogleMap map;
	private CurrentServerThread server;
	private UpdateStopDepartures usd ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_map);
		// Get Reference to MapView
		mapView = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map));
		// Init google map
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		MapsInitializer.initialize(this);
		// INIT Firebase
		Firebase.setAndroidContext(this);
		// Start Getting Live bus updates
		LiveBusUpdates updates = new LiveBusUpdates(map);
		updates.startUpdates();
		// Add bus stops
		usd = new UpdateStopDepartures(map);
		usd.addBusStops();
		// Custom Info Windows
		map.setInfoWindowAdapter(new PopupInfoWindowAdapter(getLayoutInflater()));
		// Start server if needed
		server = new CurrentServerThread();
		server.start();
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
		
		if(stop_id == null) return ;
		final VishiousMarker target = usd.getVishiousMarker(stop_id) ;
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(target.getPosition(), 17), 
				new GoogleMap.CancelableCallback() {
			@Override
			public void onFinish() {
				target.showOnLoad() ;
			}
			public void onCancel() {}
		}) ;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Zoom in on Illini Union at Urbana-Champaign
		LatLng pos = new LatLng(40.1102972, -88.2271661);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));
		// So it does not stuck while loading from Firebase.
		Toast.makeText(this, "Loading Buses...", Toast.LENGTH_LONG).show();
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
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
		startActivity(Intent.createChooser(emailIntent, "Send email..."));
	}
}