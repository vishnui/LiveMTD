package com.indukuri.mtdlive;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ScrollView;
import android.widget.TextView;

public class LegalActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView layout = new ScrollView(this);
		TextView legal = new TextView(this);
		
		legal.setTypeface(Typeface.create("monospace", Typeface.NORMAL)) ;
		String mit = getString(R.string.mit) ;
		String text = "LEGAL NOTICES\n\n"+mit ;
		text += "\n\n-------------------\nBus icon from the Noun Project: " + 
		"http://thenounproject.com/term/map-marker/5551/ by Jeremy Elder\n\n"+ 
				"Search Powered by Algolia.com\n\nMaps by Google Maps";
		
		legal.setText(text);
		legal.setPadding(10, 10, 10, 10) ;
		layout.addView(legal);
		setContentView(layout);
	}
}