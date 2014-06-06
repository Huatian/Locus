package com.locus.service;

import com.locus.util.LocationContronller;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationService extends Service {

	private LocationBinder mBinder = null;
	private LocationContronller mLocContronller = null;

	@Override
	public IBinder onBind(Intent intent) {
		if(mBinder == null){
			mBinder = new LocationBinder(LocationService.this);
		}
		
		return mBinder;
	}
	
	public void startLocation(){
		if(mLocContronller == null){
			mLocContronller = new LocationContronller(mBinder);
		}
		
		mLocContronller.startLocation();
	}
	
	public LocationContronller getLocationContronller(){
		return mLocContronller;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}
