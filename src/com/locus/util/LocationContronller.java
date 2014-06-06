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
	 * �ٶȵ�ͼ��λ�ͻ���
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

	// ����Option
	private void setLocationOption() {
		try {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Device_Sensors);// ���ö�λģʽ
			option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
			option.setScanSpan(LocusApplication.SCAN_SPAN);// ���÷���λ����ļ��ʱ��Ϊ5000ms
			option.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
			option.setNeedDeviceDirect(true);// ���صĶ�λ��������ֻ���ͷ�ķ���
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
					
					if(isInCacheState){//����״̬�У�������뻺�漯����
						mCacheDots.add(d);
						return;
					}
					
					if(!d.isOutTime(mPreDot)){
						
						double distance = DistanCounter.getInstance().getDistanceFromTwoDots(mPreDot, d);
						if(!d.isValid(distance)){//���ٶ��ж϶�λ���Ƿ���Ч
							if(Dot.validTimes > 0){
								Dot.validTimes = 0;
							}
							mDots.add(d);
							DistanCounter.getInstance().increaseDistance(distance);
						}else{
							Dot.validTimes ++;
							return;
						}
						
					}else{//��ʱ����ȡ������ʩ
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
	//��ʼ������ʩ
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
