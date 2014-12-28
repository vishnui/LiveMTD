package com.indukuri.mtdlive;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@SuppressWarnings("static-access")
public class MapFragment extends Fragment {

	// Map related stuff
	private MapView mapView;
	private GoogleMap map;
	// Backend related stuff
	private Firebase stopList;
	private Firebase stopDeps;
	private StopMarkerManager markerManager;
	private boolean done = false ;

	// Need to pass on lifecycle events to the mapView
	// so it can react appropriately since we're not using
	// the recommended MapFragment
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saved) {
		// Inflate MapView
		View view = inflater.inflate(R.layout.mapfragment, null);
		mapView = ((MapView) view.findViewById(R.id.mapView));
		mapView.onCreate(saved);
		// Configure the map
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		LatLng pos = new LatLng(40.1102972, -88.2271661);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 13)) ;
		// Custom Info Windows
		map.setInfoWindowAdapter(new PopupInfoWindowAdapter(inflater));
		MapActivity.map = this;
		// Start backend
		loadAllChambanaStops.execute() ;
		stopDeps = new Firebase("https://livecumtd.firebaseio.com/stopDeps");
		markerManager = new StopMarkerManager(map);
		stopDeps.addChildEventListener(stopDepListener);
		// Start Getting Live bus updates
		BusMarkerManager updates = new BusMarkerManager(map);
		updates.startUpdates();
		return view;
	}

	public void showMarker(String stop_id) {
		VishiousMarker target = markerManager.getVishiousMarker(stop_id);
		markerManager.moveToVishiousMarker(target);
	}

	ChildEventListener stopDepListener = new ChildEventListener() {
		public void onChildRemoved(DataSnapshot arg0) {	}
		public void onChildMoved(DataSnapshot arg0, String arg1) {	}
		public void onCancelled(FirebaseError arg0) {	}
		public void onChildChanged(DataSnapshot snap, String arg1) {
			String stopid = snap.getKey();
			VishiousMarker vmark = markerManager.getVishiousMarker(stopid);
			if (vmark == null)
				return;
			String val = (String) snap.getValue();
			String[] deps = val.split(":::");
			if (deps[0] == null || deps[0].equals(""))
				vmark.setSnippet("No buses in the next half hour :(");
			else
				vmark.setSnippet(deps[0]);
			if (deps.length > 1)
				vmark.setLastUpdatedOnFirebase(Long.parseLong(deps[1]));
		}
		public void onChildAdded(DataSnapshot arg0, String arg1) {
			onChildChanged(arg0, arg1);
	}};
	
	private AsyncTask<Void, Void, Void> loadAllChambanaStops = new AsyncTask<Void, Void, Void>() {
		@SuppressWarnings("unchecked")
		protected Void doInBackground(Void... params) {
			stopList = new Firebase(
					"https://livecumtd.firebaseio.com/stopList/stops");
			stopList.addChildEventListener(new ChildEventListener() {
				public void onChildRemoved(DataSnapshot arg0) {	}
				public void onChildMoved(DataSnapshot arg0, String arg1) {	}
				public void onChildChanged(DataSnapshot arg0, String arg1) {	}
				public void onCancelled(FirebaseError arg0) {	}
				public void onChildAdded(DataSnapshot snap, String arg1) {
					DataSnapshot stop_points = snap.child("stop_points");
					List<Map<String, Object>> stops = (List<Map<String, Object>>) stop_points
							.getValue();
					for (Map<String, Object> stop : stops) {
						double stoplat = (double) stop.get("stop_lat");
						double stoplon = (double) stop.get("stop_lon");
						String stopName = (String) stop.get("stop_name");
						String stopID = (String) stop.get("stop_id");
						markerManager.addMarker(new LatLng(stoplat, stoplon), stopName,
								stopID);
						if (stopID.contains("YMCA"))
							done = true ;
			}}}) ;
			
			// Sleep until all data has been loaded
			try {
				while(!done) Thread.sleep(200) ;
			} catch(InterruptedException e) {}
			return null;
		}
	};

	@SuppressLint("InflateParams")
	private class PopupInfoWindowAdapter implements InfoWindowAdapter {
		private View popup = null;
		private LayoutInflater inflater = null;
		PopupInfoWindowAdapter(LayoutInflater infl) {
			inflater = infl;
		}
		public View getInfoWindow(Marker marker) {
			return null;
		}
		public View getInfoContents(Marker marker) {
			if (popup == null) {
				popup = inflater.inflate(R.layout.popup_layout, null);
			}
			TextView tv = (TextView) popup.findViewById(R.id.title);
			tv.setText(marker.getTitle());
			tv = (TextView) popup.findViewById(R.id.snippet);
			tv.setText(marker.getSnippet());
			return popup;
	}}
}