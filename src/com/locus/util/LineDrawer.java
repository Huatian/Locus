package com.locus.util;

import java.util.ArrayList;

import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.locus.bean.Dot;

public class LineDrawer {
	private MapView mMapView;
	private GraphicsOverlay graphicsOverlay = null;
	public LineDrawer(MapView mapView) {
		mMapView = mapView;
		graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
	}
	
	public void drawLine(ArrayList<Dot> dots){
		// 添加折线
				graphicsOverlay.setData(Common.drawLine(dots));

				// 添加点
				// graphicsOverlay.setData(drawPoint(d));

				// 执行地图刷新使生效
				mMapView.refresh();
	}
}
