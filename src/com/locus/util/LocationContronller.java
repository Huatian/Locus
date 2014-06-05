package com.locus.util;

import java.util.ArrayList;
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
import com.locus.database.DBController;
import com.locus.util.Supplementer.OnSupplementedListener;

public class LocationContronller {

	private MapView mMapView = null;
	private TextView mRadius = null;
	/**
	 * �ٶȵ�ͼ��λ�ͻ���
	 */
	private LocationClient mLocClient;
	private GraphicsOverlay mGraphicsOverlay = null;
	private MyLocationOverlay myLocationOverlay = null;
	private LocationData mLocData = new LocationData();

	private ArrayList<Dot> mDots = null;
	private Dot mPreDot = null;
	private ArrayList<Dot> mCacheDots = null;
	
	private Supplementer mSupplementer;
	private boolean isInCacheState = false;

	public LocationContronller(MapView mapView, TextView tv) {
		this.mMapView = mapView;
		this.mRadius = tv;
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
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();
		else
			Log.d("LocSDK3", "locClient is null or not started");
	}
	
	public void stopLocation(){
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.stop();
		else
			Log.d("LocSDK3", "locClient is null or not started");
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
	
	public void savePath(){
		Gson gson = new Gson();
		String s = gson.toJson(mDots);
		DBController db = new DBController();
		db.insert(LocusApplication.instance(), s);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location.getLatitude() != 4.9E-324) {
				Dot d = new Dot(location.getLatitude(), location.getLongitude(),location.getSpeed());
				Log.i("location", "Direction:" + location.getDirection() + ",Speed:" + location.getSpeed());
				mRadius.setText("��ǰ��λ���ȣ�" + location.getRadius() + ";��ǰ�г̣�" + DistanCounter.distance + ";��ǰ�ٶȣ�"+ location
						.getSpeed());
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

				// mMapView.removeAllViews();
				refreshLocationOverlays(d);
				if ((System.currentTimeMillis() - LocusApplication.time_begin) > (15 * 60 * 1000)) {
					mLocClient.stop();
					savePath();
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

	/**
	 * ˢ�»���ͼ��
	 */
	public void refreshGraphicsOverlay(ArrayList<Dot> dots, Dot d) {
		if(dots.size() == 0){
			return;
		}
		// �������
		mGraphicsOverlay.setData(Common.drawLine(dots));

		// ��ӵ�
		// graphicsOverlay.setData(drawPoint(d));

		// ִ�е�ͼˢ��ʹ��Ч
		mMapView.refresh();
	}

	/**
	 * ���Ƶ��㣬�õ�״̬�����ͼ״̬�仯���仯
	 * 
	 * @return �����
	 */
	public Graphic drawPoint(Dot d) {
		double mLat = d.mLatitude;
		double mLon = d.mLongitude;
		int lat = (int) (mLat * 1E6);
		int lon = (int) (mLon * 1E6);
		GeoPoint pt1 = new GeoPoint(lat, lon);

		// ������
		Geometry pointGeometry = new Geometry();
		// ��������
		pointGeometry.setPoint(pt1, 10);
		// �趨��ʽ
		Symbol pointSymbol = new Symbol();
		Symbol.Color pointColor = pointSymbol.new Color();
		pointColor.red = 0;
		pointColor.green = 126;
		pointColor.blue = 255;
		pointColor.alpha = 255;
		pointSymbol.setPointSymbol(pointColor);
		// ����Graphic����
		Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
		return pointGraphic;
	}

	/**
	 * ���ƶ�λͼ��
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
}
