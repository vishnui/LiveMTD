package com.indukuri.mtdlive;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.android.gms.maps.CameraUpdateFactory;
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

public class StopMarkerManager implements GoogleMap.OnCameraChangeListener {
	
	public static VishiousMarker lastTouched ;
	
	private static HashMap<Marker, VishiousMarker> stops;
	private static HashMap<String, VishiousMarker> allStops;
	private PointQuadTree<VishiousMarker> stopQuadTree;
	private GoogleMap map ;

	public StopMarkerManager(GoogleMap map) {
		this.map = map ;
		stops = new HashMap<Marker, VishiousMarker>(500);
		allStops = new HashMap<String, VishiousMarker>(5000);
		Bounds champaign = new Bounds(-88.368849, -88.134445, 40.025909,
				40.176840);
		stopQuadTree = new PointQuadTree<VishiousMarker>(champaign);
		map.setOnCameraChangeListener(this);
		map.setOnMarkerClickListener(onMarkerClickListener);
		map.setOnInfoWindowClickListener(infoWindowClickListener);
	}

	public void addMarker(LatLng stopPos, String name, String id) {
		MarkerOptions opts = new MarkerOptions();
		opts.draggable(false).anchor(0.5f, 1f).flat(false)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop));
		opts.title(name).position(stopPos);
		VishiousMarker marker = new VishiousMarker(opts, id);
		stopQuadTree.add(marker);
		allStops.put(id, marker);
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		synchronized (stops) {
			clearStops();
			if (position.zoom < 16)
				return;
			LatLngBounds bounds = map.getProjection()
					.getVisibleRegion().latLngBounds;
			Bounds searchBounds = new Bounds(bounds.southwest.longitude,
					bounds.northeast.longitude, bounds.southwest.latitude,
					bounds.northeast.latitude);
			Collection<VishiousMarker> results = stopQuadTree
					.search(searchBounds);
			addMarkersToMap(results);
		}
	}

	private void clearStops() {
		for (Marker curr : stops.keySet()) {
			if (curr.isInfoWindowShown())
				stops.get(curr).showOnLoad = true;
			curr.remove();
		}
		stops.clear();
	}

	private void addMarkersToMap(Collection<VishiousMarker> markers) {
		for (VishiousMarker opts : markers) {
			Marker mark = map.addMarker(opts.getOpts());
			stops.put(mark, opts);
			if (opts.showOnLoad) {
				opts.showOnLoad = false;
				mark.showInfoWindow();
				
				if(opts.isOld()) {
					UpdateDepsTask thread = new UpdateDepsTask(opts, map);
					thread.execute(mark);
				}
			}
		}
	}

	// This is not the case that I optimized for...
	public static Marker getMapMarker(String id) {
		VishiousMarker vmark = allStops.get(id);
		for (Entry<Marker, VishiousMarker> entry : stops.entrySet()) {
			if (entry.getValue().equals(vmark))
				return entry.getKey();
		}
		return null;
	}

	public static VishiousMarker getVishiousMarker(String id) {
		VishiousMarker ret = allStops.get(id);
		if (ret == null) {
			for (int i = 1; i <= 10; i++) {
				if ((ret = allStops.get(id + ":" + i)) != null)
					return ret;
			}
		}
		return ret;
	}
	
	public void moveToVishiousMarker(VishiousMarker target){
		target.showOnLoad() ;
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(target.getPosition(), 16)) ;
	}

	private OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {
		public boolean onMarkerClick(final Marker marker) {
			marker.showInfoWindow();
			// If a bus marker was clicked, do nothing else
			if (!stops.containsKey(marker))
				return true;
			// else update data
			final VishiousMarker vmark = stops.get(marker);
			// If updated recently enough, do nothing
			if (!vmark.isOld())
				return true;
			// else move to camera and update its values
			UpdateDepsTask thread = new UpdateDepsTask(vmark, map);
			thread.execute(marker);
			lastTouched = vmark ;
			return true;
		}
	};
	
	private OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
		@Override
		public void onInfoWindowClick(Marker marker) {
			VishiousMarker vmark = stops.get(marker);
			lastTouched = vmark ;
			MapActivity.stopInfo.update() ;
			MapActivity.pager.setCurrentItem(1, true);
		}
	};
}