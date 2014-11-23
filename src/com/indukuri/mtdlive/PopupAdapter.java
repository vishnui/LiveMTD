package com.indukuri.mtdlive;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

@SuppressLint("InflateParams")
public class PopupAdapter implements InfoWindowAdapter {
	private View popup = null;
	private LayoutInflater inflater = null;

	PopupAdapter(LayoutInflater infl) {
		inflater = infl;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	@Override
	public View getInfoContents(Marker marker) {
		if (popup == null) {
			popup = inflater.inflate(R.layout.popup_layout, null);
		}
		TextView tv = (TextView) popup.findViewById(R.id.title);

		tv.setText(marker.getTitle());
		tv = (TextView) popup.findViewById(R.id.snippet);
		tv.setText(marker.getSnippet());

		return popup;
	}
}
