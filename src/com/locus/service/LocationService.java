package com.locus.service;

import android.content.Intent;
import android.os.IBinder;

public class LocationService extends BaseService {

	private LocationBinder mBinder;

	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;

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
