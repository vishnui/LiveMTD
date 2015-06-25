package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;

public class LoadStopsTask extends AsyncTask<Void, Void, Void> {
	private MapActivity activity ;
	private final static String NO_BUSES = "No buses in the next half hour :(" ;
	
	public LoadStopsTask(MapActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public Void doInBackground(Void... empty) {
		// Read in all stops from the json file.
		String json = null;
		StringBuffer temp = new StringBuffer();
		StopMarkerManager markerManager = activity.getMarkerManager();
		try {
			// Open a stream to stops.json
			BufferedReader stopStream = new BufferedReader(
								new InputStreamReader(
										activity.getAssets().open("stops.json"))) ;
			// Read file into a stringBuffer
			while ((json = stopStream.readLine()) != null) 	temp.append(json);
			stopStream.close() ;
			// distill from buffer into real string object
			json = temp.toString() ;
			// Create JSON Object, 
			JSONObject file = new JSONObject(json) ;
			JSONArray stops = file.getJSONArray("stops") ;
			// Now to parse
			// Each object in the array should be an array of stops
			int numStops = stops.length() ;
			for(int i=0; i < numStops; i++) {
				JSONObject mainStop = stops.getJSONObject(i) ;
				// with subservient stop_points for each 
				JSONArray stop_points = mainStop.getJSONArray("stop_points") ;
				// Add each stop point to the marker manager
				int numPoints = stop_points.length() ;
				for(int j=0; j < numPoints; j++) {
					JSONObject stop_point = stop_points.getJSONObject(j) ;
					String stop_id = stop_point.getString("stop_id") ;
					LatLng position = new LatLng(stop_point.getDouble("stop_lat"), stop_point.getDouble("stop_lon")) ;
					String stop_name = stop_point.getString("stop_name") ;
					markerManager.addBusStop(position, stop_name, stop_id) ;
				}
			}
			// Log successful read op
			Log.e("LiveMTD", "File read in") ;
		} catch (JSONException | IOException e) {
			Log.e("LiveMTD", "FILE READ ERROR: "+e.getMessage()) ;
			e.printStackTrace(); 
		}
		
		// Now set up the background firebase reference
		Firebase stopDeps = new Firebase("https://livecumtd.firebaseio.com/stopDeps") ;
		stopDeps.addChildEventListener(getChildEventListener(markerManager)) ;
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		activity.bump();
	}
	
	// This will be run on a secondary thread since
	// we set a separate looper thread to be the thread 
	// these callbacks run on.
	private ChildEventListener getChildEventListener(final StopMarkerManager markers) { 
		return new ChildEventListener() { 
			public void onChildRemoved(DataSnapshot arg0) {	}
			public void onChildMoved(DataSnapshot arg0, String arg1) {	}
			public void onCancelled(FirebaseError arg0) {	}
			public void onChildChanged(DataSnapshot snap, String arg1) {
				String stopid = snap.getKey();
				BusStop stop = markers.getBusStop(stopid);
				// If we have no record of the stop, quit
				// Again, CUMTD being annoyingly stupid
				// This should not be hard to be consistent with
				if (stop == null) return;
				// otherwise execute the update
				String val = (String) snap.getValue();
				String[] deps = val.split(":::");
				// Check the null case
				long now ;
				if(deps.length > 1) now = Long.parseLong(deps[1]) ;
				else now = System.currentTimeMillis() ;
				if (deps[0] == null || deps[0].equals(""))	stop.updateStopDeps(NO_BUSES, now);
				else stop.updateStopDeps(deps[0], now) ;
			}
			public void onChildAdded(DataSnapshot arg0, String arg1) {
				onChildChanged(arg0, arg1);
		}};
	}
}