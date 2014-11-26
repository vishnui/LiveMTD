package com.indukuri.mtdlive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NetworkDataUtils {
	
	public static String contactMTD(String reqString) {
		URL url;
		try {
			Log.e("NETWORK REQUEST", reqString) ;
			url = new URL(reqString);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			String resp = readStream(in);
			Log.e("NETWORK RESPONSE", resp) ;
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
	
	public static Map<String, Object> getMapOfMTDData(String reqString) throws JSONException{
		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = contactMTD(reqString) ;
			map = mapper.readValue(json, 
			    new TypeReference<HashMap<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map ;
	}
}
