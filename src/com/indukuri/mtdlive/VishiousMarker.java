package com.indukuri.mtdlive;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.quadtree.PointQuadTree;

public class VishiousMarker implements PointQuadTree.Item {
	private MarkerOptions markerOptions ;
	private String id ;
	private long lastUpdate ;
	public boolean showOnLoad ;
	
	public VishiousMarker(MarkerOptions mark, String i){
		markerOptions = mark ; id = i ;
		lastUpdate = 0 ; showOnLoad = false; 
	}
	public Point getPoint() {
		LatLng pos = markerOptions.getPosition() ;
		return new Point(pos.longitude, pos.latitude) ;
	}
	public void setSnippet(String snip){
		markerOptions.snippet(snip);
	}
	public boolean isOld(){
		return (System.currentTimeMillis() - lastUpdate) > 60*1000 ;
	}
	public String getId() { return id ; }
	public MarkerOptions getOpts() { return markerOptions ; }
	public LatLng getPosition() { return markerOptions.getPosition(); }
	public long updated(){ lastUpdate = System.currentTimeMillis() ; return lastUpdate ; }
	public void setLastUpdatedOnFirebase(long newcurr) {
		lastUpdate = newcurr ;
	}
	public void showOnLoad() { showOnLoad = true ;}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof VishiousMarker)) return false ;
		VishiousMarker vmark = (VishiousMarker) other ;
		return vmark.getId() == this.id ;
	}
}