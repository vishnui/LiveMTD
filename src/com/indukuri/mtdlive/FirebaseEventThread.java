package com.indukuri.mtdlive;

import com.firebase.client.EventTarget;

import android.os.Handler;
import android.os.Looper;

public class FirebaseEventThread extends Thread implements EventTarget{
	private Handler firebaseEventHandler ;
	
	@Override
	public void run() {
		Looper.prepare() ;
		firebaseEventHandler = new Handler() ;
		Looper.loop() ;
	}
	

	@Override
	public void postEvent(Runnable event) {
		firebaseEventHandler.post(event) ;
	}
	// Useless EventTarget methods
	public void restart()  {} 
	public void shutdown() {}
}
