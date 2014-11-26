package com.indukuri.mtdlive;

import java.util.UUID;

import org.json.JSONException;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.Transaction.Handler;
import com.firebase.client.Transaction.Result;
import com.firebase.client.ValueEventListener;

public class CurrentServerThread extends Thread implements ValueEventListener{
	private Firebase currServer ;
	private Firebase liveBuses ;
	private UUID uniqueID ;
	private boolean runServer ;
	
	private final static String NOTHING = ":(" ;
	private final static long FIVE_SECONDS = 5*1000 ;
	private final static String liveBusesRequest = "https://developer.cumtd.com/api/v2.2/json/GetVehicles"
			+ "?key=712bc9d438744869ab22ac4557ebd01b";
	
	public CurrentServerThread() {
		uniqueID = UUID.randomUUID() ;
		currServer = new Firebase("https://livecumtd.firebaseio.com/currServerID") ;
		currServer.addValueEventListener(this);
		runServer = false ;
		shouldIBeServer() ;
		
		// Live Buses
		liveBuses = new Firebase("https://livecumtd.firebaseio.com/liveBuses") ;
	}
	
	public void shouldIBeServer(){
		currServer.runTransaction(new Handler() {
			public void onComplete(FirebaseError arg0, boolean arg1, DataSnapshot arg2) {	}
			public Result doTransaction(MutableData data) {
				if(((String) data.getValue()).equals(NOTHING)) {
					String id = uniqueID.toString() ;
					data.setValue(id);
					currServer.onDisconnect().setValue(NOTHING) ;
					runServer = true ;
				}
				return Transaction.success(data) ;
			}
		}) ;
	}
	
	@Override
	public void run() {
		while(true) {
			if(runServer){
				try {
					liveBuses.setValue(NetworkDataUtils.getMapOfMTDData(liveBusesRequest)) ;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			// Sleep for five seconds and check again then
			try {
				Thread.sleep(FIVE_SECONDS);
			} catch (InterruptedException e) {	}
		}
	}
	
	@Override
	public void onDataChange(DataSnapshot arg0) {
		shouldIBeServer() ;
	}
	public void onCancelled(FirebaseError arg0) {}
}