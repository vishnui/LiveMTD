package com.indukuri.mtdlive;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
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
		map.setOnMarkerClickListener(onMarkerClickListener) ;
	}
	
	public void addMarker(LatLng stopPos, String name, String id){
		MarkerOptions opts = new MarkerOptions();
		opts.draggable(false).anchor(0.5f, 0.5f).flat(false)
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
			if(opts.showOnLoad) {
				opts.showOnLoad = false ;
				mark.showInfoWindow() ;
			}
		}
	}
	
	// This is not the case that I optimized for...
	public Marker getMapMarker(String id){
		VishiousMarker vmark = allStops.get(id) ;
		for(Entry<Marker, VishiousMarker> entry : stops.entrySet()) {
			if(entry.getValue().equals(vmark)) return entry.getKey() ;
		}
		return null ;
	}
	
	public VishiousMarker getVishiousMarker(String id){
		VishiousMarker ret = allStops.get(id);
		if(ret == null) {
			for(int i=1; i <= 10; i++){
				if((ret = allStops.get(id+":"+i)) != null) return ret ;
			}
		}
		return ret ;
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
	
	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		public boolean onMarkerClick(Marker marker) {
			marker.showInfoWindow() ;
			onStopClickListener.onInfoWindowClick(marker);
			return true;
		}
	};
}