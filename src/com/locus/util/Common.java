package com.locus.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.bean.Dot;

public class Common {
	public static Graphic drawLine(ArrayList<Dot> dots) {

		// 设定折线点坐标
		GeoPoint[] linePoints = new GeoPoint[dots.size()];
		int i = 0;
		for (Dot d : dots) {
			double mLat = d.mLatitude;
			double mLon = d.mLongitude;

			int lat = (int) (mLat * 1E6);
			int lon = (int) (mLon * 1E6);
			GeoPoint pt = new GeoPoint(lat, lon);
			linePoints[i] = pt;
			i++;
		}

		// 构建线
		Geometry lineGeometry = new Geometry();
		lineGeometry.setPolyLine(linePoints);

		// 设定样式
		Symbol lineSymbol = new Symbol();
		Symbol.Color lineColor = lineSymbol.new Color();
		lineColor.red = 255;
		lineColor.green = 0;
		lineColor.blue = 0;
		lineColor.alpha = 255;
		lineSymbol.setLineSymbol(lineColor, 5);
		// 生成Graphic对象
		Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
		return lineGraphic;
	}

	public static String longToString(long time) {
		Date date = new Date(time);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date);
	}

	public static String longToDuration(long time) {
		long nh = 3600 * 1000;
		long nm = 60 * 1000;
		int hour = (int) (time / nh);
		int minute = (int) ((time % nh) / nm);
		int second = (int) ((time % nh % nm) / 1000);
		
		String sHour = hour > 0 ? "" + hour + "小时" : "";
		String sMinute = minute > 0 ? "" + minute + "分钟":"" ;
		String sSecond = second > 0? "" + second + "秒":"";

		return sHour + sMinute + sSecond;
	}
}
