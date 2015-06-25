package com.indukuri.mtdlive;

import java.util.Collection;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.quadtree.PointQuadTree;

public class StopMarkerManager implements GoogleMap.OnCameraChangeListener {
	
	private HashMap<Marker, BusStop> stops;
	private HashMap<String, BusStop> allStops;
	private PointQuadTree<BusStop> stopQuadTree;
	private GoogleMap map ;

	public StopMarkerManager(GoogleMap mapParam) {
		this.map = mapParam ;
		stops = new HashMap<Marker, BusStop>(500);
		allStops = new HashMap<String, BusStop>(5000);
		Bounds champaign = new Bounds(-88.368849, -88.134445, 40.025909,
				40.176840);
		stopQuadTree = new PointQuadTree<BusStop>(champaign);
		map.setOnCameraChangeListener(this);
		map.setOnMarkerClickListener(onMarkerClickListener);
	}

	/* 
	 * Add a bus stop to the manager
	 */
	public void addBusStop(LatLng stopPos, String name, String id) {
		MarkerOptions opts = new MarkerOptions();
		opts.draggable(false).anchor(0.5f, 0.5f).flat(false)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop));
		opts.title(name).position(stopPos);
		BusStop marker = new BusStop(opts, id);
		stopQuadTree.add(marker);
		allStops.put(id, marker);
	}

	// Whenever the viewing window changes, update the
	// markers that need to be one the map
	@Override
	public void onCameraChange(CameraPosition position) {
		synchronized (stops) {
			clearStops();
			if (position.zoom >= 16) {
				LatLngBounds bounds = map.getProjection()
						.getVisibleRegion().latLngBounds;
				Bounds searchBounds = new Bounds(bounds.southwest.longitude,
						bounds.northeast.longitude, bounds.southwest.latitude,
						bounds.northeast.latitude);
				Collection<BusStop> results = stopQuadTree
						.search(searchBounds);
				addMarkersToMap(results);
			}
		}
	}

	// Clear all the markers off the map
	private void clearStops() {
		for (Marker curr : stops.keySet()) {
			if (curr.isInfoWindowShown())
				stops.get(curr).showOnLoad = true;
			curr.remove();
		}
		stops.clear();
	}

	// Add all markers to the map one by one
	private void addMarkersToMap(Collection<BusStop> markers) {
		for (BusStop stop : markers) {
			Marker mark = map.addMarker(stop.opts());
			stops.put(mark, stop);
			if (stop.showOnLoad) {
				stop.showOnLoad = false;
				mark.showInfoWindow();
				
				if(stop.isOld()) {
					UpdateDepsTask thread = new UpdateDepsTask(stop, map);
					thread.execute(mark);
				}
			}
		}
	}
	
	/**
	 * Refreshes the map so any marker additions
	 * can be seen without having to move the map
	 */
	public void bump() {
		onCameraChange(map.getCameraPosition()) ;
	}
	
	/**
	 * Retrieves the BusStop object corresponding
	 * to the stop id given.
	 */
	public BusStop getBusStop(String id) {
		BusStop ret = allStops.get(id);
		if (ret == null) {
			for (int i = 1; i <= 10; i++) {
				if ((ret = allStops.get(id + ":" + i)) != null)
					return ret;
			}
		}
		return ret;
	}
	
	// Callback function that gets called by the map object
	// when a marker is clicked.
	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		public boolean onMarkerClick(final Marker marker) {
			// If a bus marker was clicked, do nothing else
			if (!stops.containsKey(marker))
				return true;
			// else update data
			final BusStop vmark = stops.get(marker);
			// If updated recently enough, do nothing
			if (vmark.isOld()){
				marker.setSnippet("Loading...");
				UpdateDepsTask thread = new UpdateDepsTask(vmark, map);
				thread.execute(marker);
			}
			marker.showInfoWindow();
			return true;
		}
	};
}