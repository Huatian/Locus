package com.locus.util;

import java.util.ArrayList;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.locus.LocationActivity;
import com.locus.LocusApplication;
import com.locus.bean.Dot;
import com.locus.bean.LocationInfo;
import com.locus.database.DBController;
import com.locus.service.LocationBinder;
import com.locus.util.Supplementer.OnSupplementedListener;

public class LocationContronller {

	/**
	 * 百度地图定位客户端
	 */
	private LocationClient mLocClient;
	private LocationBinder mBinder;

	private LocationInfo mLocationInfo;
	private ArrayList<Dot> mDots = null;
	private Dot mPreDot = null;
	private ArrayList<Dot> mCacheDots = null;
	
	private Supplementer mSupplementer;
	private boolean isInCacheState = false;

	public LocationContronller(LocationBinder binder) {
		mBinder = binder;
		mLocationInfo = new LocationInfo();
		mDots = new ArrayList<Dot>();
		mCacheDots = new ArrayList<Dot>();
		
		mLocClient = LocusApplication.instance().mLocationClient;
		setLocationOption();		
		MyLocationListener mMyLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mMyLocationListener);

		
	}
	
	public void startLocation(){
		mLocClient.start();
		LocusApplication.time_begin = System.currentTimeMillis();
	}
	
	public void stopLocation(){
		if (mLocClient != null && mLocClient.isStarted()){
			mLocClient.stop();
			savePath();
		}
		else
			Log.d("LocSDK3", "locClient is null or not started");
		
		if(mSupplementer != null){
			mSupplementer.destroy();
		}
	}

	// 设置Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Device_Sensors);// 设置定位模式
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
			option.setScanSpan(LocusApplication.SCAN_SPAN);// 设置发起定位请求的间隔时间为5000ms
			option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
			option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
			mLocClient.setLocOption(option);
		} catch (Exception e) {
			e.printStackTrace();
			// mLocationInit = false;
		}
	}
	
	private void savePath(){
		mLocationInfo.mTimeStart = LocusApplication.time_begin;
		mLocationInfo.mTimeEnd = System.currentTimeMillis();
		mLocationInfo.mDuration = mLocationInfo.mTimeEnd - mLocationInfo.mTimeStart;
		mLocationInfo.mDistance = DistanCounter.distance;
		
		Gson gson = new Gson();
		String s = gson.toJson(mDots);
		mLocationInfo.mPathString = s;
		DBController db = new DBController();
		db.insert(LocusApplication.instance(), mLocationInfo);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location.getLatitude() != 4.9E-324) {
				Dot d = new Dot(location.getLatitude(), location.getLongitude(),location.getSpeed());
				Log.i("location", "Direction:" + location.getDirection() + ",Speed:" + location.getSpeed());
				
				if(mPreDot != null){
					
					if(isInCacheState){//补偿状态中，将点放入缓存集合中
						mCacheDots.add(d);
						return;
					}
					
					if(!d.isOutTime(mPreDot)){
						
						double distance = DistanCounter.getInstance().getDistanceFromTwoDots(mPreDot, d);
						if(!d.isValid(distance)){//从速度判断定位点是否有效
							if(Dot.validTimes > 0){
								Dot.validTimes = 0;
							}
							mDots.add(d);
							DistanCounter.getInstance().increaseDistance(distance);
						}else{
							Dot.validTimes ++;
							return;
						}
						
					}else{//超时，采取补偿措施
						startSupplement(d);
					}
				}
				
				Log.i("dot", "Latitude:" + d.mLatitude + ";Longitude:"
						+ d.mLongitude);

				((LocationActivity)mBinder.getHandler()).refreshViews(location, mDots, d);
				mPreDot = d;
			} else {
				// do something
			}

		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub

		}
	}
	//开始补偿措施
	private void startSupplement(Dot d){
		isInCacheState = true;
		if(mSupplementer == null){
			mSupplementer = new Supplementer(mDots);
			mSupplementer.setOnSupplementedListener(new OnSupplementedListener() {
				
				@Override
				public void onSupplemented(int distance) {
					mDots.addAll(mCacheDots);
					mPreDot = mCacheDots.get(mCacheDots.size() - 1);
					DistanCounter.getInstance().increaseDistance(distance);
					isInCacheState = false;
				}
			});
		}
		mSupplementer.startPlan(mPreDot.toGeoPoint(), d.toGeoPoint());
		mCacheDots.add(d);
	}

	public void reSetLocationinfo(){
		mLocationInfo.reSet();
	}
}
