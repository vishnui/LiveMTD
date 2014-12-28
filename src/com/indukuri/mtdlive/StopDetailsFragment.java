package com.indukuri.mtdlive;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.indukuri.mtdlive.ETAListAdapter.OnUpdateFinished;

@SuppressLint("InflateParams")
public class StopDetailsFragment extends Fragment {

	private ETAListAdapter adapter;
	private PtrFrameLayout ptrFrame;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stopinfofragment, null);
		MapActivity.stopInfo = this ;
		ptrFrame = (PtrFrameLayout) view
				.findViewById(R.id.ptrFrame);

		((ListView) view.findViewById(R.id.etalist)).setAdapter(adapter);

		adapter.setOnUpdateFinishedListener(new OnUpdateFinished() {
			public void onFinish() {
				ptrFrame.refreshComplete();
			}
		});

		ptrFrame.setPtrHandler(new PtrHandler() {
			public void onRefreshBegin(PtrFrameLayout frame) {
				adapter.update();
			}
			public boolean checkCanDoRefresh(PtrFrameLayout frame,
					View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity con) {
		super.onAttach(con);
		adapter = new ETAListAdapter(con, R.layout.etalistitem,
				new ArrayList<String>(10));
	}

	public void update() {
		adapter.setStop(StopMarkerManager.lastTouched);
	}
}