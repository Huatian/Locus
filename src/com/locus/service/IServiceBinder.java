package com.locus.service;

import android.app.Service;


public interface IServiceBinder {
	public interface IBinderUIHandler {
		public void handleResult();

		public IServiceBinder getBinder();
		 
		public void onConnected();
	}

	// 设这调用的Activity
	public void setHandler(IBinderUIHandler activity);

	// 获取activity
	public IBinderUIHandler getHandler();

	// 得到service
	public Service getService();
}
