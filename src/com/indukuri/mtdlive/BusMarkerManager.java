package com.indukuri.mtdlive;

import java.util.HashMap;
import java.util.Locale;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BusMarkerManager implements ChildEventListener {
	private HashMap<String, Marker> markers;
	private Firebase liveBuses ;
	private GoogleMap map ;

	public BusMarkerManager(GoogleMap map) {
		this.map = map ;
		markers = new HashMap<String, Marker>() ;
		// Start listening to bus updates
		liveBuses = new Firebase("https://livecumtd.firebaseio.com/liveBuses/vehicles") ;
	}
	
	public void startUpdates(){
		liveBuses.addChildEventListener(this);
	}

	public void onChildRemoved(DataSnapshot snapshot) {
		String busID = (String) snapshot.child("vehicle_id").getValue();
		Log.e("LIVEMTD", "Removed Bus "+busID) ;
		Marker bus = markers.remove(busID);
		if(bus == null) return ;
		bus.remove();
	}

	public void onChildChanged(DataSnapshot snapshot, String arg1) {
		double lat = (Double) snapshot.child("location/lat").getValue();
		double lon = (Double) snapshot.child("location/lon").getValue();
		String busID = (String) snapshot.child("vehicle_id").getValue();
		Marker bus = markers.get(busID);
		if(bus == null) return ;
		Log.e("LIVEMTD", "Modified Bus "+busID) ;
		LatLng newPos = new LatLng(lat, lon);
		bus.setPosition(newPos);
	}

	public void onChildAdded(DataSnapshot snapshot, String arg1) {
		double lat = (Double) snapshot.child("location/lat").getValue();
		double lon = (Double) snapshot.child("location/lon").getValue();
		String route = (String) snapshot.child("trip/route_id").getValue();
		String headsign = (String) snapshot.child("trip/trip_headsign").getValue();
		String busID = (String) snapshot.child("vehicle_id").getValue();
		LatLng bus = new LatLng(lat, lon);
		addBus(route, bus, busID, headsign);
		Log.e("LIVEMTD", "Added Bus "+busID) ;
		MapActivity.takeOffSplash() ;
	}

	// Adding Bus to the map
	public void addBus(String route, LatLng busPos, String busID, String headsign) {
		MarkerOptions opts = new MarkerOptions();
		BitmapDescriptor colorAppropriateIcon = getIcon(route);
		opts.anchor(0.5f, 0.5f).draggable(false).flat(false).position(busPos)
				.icon(colorAppropriateIcon);
		opts.title(route).snippet(headsign) ;
		Marker busMarker = map.addMarker(opts);
		markers.put(busID, busMarker);
	}

	public BitmapDescriptor getIcon(String route) {
		route = route.toLowerCase(Locale.US);
		int ret;

		if (route.contains("air"))
			ret = R.drawable.airbus;
		else if (route.contains("navy"))
			ret = R.drawable.navybus;
		else if (route.contains("illini"))
			ret = R.drawable.illinibus;
		else if (route.contains("silver"))
			ret = R.drawable.silverbus;
		else if (route.contains("brown"))
			ret = R.drawable.brownbus;
		else if (route.contains("lime"))
			ret = R.drawable.limebus;
		else if (route.contains("green"))
			ret = R.drawable.greenbus;
		else if (route.contains("red"))
			ret = R.drawable.redbus;
		else if (route.contains("lavender"))
			ret = R.drawable.lavendarbus;
		else if (route.contains("blue"))
			ret = R.drawable.bluebus;
		else if (route.contains("ruby"))
			ret = R.drawable.rubybus;
		else if (route.contains("orange"))
			ret = R.drawable.orangebus;
		else if (route.contains("grey"))
			ret = R.drawable.greybus;
		else if (route.contains("gold"))
			ret = R.drawable.goldbus;
		else if (route.contains("pink"))
			ret = R.drawable.pinkbus;
		else if (route.contains("teal"))
			ret = R.drawable.tealbus;
		else if (route.contains("bronze"))
			ret = R.drawable.bronzebus;
		else if (route.contains("yellow"))
			ret = R.drawable.yellowbus;
		else
			ret = R.drawable.saferides;

		return BitmapDescriptorFactory.fromResource(ret);
	}

	// Useless Interface Methods
	public void onCancelled(FirebaseError arg0) {	}
	public void onChildMoved(DataSnapshot a0, String a1) {	}
}