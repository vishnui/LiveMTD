package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkDataUtils {
	
	public static String contactMTD(String reqString) {
		URL url;
		try {
			url = new URL(reqString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String resp = readStream(in);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String readStream(BufferedReader is) throws IOException {
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = is.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static JSONObject getJSONObjectFromMTD(String reqString) throws JSONException {
		String resp = contactMTD(reqString) ;
		return new JSONObject(resp);
	}
}
