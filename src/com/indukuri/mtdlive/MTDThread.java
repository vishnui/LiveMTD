package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MTDThread extends Thread {
	private boolean running;
	private GoogleMap map;
	private String reqString = "https://developer.cumtd.com/api/v2.2/json/GetVehicles?key=712bc9d438744869ab22ac4557ebd01b";

	public MTDThread(Location l, GoogleMap mv) {
		running = true;
		map = mv;
	}

	@Override
	public void run() {
		while (running) {
			URL url;
			try {
				url = new URL(reqString);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream()));
				JSONObject response = readStream(in);
				processResponse(response);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// Sleep for a full second before asking for an update
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void processResponse(JSONObject resp) throws JSONException{
		JSONArray vehicles = resp.getJSONArray("vehicles");
		int numVehicles = vehicles.length() ;
		map.clear() ;
		for(int i=0; i < numVehicles; i++){
			JSONObject vehicle = vehicles.getJSONObject(i);
			JSONObject trip = vehicle.getJSONObject("trip") ;
			JSONObject location = vehicle.getJSONObject("location") ;
			
			String color = trip.getString("route_id") ;
			String lat = location.getString("lat") ;
			String lng = location.getString("lon");
			
			LatLng busPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			addBus(color, busPos) ;
		}
	}

	public JSONObject readStream(BufferedReader is) throws IOException,
			JSONException {
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = is.readLine()) != null) {
			sb.append(line);
		}
		line = sb.toString();
		JSONObject ret = new JSONObject(line);
		return ret;
	}
	
	public void addBus(String color, LatLng busPos){
		MarkerOptions opts = new MarkerOptions() ;
		BitmapDescriptor colorAppropriateIcon = getIcon(color) ;
		opts.anchor(0.5f, 0.5f).draggable(false).flat(true).position(busPos).icon(colorAppropriateIcon) ;
		map.addMarker(opts);
	}
	
	public BitmapDescriptor getIcon(String color){
		return null ;
	}

	public void mstop() {
		running = false;
	}
}