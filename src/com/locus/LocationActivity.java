package com.locus;

import java.util.ArrayList;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.bean.Dot;
import com.locus.service.IServiceBinder;
import com.locus.service.IServiceBinder.IBinderUIHandler;
import com.locus.service.LocationBinder;
import com.locus.service.LocationService;
import com.locus.util.Common;
import com.locus.util.DistanCounter;
import com.locus1.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class LocationActivity extends MapActivity implements IBinderUIHandler,ServiceConnection{
	
	private MapView mMapView = null;
	private GraphicsOverlay mGraphicsOverlay = null;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData mLocData = null;
	
	private LocationBinder mBinder;
	
	private TextView mPathInfo;
	
	private boolean isVisibility = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		mPathInfo = (TextView) findViewById(R.id.tv_location_info);
		mGraphicsOverlay = new GraphicsOverlay(mMapView);
		myLocationOverlay = new MyLocationOverlay(mMapView);
		mLocData = new LocationData();
		myLocationOverlay.enableCompass();
		mMapView.getOverlays().add(mGraphicsOverlay);
		mMapView.getOverlays().add(myLocationOverlay);
		
		Intent it = new Intent(LocationActivity.this,LocationService.class);
		bindService(it, this, BIND_AUTO_CREATE);
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		isVisibility = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		isVisibility = false;
	}

	@Override
	protected void onDestroy() {
		
		if(mBinder.getService().getLocationContronller() != null){
			mBinder.getService().getLocationContronller().stopLocation();
		}
		
		unbindService(this);
		super.onDestroy();
	}


	@Override
	public IServiceBinder getBinder() {
		// TODO Auto-generated method stub
		return mBinder;
	}


	@Override
	public void refreshViews(BDLocation location,ArrayList<Dot> dots, Dot d) {
		if(!isVisibility){
			return;
		}
		mPathInfo.setText("当前定位精度：" + location.getRadius() + ";当前行程：" + DistanCounter.distance + ";当前速度："+ location
				.getSpeed());
		
		refreshLocationOverlays(d);
		if ((System.currentTimeMillis() - LocusApplication.time_begin) > (15 * 60 * 1000)) {
			mBinder.getService().getLocationContronller().stopLocation();
			setMsgDialog(LocusApplication.instance());
			return;
		}
		refreshGraphicsOverlay(dots);
	}
	
	/**
	 * 刷新绘制图层
	 */
	public void refreshGraphicsOverlay(ArrayList<Dot> dots) {
		if(dots.size() == 0){
			return;
		}
		// 添加折线
		mGraphicsOverlay.setData(Common.drawLine(dots));

		// 执行地图刷新使生效
		mMapView.refresh();
	}
	
	/**
	 * 绘制定位图层
	 * 
	 * @param d
	 */
	public void refreshLocationOverlays(Dot d) {

		mLocData.latitude = d.mLatitude;
		mLocData.longitude = d.mLongitude;
		mLocData.direction = 2.0f;
		myLocationOverlay.setData(mLocData);

		mMapView.refresh();
		mMapView.getController().animateTo(
				new GeoPoint((int) (mLocData.latitude * 1e6),
						(int) (mLocData.longitude * 1e6)));
	}


	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mBinder = (LocationBinder) service;
		mBinder.setHandler(LocationActivity.this);
		mBinder.getService().startLocation();
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		
	}
	
	public Dialog setMsgDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				LocusApplication.time_begin = System.currentTimeMillis();
				mBinder.getService().getLocationContronller().reSetLocationinfo();;
				mBinder.getService().startLocation();
			}

		});
		builder.setNegativeButton("不，返回主界面", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) context).finish();
			}

		});
		builder.setMessage("您在十五分钟内的路线已保存，您还仍需定位吗？");
		AlertDialog dialog = builder.create();
		return dialog;
	}

}
