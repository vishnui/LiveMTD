package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MTDThread extends Thread {
	private static final long FIVE_SECONDS = 5*1000 ;
	private boolean running;
	private GoogleMap map;
	private String reqString = "https://developer.cumtd.com/api/v2.2/json/GetVehicles" +
			"?key=712bc9d438744869ab22ac4557ebd01b";
	private Activity context ;
	private JSONObject resp  ;
	
	public MTDThread(GoogleMap mv, Activity c) {
		running = true;
		map = mv;
		context = c ;
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
				resp = readStream(in);
				context.runOnUiThread(runner) ;
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// Sleep for a full minute before asking for an update
			try {
				Thread.sleep(FIVE_SECONDS);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private Runnable runner = new Runnable() {
		public void run() {
			try {
				processResponse(resp);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	
	public void processResponse(JSONObject resp) throws JSONException{
		JSONArray vehicles = resp.getJSONArray("vehicles");
		int numVehicles = vehicles.length() ;
		map.clear() ;
		for(int i=0; i < numVehicles; i++){
			JSONObject vehicle = vehicles.getJSONObject(i);
			JSONObject trip = vehicle.getJSONObject("trip") ;
			JSONObject location = vehicle.getJSONObject("location") ;
			
			String routeid = trip.getString("route_id") ;
			String lat = location.getString("lat") ;
			String lng = location.getString("lon");
			
			LatLng busPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
			addBus(routeid, busPos) ;
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
	
	public BitmapDescriptor getIcon(String route){
		route = route.toLowerCase(Locale.US);
		int ret ;
		
		if(route.contains("air")) ret = R.drawable.airbus ;
		else if(route.contains("navy")) ret = R.drawable.navybus ;
		else if(route.contains("illini")) ret = R.drawable.illinibus ;
		else if(route.contains("silver")) ret = R.drawable.silverbus ;
		else if(route.contains("brown")) ret = R.drawable.brownbus ;
		else if(route.contains("lime")) ret = R.drawable.limebus ;
		else if(route.contains("green")) ret = R.drawable.greenbus ;
		else if(route.contains("red")) ret = R.drawable.redbus ;
		else if(route.contains("lavendar")) ret = R.drawable.lavendarbus ;
		else if(route.contains("blue")) ret = R.drawable.bluebus ;
		else if(route.contains("ruby")) ret = R.drawable.rubybus ;
		else if(route.contains("orange")) ret = R.drawable.orangebus ;
		else if(route.contains("grey")) ret = R.drawable.greybus ;
		else if(route.contains("gold")) ret = R.drawable.goldbus ;
		else if(route.contains("pink")) ret = R.drawable.pinkbus ;
		else if(route.contains("teal")) ret = R.drawable.tealbus ;
		else if(route.contains("bronze")) ret = R.drawable.bronzebus ;
		else if(route.contains("yellow")) ret = R.drawable.yellowbus ;
		else ret = R.drawable.saferides ;
		
		return BitmapDescriptorFactory.fromResource(ret) ;
	}

	public void mstop() {
		running = false;
	}
}