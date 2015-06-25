package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.content.res.AssetManager;

public class ReadStopsThread extends Thread {
	
	private BufferedReader stopStream ;
	private StopMarkerManager markers ;
	public ReadStopsThread(AssetManager assets, StopMarkerManager manager){
		stopStream = null ;
		markers = manager ;
		try {
			stopStream = new BufferedReader(new InputStreamReader(assets.open("stops.json"))) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		String json = null ;
		StringBuffer temp = new StringBuffer() ;
		try {
			// Read in file into a string
			// TODO check this for performance, prob will be bad
			// although string buffer is the best way to do this
			while ((json = stopStream.readLine()) != null) 	temp.append(json) ;
			stopStream.close() ;
			// distill from buffer into real string object
			json = temp.toString() ;
			// Create JSON Object, 
			JSONArray stops = new JSONArray(json) ;
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
					LatLng position = new LatLng(stop_point.getDouble("stop_lat"), stop_point.getDouble("stop_lng")) ;
					String stop_name = stop_point.getString("stop_name") ;
					markers.addMarker(position, stop_name, stop_id) ;
				}
			}
		} catch (JSONException | IOException e) {
			// Both exceptions should never occur so print error
			e.printStackTrace(); System.exit(MAX_PRIORITY); //   and die
		}
	}

}
