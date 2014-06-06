package com.locus.util;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.gson.Gson;
import com.locus.LocusApplication;
import com.locus.bean.Dot;
import com.locus.bean.LocationInfo;
import com.locus.database.DBController;
import com.locus.util.Supplementer.OnSupplementedListener;

public class LocationContronller {

	private MapView mMapView = null;
	private TextView mRadius = null;
	/**
	 * 百度地图定位客户端
	 */
	private LocationClient mLocClient;
	private GraphicsOverlay mGraphicsOverlay = null;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData mLocData = new LocationData();

	private LocationInfo mLocationInfo;
	private ArrayList<Dot> mDots = null;
	private Dot mPreDot = null;
	private ArrayList<Dot> mCacheDots = null;
	
	private Supplementer mSupplementer;
	private boolean isInCacheState = false;

	public LocationContronller(MapView mapView, TextView tv) {
		this.mMapView = mapView;
		this.mRadius = tv;
		mLocationInfo = new LocationInfo();
		mDots = new ArrayList<Dot>();
		mCacheDots = new ArrayList<Dot>();
		
		mLocClient = LocusApplication.instance().mLocationClient;
		setLocationOption();		
		MyLocationListener mMyLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mMyLocationListener);

		mGraphicsOverlay = new GraphicsOverlay(mMapView);
		myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		mMapView.getOverlays().add(mGraphicsOverlay);
		mMapView.getOverlays().add(myLocationOverlay);
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
				mRadius.setText("当前定位精度：" + location.getRadius() + ";当前行程：" + DistanCounter.distance + ";当前速度："+ location
						.getSpeed());
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

				// mMapView.removeAllViews();
				refreshLocationOverlays(d);
				if ((System.currentTimeMillis() - LocusApplication.time_begin) > (15 * 60 * 1000)) {
					stopLocation();
					setMsgDialog(LocusApplication.instance());
					return;
				}
				refreshGraphicsOverlay(mDots, d);
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

	/**
	 * 刷新绘制图层
	 */
	public void refreshGraphicsOverlay(ArrayList<Dot> dots, Dot d) {
		if(dots.size() == 0){
			return;
		}
		// 添加折线
		mGraphicsOverlay.setData(Common.drawLine(dots));

		// 添加点
		// graphicsOverlay.setData(drawPoint(d));

		// 执行地图刷新使生效
		mMapView.refresh();
	}

	/**
	 * 绘制单点，该点状态不随地图状态变化而变化
	 * 
	 * @return 点对象
	 */
	public Graphic drawPoint(Dot d) {
		double mLat = d.mLatitude;
		double mLon = d.mLongitude;
		int lat = (int) (mLat * 1E6);
		int lon = (int) (mLon * 1E6);
		GeoPoint pt1 = new GeoPoint(lat, lon);

		// 构建点
		Geometry pointGeometry = new Geometry();
		// 设置坐标
		pointGeometry.setPoint(pt1, 10);
		// 设定样式
		Symbol pointSymbol = new Symbol();
		Symbol.Color pointColor = pointSymbol.new Color();
		pointColor.red = 0;
		pointColor.green = 126;
		pointColor.blue = 255;
		pointColor.alpha = 255;
		pointSymbol.setPointSymbol(pointColor);
		// 生成Graphic对象
		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
		return pointGraphic;
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
	
	public Dialog setMsgDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				LocusApplication.time_begin = System.currentTimeMillis();
				mLocationInfo.reSet();
				startLocation();
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
