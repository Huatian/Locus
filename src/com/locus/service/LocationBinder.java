package com.locus.service;

import java.lang.ref.WeakReference;

import com.locus.service.IServiceBinder.IBinderUIHandler;

import android.os.Binder;

public class LocationBinder extends Binder implements IServiceBinder {

	private LocationService mService;
	WeakReference<IBinderUIHandler> mActivity;

	@Override
	public LocationService getService() {
		return mService;
	}

	public LocationBinder(LocationService service) {
		super();
		this.mService = service;
	}

	@Override
	public void setHandler(IBinderUIHandler activity) {
		mActivity = new WeakReference<IServiceBinder.IBinderUIHandler>(activity);
	}

	@Override
	public IBinderUIHandler getHandler() {
		return mActivity.get();
	}
}
