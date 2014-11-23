package com.indukuri.mtdlive;

import java.util.Collection;
import java.util.HashMap;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.quadtree.PointQuadTree;

public class MarkerManager implements GoogleMap.OnCameraChangeListener{
	private GoogleMap map ;
	private HashMap<Marker, VishiousMarker> stops ;
	private HashMap<String, VishiousMarker> allStops ;
	private PointQuadTree<VishiousMarker> stopQuadTree;
	
	public MarkerManager(GoogleMap mm){
		map = mm ;
		stops = new HashMap<Marker, VishiousMarker>(500);
		allStops = new HashMap<String, VishiousMarker>(5000);
		Bounds champaign = new Bounds(-88.368849, -88.134445, 40.025909, 40.176840) ;
		stopQuadTree = new PointQuadTree<VishiousMarker>(champaign) ;
		map.setOnCameraChangeListener(this);
		map.setOnInfoWindowClickListener(onStopClickListener);
	}
	
	public void addMarker(LatLng stopPos, String name, String id){
		MarkerOptions opts = new MarkerOptions();
		opts.draggable(false).anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop));
		opts.title(name).position(stopPos) ;
		VishiousMarker marker = new VishiousMarker(opts, id) ;
		stopQuadTree.add(marker) ;
		allStops.put(id, marker) ;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		synchronized (stops) {
			clearStops() ;
			if(position.zoom < 16) return ;
			LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
			Bounds searchBounds = new Bounds(bounds.southwest.longitude
					, bounds.northeast.longitude, bounds.southwest.latitude, 
					bounds.northeast.latitude) ;
			Collection<VishiousMarker> results = stopQuadTree.search(searchBounds) ;
			addMarkersToMap(results) ;
		}
	}
	
	public void clearStops(){
		for(Marker curr : stops.keySet()) {
			curr.remove();
		}
		stops.clear() ;
	}
	
	public void addMarkersToMap(Collection<VishiousMarker> markers){
		for(VishiousMarker opts : markers){
			Marker mark = map.addMarker(opts.getOpts()) ;
			stops.put(mark, opts);
		}
	}
	
	public VishiousMarker getMarker(String id){
		return allStops.get(id);
	}
	
	private OnInfoWindowClickListener onStopClickListener = new OnInfoWindowClickListener() {
		public void onInfoWindowClick(Marker marker) {
			// If a bus marker was clicked, do nothing
			if(!stops.containsKey(marker)) return ;
			// else update data
			VishiousMarker vmark = stops.get(marker);	
			// If updated recently enough, do nothing
			if(!vmark.isOld()) return ;
			// else update
			UpdateDepsTask thread = new UpdateDepsTask(vmark);
			thread.execute(marker) ;
		}
	};
}