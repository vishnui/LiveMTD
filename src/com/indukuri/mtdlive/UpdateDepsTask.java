package com.indukuri.mtdlive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.Marker;

public class UpdateDepsTask extends AsyncTask<Marker, Marker, Marker>{
	
	private String reqString = "https://developer.cumtd.com/api/v2.2/json/GetDeparturesByStop" +
			"?key=1ff4b73c51d545e6a6175682d6a21600&stop_id=" ;
	private Firebase stopDeps ;
	private VishiousMarker marker ;
	private String resultString ;
	
	public UpdateDepsTask(VishiousMarker mark){
		marker = mark ;
		reqString += mark.getId() ; ;
		stopDeps = new Firebase("https://livecumtd.firebaseio.com/stopDeps/"+mark.getId());
	}
	
	@Override
	protected Marker doInBackground(Marker... params) {
		String infoWindowContents = null;
		// Get JSON Response
		try {
			JSONObject response = NetworkDataUtils.getJSONObjectFromMTD(reqString) ;
			Log.e("LiveMTD", response.toString()) ;
			// Process JSON
			JSONArray deps = response.getJSONArray("departures") ;
			// Construct	
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// If crashed, leave
		if(infoWindowContents == null) return params[0];
		// else write to infoWindowContents
		Log.e("LiveMTD", "window: "+infoWindowContents) ;
		marker.setSnippet(infoWindowContents);
		long update = marker.updated();
		stopDeps.setValue(infoWindowContents+":::"+update) ;
		resultString = infoWindowContents ;
		return params[0] ;
	}

	@Override
	protected void onPostExecute(Marker result) {
		result.setSnippet(resultString);
		result.hideInfoWindow();
//		result.setVisible(false);
//		result.setVisible(true);
		result.showInfoWindow();
	}
}