package com.indukuri.mtdlive;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends FragmentActivity {

	private SupportMapFragment mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fullscreen_map);
		// Get Reference to MapView
		mapView = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)) ;
	}

	// Location Tracking Setup
	// ----------------------------------------------------------------------------------
	private LocationManager locations;
	private MTDThread mtdUpdates;
	private GoogleMap map;

	public void startMTDDataPolling() {
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
		locations = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locations
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
	}
}
