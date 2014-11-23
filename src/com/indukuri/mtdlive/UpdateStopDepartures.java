package com.indukuri.mtdlive;

import java.util.List;
import java.util.Map;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("unchecked")
public class UpdateStopDepartures {
	private GoogleMap map;
	private Firebase stopList;
	private Firebase stopDeps;
	private MarkerManager markersInView ;

	public UpdateStopDepartures(GoogleMap m) {
		map = m;
		stopList = new Firebase(
				"https://livecumtd.firebaseio.com/stopList/stops");
		stopDeps = new Firebase(
				"https://livecumtd.firebaseio.com/stopDeps");
		markersInView = new MarkerManager(map);
	}

	public void addBusStops() {
		stopList.addChildEventListener(stopListListener);
		stopDeps.addChildEventListener(stopDepListener);
	}
	
	ChildEventListener stopDepListener = new ChildEventListener() {
		public void onChildRemoved(DataSnapshot arg0) {	}
		public void onChildMoved(DataSnapshot arg0, String arg1) {	}
		public void onChildChanged(DataSnapshot snap, String arg1) {
			String stopid = snap.getKey() ;
			VishiousMarker vmark = markersInView.getMarker(stopid);
			if(vmark == null) return ;
			String val = (String) snap.getValue() ;
			String[] deps = val.split(":::")  ;
			vmark.setSnippet(deps[0]) ;
			if(deps.length > 1)
				vmark.setLastUpdatedOnFirebase(Long.parseLong(deps[1])) ;
		}
		public void onChildAdded(DataSnapshot arg0, String arg1) {
			// Do everything in on child changed
			onChildChanged(arg0, arg1);
		}
		public void onCancelled(FirebaseError arg0) {		}
	};

	ChildEventListener stopListListener = new ChildEventListener() {
		public void onChildRemoved(DataSnapshot arg0) {	}
		public void onChildMoved(DataSnapshot arg0, String arg1) {	}
		public void onChildChanged(DataSnapshot arg0, String arg1) {}
		public void onCancelled(FirebaseError arg0) {	}
		public void onChildAdded(DataSnapshot snap, String arg1) {
			DataSnapshot stop_points = snap.child("stop_points");
			List<Map<String, Object>> stops = (List<Map<String, Object>>) stop_points
					.getValue();
			for (Map<String, Object> stop : stops) {
				double stoplat = (double) stop.get("stop_lat");
				double stoplon = (double) stop.get("stop_lon");
				String stopName = (String) stop.get("stop_name") ;
				String stopID = (String) stop.get("stop_id") ;
				markersInView.addMarker(new LatLng(stoplat, stoplon), stopName, stopID);
			}
		}
	};
}