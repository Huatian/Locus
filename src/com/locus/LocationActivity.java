package com.locus;

import com.locus.util.LocationContronller;
import com.locus.util.Supplementer;
import com.locus1.R;
import android.os.Bundle;
import android.widget.TextView;

public class LocationActivity extends MapActivity {
	
	private LocationContronller mLocContronller;

	Supplementer supplementer;
	
	private TextView mTV_Radius;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		mTV_Radius = (TextView) findViewById(R.id.tv_location_info);
		
		mLocContronller = new LocationContronller(mMapView, mTV_Radius);
		mLocContronller.startLocation();
	}

	@Override
	protected void onDestroy() {
		if(supplementer != null){
			supplementer.destroy();
		}
		
		if(mLocContronller != null){
			mLocContronller.stopLocation();
		}
		super.onDestroy();
	}

	

}
