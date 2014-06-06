package com.locus.service;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.locus.bean.Dot;

import android.app.Service;

public interface IServiceBinder {
	public interface IBinderUIHandler {
		public void refreshViews(BDLocation location, ArrayList<Dot> dots, Dot d);

		public IServiceBinder getBinder();

	}

	// 设这调用的Activity
	public void setHandler(IBinderUIHandler activity);

	// 获取activity
	public IBinderUIHandler getHandler();

	// 得到service
	public Service getService();
}
