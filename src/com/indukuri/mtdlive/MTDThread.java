package com.indukuri.mtdlive;

import android.location.Location;

public class MTDThread extends Thread{
	private Location userLoc ;
	private boolean running  ;
	
	public MTDThread(Location l){
		userLoc = l ; running = true ;
	}
	@Override
	public void run(){
		while(running){
			
		}
	}
	
	public void updateLocation(Location loc){
		userLoc = loc ;
	}
	
	public void mstop(){
		running = false; 
	}
}