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
	 * MapView �ǵ�ͼ���ؼ�
	 */
	private MapView mMapView = null;
	/**
	 * ��MapController��ɵ�ͼ����
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener ���ڴ����ͼ�¼��ص�
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
		case 0:// ����㻮��
			onPosition0();
			break;
		case 1://����·��
			onPosition1();
			break;
		case 2://��λ·��
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
		Log.i("jd", "����·��");
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
		 * ����MapView��setContentView()�г�ʼ��,��������Ҫ��BMapManager��ʼ��֮��
		 */

		mMapView = (MapView) findViewById(R.id.bmapView);
		/**
		 * ��ȡ��ͼ������
		 */
		mMapController = mMapView.getController();
		/**
		 * ���õ�ͼ�Ƿ���Ӧ����¼� .
		 */
		mMapController.enableClick(true);
		/**
		 * ���õ�ͼ���ż���
		 */
		mMapController.setZoom(12);

		/**
		 * ����ͼ�ƶ���ָ����
		 * ʹ�ðٶȾ�γ�����꣬����ͨ��http://api.map.baidu.com/lbsapi/getpoint/index
		 * .html��ѯ�������� �����Ҫ�ڰٶȵ�ͼ����ʾʹ����������ϵͳ��λ�ã��뷢�ʼ���mapapi@baidu.com��������ת���ӿ�
		 */
		GeoPoint p;
		double cLat = 39.945;
		double cLon = 116.404;
		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// ����intent����ʱ���������ĵ�Ϊָ����
			Bundle b = intent.getExtras();
			p = new GeoPoint(b.getInt("y"), b.getInt("x"));
		} else {
			// �������ĵ�Ϊ�찲��
			p = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
		}

		mMapController.setCenter(p);

		initMapViewListener();

		mMapView.regMapViewListener(LocusApplication.mapManager,
				mMapListener);
	}

	private void initMapViewListener() {
		/**
		 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
		 */
		mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * �ڴ˴����ͼ�ƶ���ɻص� ���ţ�ƽ�ƵȲ�����ɺ󣬴˻ص�������
				 */
			}

			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * �ڴ˴����ͼpoi����¼� ��ʾ��ͼpoi���Ʋ��ƶ����õ� ���ù���
				 * mMapController.enableClick(true); ʱ���˻ص����ܱ�����
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
				 * �����ù� mMapView.getCurrentMap()�󣬴˻ص��ᱻ���� ���ڴ˱����ͼ���洢�豸
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 * ��ͼ��ɴ������Ĳ�������: animationTo()���󣬴˻ص�������
				 */
			}

			/**
			 * �ڴ˴����ͼ������¼�
			 */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(MainActivity.this, "��ͼ�������", Toast.LENGTH_SHORT)
						.show();
			}
		};
	}

	@Override
	protected void onPause() {
		/**
		 * MapView������������Activityͬ������activity����ʱ�����MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView������������Activityͬ������activity�ָ�ʱ�����MapView.onResume()
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
		 * MapView������������Activityͬ������activity����ʱ�����MapView.destroy()
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
