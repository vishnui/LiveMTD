package com.indukuri.mtdlive;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class ETAListAdapter extends ArrayAdapter<String> {
	private ArrayList<String> buses ;
	private Context context ;
	private Pattern pat ;
	private Firebase backend ;
	private VishiousMarker vmark ;
	
	private OnUpdateFinished onUpdateFinishedListener ;
	private Typeface robotoLight ;
	private Intent launchService ;
	
	public ETAListAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
		super(context, textViewResourceId, objects) ;
		buses = objects ;
		this.context = context ;
		pat = Pattern.compile("(\\d{1,3})(\\w)\\s(\\d{1,2})\\s(\\w{1,4})", Pattern.CASE_INSENSITIVE) ;
		robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf") ;
		launchService = null ;
	}

	@Override
	public void add(String data) {
		buses.clear() ;
		String[] etas = data.split(":::") ;
		
		String[] newData ;
		if(etas == null || etas.length < 2) 
			newData = new String[0] ;
		else 
			newData = etas[0].split("\n") ;
		for(String bus : newData) 
			buses.add(bus) ;

		onUpdateFinishedListener.onFinish() ;
		notifyDataSetChanged() ;
	}
	
	public void update() {
		if(canRefresh()) 
			new UpdateDepsTask(vmark, null).execute(StopMarkerManager.getMapMarker(vmark.getId())) ;
		else onUpdateFinishedListener.onFinish() ;
	}
	
	public boolean canRefresh() {
		return vmark.isOld() ;
	}
	
	public void setStop(VishiousMarker vmark){
		this.vmark = vmark ;
		if(backend != null) backend.removeEventListener(dataUpdateListener) ;
		backend = new Firebase("https://livecumtd.firebaseio.com/stopDeps/"+vmark.getId()) ;
		backend.addValueEventListener(dataUpdateListener) ;
	}
	
	private ValueEventListener dataUpdateListener = new ValueEventListener() {
		public void onDataChange(DataSnapshot snap) {
			String data = (String) snap.getValue() ;
			add(data) ;
		}
		public void onCancelled(FirebaseError arg0) {}
	};

	@Override
	public String getItem(int position) {
		return buses.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Inflate the view
		if(convertView == null) 
			convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.etalistitem, null);
		// parse the incoming data
		String data = buses.get(position);
		final Matcher m = pat.matcher(data);
		
		TextView busname = ((TextView) convertView.findViewById(R.id.busname)); 
		TextView eta = ((TextView) convertView.findViewById(R.id.eta));
		
		eta.setTypeface(robotoLight);
		busname.setTypeface(robotoLight);
		
		if(m.find()){
			// get bus route number
			String bus = m.group(1) ;
			int color = getBackgroundColor(bus); 

			busname.setText(bus + m.group(2)+ " "+getBusName(bus)) ;
			busname.setTextColor(color);

			eta.setText("ETA: "+m.group(3)+" min") ;
		} else {
			busname.setText("Have fun walking") ;
			busname.setTextColor(0xff000000);
			
			eta.setText("ETA: Never");
		}
		
		ImageButton trackbutton = ((ImageButton) convertView.findViewById(R.id.trackButton)); 
		trackbutton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if(launchService != null) context.stopService(launchService);
				launchService = new Intent(context, TrackingService.class) ;
				launchService.putExtra("com.indukuri.livemtd.stop_id", vmark.getId()) ;
				launchService.putExtra("com.indukuri.livemtd.bus", m.group(1)+m.group(2)) ;
				context.startService(launchService);
				Toast.makeText(context, "Tracking.  Check your notifications.", Toast.LENGTH_LONG).show() ;
				return true ;
			}
		}) ;
		return convertView;
	}
	
	private int getBackgroundColor(String route) {
		if (route.equals("27") || route.equals("270"))
			return 0xff68b6e5 ;
		else if (route.equals("14") || route.equals("140"))
			return 0xff2b3088 ;
		else if (route.equals("22") || route.equals("220"))
			return 0xff5a1d5a ;
		else if (route.equals("13") || route.equals("130"))
			return 0xffcccccc ;
		else if (route.equals("9") || route.equals("90"))
			return 0xff825622 ;
		else if (route.equals("18") || route.equals("180"))
			return 0xffb2d235 ;
		else if (route.equals("5") || route.equals("50"))
			return 0xff008063 ;
		else if (route.equals("2") || route.equals("20"))
			return 0xffff0000;
		else if (route.equals("30") || route.equals("3"))
			return 0xffa78bc0 ;
		else if (route.equals("4") || route.equals("40"))
			return 0xff355caa ;
		else if (route.equals("11") || route.equals("110"))
			return 0xffeb008b ;
		else if (route.equals("6") || route.equals("60"))
			return 0xfff99f2a ;
		else if (route.equals("70") || route.equals("7"))
			return 0xff808285 ;
		else if (route.equals("10"))
			return 0xffc7994a ;
		else if (route.equals("16"))
			return 0xfff9cbdf ;
		else if (route.equals("120") || route.equals("12"))
			return 0xff006991 ;
		else if (route.equals("8") || route.equals("80"))
			return 0xff9e8966;
		else if (route.equals("1") || route.equals("100"))
			return 0xfffcee1f;
		else 
			return context.getResources().getColor(R.color.primary) ;
	}
	
	private String getBusName(String route) {
		if (route.equals("27") || route.equals("270"))
			return "Airbus" ;
		else if (route.equals("14") || route.equals("140"))
			return "NAVY" ;
		else if (route.equals("22") || route.equals("220"))
			return "ILLINI" ;
		else if (route.equals("13") || route.equals("130"))
			return "SILVER" ;
		else if (route.equals("9") || route.equals("90"))
			return "BROWN" ;
		else if (route.equals("18") || route.equals("180"))
			return "LIME" ;
		else if (route.equals("5") || route.equals("50"))
			return "GREEN" ;
		else if (route.equals("2") || route.equals("20"))
			return "RED";
		else if (route.equals("30") || route.equals("3"))
			return "LAVENDER" ;
		else if (route.equals("4") || route.equals("40"))
			return "BLUE" ;
		else if (route.equals("11") || route.equals("110"))
			return "RUBY" ;
		else if (route.equals("6") || route.equals("60"))
			return "ORANGE" ;
		else if (route.equals("70") || route.equals("7"))
			return "GREY" ;
		else if (route.equals("10"))
			return "GOLD" ;
		else if (route.equals("16"))
			return "PINK" ;
		else if (route.equals("120") || route.equals("12"))
			return "TEAL" ;
		else if (route.equals("8") || route.equals("80"))
			return "BRONZE";
		else if (route.equals("1") || route.equals("100"))
			return "YELLOW";
		else 
			return "SAFE RIDES" ;
	}
	
	public interface OnUpdateFinished {
		public void onFinish() ;
	}
	
	public void setOnUpdateFinishedListener(OnUpdateFinished listener){
		onUpdateFinishedListener = listener ;
	}
}