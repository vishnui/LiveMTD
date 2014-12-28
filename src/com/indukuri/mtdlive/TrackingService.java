package com.indukuri.mtdlive;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.Marker;

public class TrackingService extends Service {

	private Firebase backend;
	private String data;
	private String bus ;
	private String id;
	private NotificationManager mNotificationManager;
	private final int NOTIF_ID = 2334123;

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Firebase.setAndroidContext(this);
		IntentFilter filter = new IntentFilter("com.indukuri.livemtd.stop_tracking") ;
		registerReceiver(dismissReceiver, filter) ;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(update);
		mNotificationManager.cancelAll() ;
		unregisterReceiver(dismissReceiver) ;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		id = intent.getExtras().getString("com.indukuri.livemtd.stop_id");
		bus = intent.getExtras().getString("com.indukuri.livemtd.bus");
		backend = new Firebase("https://livecumtd.firebaseio.com/stopDeps/"
				+ id);
		backend.addValueEventListener(listener);
		return START_REDELIVER_INTENT;
	}

	private void updateNotification() {
		String[] lastUpdateTimeSplit = data.split(":::") ;
		String[] buses = lastUpdateTimeSplit[0].split("\n") ;
		for(int i=0; i < buses.length; i++) {
			if(buses[i].startsWith(bus)) {
				buildNotification(buses[i]) ;
				return ;
			}
		}
		this.stopSelf() ;
	}
	
	private void buildNotification(String data) {
		String[] split = data.split("\t") ;
		String bus = split[0] ;
		int time = Integer.parseInt(split[1].split(" ")[0]) ;
		// I guess I'm a stickler for grammar...
		String dueTime ;
		if(time == 0) dueTime = " is due" ;
		else if ( time == 1) dueTime = " will arrive in 1 minute" ;
		else dueTime = " will arrive in "+time+" minutes" ;
		
		// Construct pending intents
		PendingIntent dismiss = PendingIntent.getBroadcast(this, 0, new Intent("com.indukuri.livemtd.stop_tracking"), 0) ;
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.addAction(R.drawable.canceltrack, "Stop Tracking", dismiss)
		.setDeleteIntent(dismiss)
		.setSmallIcon(R.drawable.ic_launcher)
		.setCategory(NotificationCompat.CATEGORY_STATUS)
	    .setContentTitle("LiveMTD Tracker")
	    .setContentText("The "+bus+dueTime) ;
		if(time > 0) mBuilder.setOngoing(true);
		else mBuilder.setOngoing(false) ;
		mNotificationManager.notify(NOTIF_ID, mBuilder.build()) ;
		handler.postDelayed(update, 30 * 1000);
	}
	
	private ValueEventListener listener = new ValueEventListener() {
		public void onDataChange(DataSnapshot arg0) {
			data = (String) arg0.getValue();
			updateNotification() ;
		}
		public void onCancelled(FirebaseError arg0) {}
	};

	private Handler handler = new Handler();
	private Runnable update = new Runnable() {
		@Override
		public void run() {
			if(data != null) {
				String[] split = data.split(":::");
				String lastString = split[split.length - 1];
				long last = Long.parseLong(lastString);
				
				if ((System.currentTimeMillis() - last) > (45 * 1000)) {
					VishiousMarker vmark = StopMarkerManager.getVishiousMarker(id);
					Marker mark = null;
					new UpdateDepsTask(vmark, null).execute(mark);
				} else 
					handler.postDelayed(update, 30 * 1000);
	}}};

	// IPC Shit. Don't need to deal with this.
	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		TrackingService getService() {
			return TrackingService.this;
	}}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private BroadcastReceiver dismissReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			mNotificationManager.cancelAll() ;
			stopService(new Intent(context, TrackingService.class)) ;
		}
	};
}
