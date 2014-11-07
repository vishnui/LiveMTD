package com.indukuri.mtdlive;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenMapActivity extends FragmentActivity {

	private SupportMapFragment mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fullscreen_map);
		mapView = SupportMapFragment.newInstance();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.container, mapView);
		fragmentTransaction.commit();
	}

	// Location Tracking Setup
	// ----------------------------------------------------------------------------------
	private LocationManager locations;
	private MTDThread mtdUpdates;
	private GoogleMap map;

	public void startMTDDataPolling() {
		locations = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		MapsInitializer.initialize(this);
		mtdUpdates = new MTDThread(map, this);
		mtdUpdates.start();
	}

	// ----------------------------------------------------------------------------------

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		startMTDDataPolling();
		Location loc = locations
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
	}
}
