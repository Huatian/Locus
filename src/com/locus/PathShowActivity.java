package com.locus;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locus.bean.Dot;
import com.locus.util.Common;
import com.locus.util.LineDrawer;
import com.locus1.R;

import android.os.Bundle;
import android.widget.TextView;

public class PathShowActivity extends MapActivity {
	private TextView mTV_Radius;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);

		mTV_Radius = (TextView) findViewById(R.id.tv_location_info);

		initView();
	}

	private void initView() {
		mTV_Radius.setText("起始时间：" + Common.longToString(LocusApplication.currentLocationInfo.mTimeStart)
						+ ";结束时间：" + Common.longToString(LocusApplication.currentLocationInfo.mTimeEnd)
						+ ";持续时间：" + Common.longToDuration(LocusApplication.currentLocationInfo.mDuration)
						+ ";行程里程：" + LocusApplication.currentLocationInfo.mDistance+ "m");
		LineDrawer lineDrawer = new LineDrawer(mMapView);
		lineDrawer.drawLine(parseJson(LocusApplication.currentLocationInfo.mPathString));
	}

	private ArrayList<Dot> parseJson(String path) {
		Gson gson = new Gson();
		
		return gson.fromJson(path, new TypeToken<ArrayList<Dot>>() {
		}.getType());

	}
}
