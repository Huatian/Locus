package com.locus;

import java.util.ArrayList;

import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.bean.Dot;
import com.locus.util.LineDrawer;
import com.locus.util.LocationContronller;
import com.locus.util.Supplementer;
import com.locus1.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	private MKMapViewListener mMapListener = null;

	private LocationContronller mLocContronller;

	Supplementer supplementer;
	
	private TextView mTV_Radius;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initMapview();
		mTV_Radius = (TextView) findViewById(R.id.tv_radius);

		int p = getIntent().getBundleExtra("bundle").getInt("position");

		switch (p) {
		case 0:// 多个点划线
			onPosition0();
			break;
		case 1://步行路线
			onPosition1();
			break;
		case 2://定位路线
			onPosition2();
			break;
		default:
			break;
		}

	}

	private void onPosition0() {
		ArrayList<Dot> dots = new ArrayList<Dot>();
		if(LocusApplication.currentPath == null){
			Double latitude = 39.912169;
			Double longitude = 116.459232;
			
			for (int i = 0; i < 3600; i++) {
				Double tempLatitude = latitude + 0.0001f;
				Double tempLongitude = longitude + 0.0001f;
				Dot d = new Dot(tempLatitude,tempLongitude,0.0f);
				dots.add(d);
				latitude = tempLatitude;
				longitude = tempLongitude;
				Log.i("dot", "" + latitude + ";" + longitude);
			}
		}else{
			dots = LocusApplication.currentPath;
		}
		
		
		LineDrawer lineDrawer = new LineDrawer(mMapView);
		lineDrawer.drawLine(dots);
	}

	private void onPosition1() {
		Log.i("jd", "步行路线");
		ArrayList<Dot> dots = new ArrayList<Dot>();
		GeoPoint stPoint = new GeoPoint((int)(39.91226899999747 *1E6),(int)(116.45933199999747 *1E6));
		GeoPoint enPoint = new GeoPoint((int)(40.272168990905634 *1E6), (int)(116.81923199090564 *1E6));
		supplementer = new Supplementer(dots);
		supplementer.startPlan(stPoint, enPoint);
	}

	private void onPosition2() {
		mLocContronller = new LocationContronller(mMapView, mTV_Radius);
		mLocContronller.startLocation();
	}

	private void initMapview() {
		
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */

		mMapView = (MapView) findViewById(R.id.bmapView);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(12);

		/**
		 * 将地图移动至指定点
		 * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index
		 * .html查询地理坐标 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
		 */
		GeoPoint p;
		double cLat = 39.945;
		double cLon = 116.404;
		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = intent.getExtras();
			p = new GeoPoint(b.getInt("y"), b.getInt("x"));
		} else {
			// 设置中心点为天安门
			p = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
		}

		mMapController.setCenter(p);

		initMapViewListener();

		mMapView.regMapViewListener(LocusApplication.mapManager,
				mMapListener);
	}

	private void initMapViewListener() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
				 */
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
				 * mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				String title = "";
				if (mapPoiInfo != null) {
					title = mapPoiInfo.strText;
					Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT)
							.show();
					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}

			/**
			 * 在此处理地图载完成事件
			 */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(MainActivity.this, "地图加载完成", Toast.LENGTH_SHORT)
						.show();
			}
		};
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(supplementer != null){
			supplementer.destroy();
		}
		
		if(mLocContronller != null){
			mLocContronller.stopLocation();
			mLocContronller.savePath();
		}
		
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.i("Locus", "on config changed !!!");
	}

}
