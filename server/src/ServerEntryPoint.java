package com.indukuri.mtdlive.server;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;

public class ServerEntryPoint {
	private static Firebase firebase;

	public static void main(String[] args) throws FirebaseException {
		firebase = new Firebase("https://livecumtd.firebaseio.com/");
		while(true){
			updateBuses() ;
			try {
				Thread.sleep(4*1000) ;
			} catch (InterruptedException e) {	}
		}
	}

	public static void updateStopList() throws FirebaseException {
		String getAllStopReqString = "https://developer.cumtd.com/api/v2.2/json/GetStops"
				+ "?key=712bc9d438744869ab22ac4557ebd01b";
		String jsonResponse = HttpUtils.contactMTD(getAllStopReqString) ;
		firebase.put("/stopList", jsonResponse) ;
	}

	public static void updateBuses() throws FirebaseException {
		String getVehiclesReqString = "https://developer.cumtd.com/api/v2.2/json/GetVehicles"
				+ "?key=712bc9d438744869ab22ac4557ebd01b";
		
		String jsonResponse = HttpUtils.contactMTD(getVehiclesReqString) ;
		firebase.put("/liveBuses", jsonResponse) ;
	}
}