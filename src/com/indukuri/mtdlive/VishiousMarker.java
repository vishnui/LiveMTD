package com.indukuri.mtdlive;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.quadtree.PointQuadTree;

public class VishiousMarker implements PointQuadTree.Item {
	private MarkerOptions markerOptions ;
	private String id ;
	private long lastUpdate ;
	
	public VishiousMarker(MarkerOptions mark, String i){
		markerOptions = mark ; id = i ;
		lastUpdate = 0 ;
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
	public long updated(){ lastUpdate = System.currentTimeMillis() ; return lastUpdate ; }
	public void setLastUpdatedOnFirebase(long newcurr) {
		lastUpdate = newcurr ;
	}
}