package com.indukuri.mtdlive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.firebase.client.Config;
import com.firebase.client.EventTarget;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapActivity extends Activity {
	// Don't really need this to be a global variable but
	// don't want to risk getting GC'ed
	private FirebaseEventThread fbEventLoop ;
	private StopMarkerManager markerManager ;
	
	// BASIC INITIALIZATION
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Setup Actvity and its contents
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fullscreen_map);
		// Setup Google Map
		MapsInitializer.initialize(this);
		MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
		fragment.getMapAsync(mapReadyCallback) ;
		// INIT Firebase
		Firebase.setAndroidContext(this);
		try{ 
			Config defaultConfig = Firebase.getDefaultConfig() ;
			fbEventLoop = new FirebaseEventThread() ;
			fbEventLoop.start() ;
			defaultConfig.setEventTarget(fbEventLoop) ;
			Firebase.setDefaultConfig(defaultConfig);
		} catch(FirebaseException e) {
			// Sometimes firebase complains about being init'ed 
			// twice when the activity is opened soon after 
			// being closed and onCreate is called.  Given that 
			// we've already configed in that case, just swallow
		}
	}
	
	private OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
		@Override
		public void onMapReady(GoogleMap googleMap) {
			// Setup Map
			googleMap.getUiSettings().setMyLocationButtonEnabled(true) ;
			googleMap.getUiSettings().setMapToolbarEnabled(false) ;
			googleMap.getUiSettings().setIndoorLevelPickerEnabled(false) ;
			googleMap.getUiSettings().setZoomControlsEnabled(false) ;
			googleMap.setTrafficEnabled(false) ;
			googleMap.setMyLocationEnabled(true) ;
			LatLng pos = new LatLng(40.1102972, -88.2271661) ; // Illini Union, Champaign, IL
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17)) ;
			googleMap.setInfoWindowAdapter(new PopupInfoWindowAdapter(getLayoutInflater())) ;
			// Start marker manager
			markerManager = new StopMarkerManager(googleMap);
			new LoadStopsTask(MapActivity.this).execute() ;
		}
	};
	
	public void bump(){ markerManager.bump();	}
	public StopMarkerManager getMarkerManager() { return markerManager; }
	
	// Private inner thread to send firebase events to.  Prevents any main
	// thread hanging.
	private class FirebaseEventThread extends Thread implements EventTarget {
		private Handler firebaseEventHandler ; 
		private Looper firebaseLooper ;
		public void run() {
			Looper.prepare() ;
			firebaseEventHandler = new Handler() ;
			firebaseLooper = Looper.myLooper() ;
			Looper.loop() ;
		}
		// When a firebase event occurs, post it to
		// the event loop thread's handlet
		public void postEvent(Runnable event) {
			firebaseEventHandler.post(event) ;
		}
		// Somebody please call this from somewhere, 
		// the API pleads.  But no, Vish giveth not tonight
		public void shutdown() {
			firebaseLooper.quit() ;
		}
		// Useless EventTarget methods
		public void restart()  {}
	}
	
	@SuppressLint("InflateParams")
	private class PopupInfoWindowAdapter implements InfoWindowAdapter {
		private View popup = null;
		private LayoutInflater inflater = null;
		public PopupInfoWindowAdapter(LayoutInflater infl) {
			inflater = infl;
		}
		public View getInfoWindow(Marker marker) {
			return null;
		}
		public View getInfoContents(Marker marker) {
			if (popup == null) 
				popup = inflater.inflate(R.layout.popup_layout, null);
			
			TextView tv = (TextView) popup.findViewById(R.id.title);
			tv.setText(marker.getTitle());
			tv = (TextView) popup.findViewById(R.id.snippet);
			tv.setText(marker.getSnippet());
			return popup;
		}
	}
}