package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class UpdateDepsTask extends AsyncTask<Marker, Marker, Marker>{
	
	private String reqString = "https://developer.cumtd.com/api/v2.2/json/GetDeparturesByStop" +
			"?key=1ff4b73c51d545e6a6175682d6a21600&stop_id=" ;
	private Firebase stopDeps ;
	private BusStop marker ;
	private String resultString ;
	private GoogleMap map ;
	
	public UpdateDepsTask(BusStop mark, GoogleMap map){
		this.map = map ;
		marker = mark ;
		reqString += mark.id() ;
		stopDeps = new Firebase("https://livecumtd.firebaseio.com/stopDeps/"+mark.id() );
	}
	
	@Override
	protected Marker doInBackground(Marker... params) {
		String infoWindowContents = "";
		// Get JSON Response
		try {
			JSONObject response = getJSONObjectFromMTD(reqString) ;
			// If response is null, the stop must not exist
			// fail and let user know
			long update = marker.updateTimestamp();
			if(response == null){
				infoWindowContents = "This stop no longer exists! :(" ;
				stopDeps.setValue(":::"+update) ;
			} else {
				Log.e("LiveMTD", response.toString()) ;
				// Process JSON
				JSONArray deps = response.getJSONArray("departures") ;
				// Extract departure times from bloated
				// JSON response from CUMTD 
				int depsLength = deps.length() ;
				StringBuffer sb = new StringBuffer() ;
				for(int i=0; i < depsLength ; i++){
					JSONObject dep = deps.getJSONObject(i);
					String headsign = dep.getString("headsign") ;
					headsign = headsign.split(" ")[0] ;
					String expectedMins = dep.getString("expected_mins") ;
					sb.append(headsign+"\t"+expectedMins+" min\n") ;
				}
				infoWindowContents = sb.toString() ;
				// Update Firebase update value
				int lastNewLine = infoWindowContents.lastIndexOf("\n") ;
				if(lastNewLine != -1){
					infoWindowContents = infoWindowContents.substring(0, lastNewLine) ;
					stopDeps.setValue(infoWindowContents+":::"+update) ;
				} else {
					infoWindowContents = "No buses in the next half hour :(" ;
					stopDeps.setValue(":::"+update) ;
				}
			}
			
			Log.e("LiveMTD", "window: "+infoWindowContents) ;
			marker.updateStopDeps(infoWindowContents, System.currentTimeMillis()) ;
			resultString = infoWindowContents ;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return params[0] ;
	}
	
	private JSONObject getJSONObjectFromMTD(String reqString) {
		URL url;
		try {
			Log.e("NETWORK REQUEST", reqString) ;
			url = new URL(reqString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			// Read in file into a string
			String line;
			StringBuffer sb = new StringBuffer();
			while ((line = in.readLine()) != null) 	sb.append(line);
			line = sb.toString();
			Log.e("NETWORK RESPONSE", line) ;
			return new JSONObject(line);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	} 

	@Override
	protected void onPostExecute(Marker result) {
		if(result == null) 	return ;
		result.setSnippet(resultString);
		result.hideInfoWindow() ;
		result.showInfoWindow();
		if(map != null) map.animateCamera(CameraUpdateFactory.newLatLng(result.getPosition())) ;
	}
}