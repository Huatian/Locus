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
		// �������
				graphicsOverlay.setData(Common.drawLine(dots));

				// ��ӵ�
				// graphicsOverlay.setData(drawPoint(d));

				// ִ�е�ͼˢ��ʹ��Ч
				mMapView.refresh();
	}
}
