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
 * 定位补偿类，针对gps中间的盲点，用百度的路线查询获得的定位点进行填补</p>
 * 用法：构造之后，先设置回调监听{@link setOnSupplementedListener}，</p>
 * 而后调用{@link startPlan}开始查询路线，最后在调用者（activity或者frgment）</p>
 * 的onDestroy()方法中调用{@link destroy}销毁资源
 * @author lingwei.wang@bitbao.com
 *
 */
public class Supplementer {
	// 搜索相关
	private MKSearch mSearch = null;

	private ArrayList<Dot> mDots = null;

	private OnSupplementedListener mListener;
	public Supplementer(ArrayList<Dot> dots) {
		mDots = dots;
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();

		mSearch.init(LocusApplication.mapManager, new MKSearchListener() {

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult results, int error) {
				//起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR){
					//遍历所有地址
//					ArrayList<MKPoiInfo> stPois = res.getAddrResult().mStartPoiList;
//					ArrayList<MKPoiInfo> enPois = res.getAddrResult().mEndPoiList;
//					ArrayList<MKCityListInfo> stCities = res.getAddrResult().mStartCityList;
//					ArrayList<MKCityListInfo> enCities = res.getAddrResult().mEndCityList;
					return;
				}
				if (error != 0 || results == null) {
					Toast.makeText(LocusApplication.instance(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
					return;
				}
				Log.i("MKWalkingRouteResult", "" + results.toString());
				MKRoutePlan plan = results.getPlan(0);// 获取第一条步行计划
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
		//stNode.name = "龙泽";
		stNode.pt = startPoint;
		MKPlanNode enNode = new MKPlanNode();
		//enNode.name = "西二旗";
		enNode.pt = endpoint;
		mSearch.walkingSearch("北京", stNode, "北京", enNode);
	}
	
	public void destroy(){
		mSearch.destory();
	}

	// 讲一个计划路线的首位坐标加入集合
	private void parseRouteToData(MKRoute route) {
		ArrayList<ArrayList<GeoPoint>> geoPoints = route.getArrayPoints();
		int i=0;
		for(ArrayList<GeoPoint> l:geoPoints){//一条路线上不同的路段
			Log.i("num", "" + i);
			for(GeoPoint g:l){//一个路段上有很多点
				Log.i("addStartAndEndToData", "" + g.getLatitudeE6() / 1E6 +"," + g.getLongitudeE6()/1E6);
			}
			
			parseListToData(l);
		}
	}
	
	private void parseListToData(ArrayList<GeoPoint> list){//将一个路段上的首末两点加入定位数据中，其余过滤
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
