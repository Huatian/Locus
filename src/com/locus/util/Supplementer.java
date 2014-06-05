package com.locus.util;

import java.util.ArrayList;

import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.LocusApplication;
import com.locus.bean.Dot;

/**
 * ��λ�����࣬���gps�м��ä�㣬�ðٶȵ�·�߲�ѯ��õĶ�λ������</p>
 * �÷�������֮�������ûص�����{@link setOnSupplementedListener}��</p>
 * �������{@link startPlan}��ʼ��ѯ·�ߣ�����ڵ����ߣ�activity����frgment��</p>
 * ��onDestroy()�����е���{@link destroy}������Դ
 * @author lingwei.wang@bitbao.com
 *
 */
public class Supplementer {
	// �������
	private MKSearch mSearch = null;

	private ArrayList<Dot> mDots = null;

	private OnSupplementedListener mListener;
	public Supplementer(ArrayList<Dot> dots) {
		mDots = dots;
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = new MKSearch();

		mSearch.init(LocusApplication.mapManager, new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult results, int error) {
				//�����յ������壬��Ҫѡ�����ĳ����б���ַ�б�
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//�������е�ַ
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || results == null) {
					Toast.makeText(LocusApplication.instance(), "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i("MKWalkingRouteResult", "" + results.toString());
				MKRoutePlan plan = results.getPlan(0);// ��ȡ��һ�����мƻ�
				MKRoute route = plan.getRoute(0);
				parseRouteToData(route);
				mListener.onSupplemented(route.getDistance());
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	public void setOnSupplementedListener(OnSupplementedListener listener){
		mListener = listener;
	}
	
	public void startPlan(GeoPoint startPoint, GeoPoint endpoint){
		MKPlanNode stNode = new MKPlanNode();
		//stNode.name = "����";
		stNode.pt = startPoint;
		MKPlanNode enNode = new MKPlanNode();
		//enNode.name = "������";
		enNode.pt = endpoint;
		mSearch.walkingSearch("����", stNode, "����", enNode);
	}
	
	public void destroy(){
		mSearch.destory();
	}

	// ��һ���ƻ�·�ߵ���λ������뼯��
	private void parseRouteToData(MKRoute route) {
		ArrayList<ArrayList<GeoPoint>> geoPoints = route.getArrayPoints();
		int i=0;
		for(ArrayList<GeoPoint> l:geoPoints){//һ��·���ϲ�ͬ��·��
			Log.i("num", "" + i);
			for(GeoPoint g:l){//һ��·�����кܶ��
				Log.i("addStartAndEndToData", "" + g.getLatitudeE6() / 1E6 +"," + g.getLongitudeE6()/1E6);
			}
			
			parseListToData(l);
		}
	}
	
	private void parseListToData(ArrayList<GeoPoint> list){//��һ��·���ϵ���ĩ������붨λ�����У��������
		int size = list.size();
		GeoPoint st;
		GeoPoint end = list.get(size - 1);
		switch (size) {
		case 1:
			st = list.get(0);
			break;
		
		default:
			st = list.get(0);
			end = list.get(size - 1);
			break;
		}
		
		addPointToData(st);
		if(end != null){
			addPointToData(end);
		}
	}
	
	private void addPointToData(GeoPoint point){
		mDots.add(new Dot(point.getLatitudeE6() / 1E6, point.getLatitudeE6() / 1E6));
	}
	
	public interface OnSupplementedListener{
		public void onSupplemented(int distance);
	}
}
