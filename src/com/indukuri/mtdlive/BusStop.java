package com.indukuri.mtdlive;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.quadtree.PointQuadTree;

public class BusStop implements PointQuadTree.Item {
	
	private String id ;
	private long lastUpdate ;
	private MarkerOptions opts ;
	
	public boolean showOnLoad = false;
	
	public BusStop(MarkerOptions opts, String i){
		this.opts = opts ;
		id = i ; 
		lastUpdate = 0 ;
	}
	
	public boolean isOld(){
		return (System.currentTimeMillis() - lastUpdate) > 60*1000 ;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof BusStop)) return false ;
		BusStop vmark = (BusStop) other ;
		return vmark.id == this.id ;
	}
	
	public long updateTimestamp(){ 
		lastUpdate = System.currentTimeMillis() ;
		return lastUpdate ; 
	}
	
	public String id() { return id ; }
	public String name() { return opts.getTitle() ; }
	public MarkerOptions opts() {	return opts ;	}
	public void updateStopDeps(String deps, long timestamp) {
		if(timestamp < lastUpdate) return ;
		// else
		opts.snippet(deps) ;
		lastUpdate = timestamp ;
	}

	@Override
	public Point getPoint() {
		LatLng pos = opts.getPosition() ;
		return new Point(pos.longitude, pos.latitude) ;
	}
	public LatLng getPosition() { return opts.getPosition() ; }
}