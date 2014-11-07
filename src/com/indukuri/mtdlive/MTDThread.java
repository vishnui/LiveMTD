package com.indukuri.mtdlive;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;

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

	public void mstop() {
		running = false;
	}
}