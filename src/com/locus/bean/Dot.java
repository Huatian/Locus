package com.locus.bean;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.LocusApplication;

public class Dot {

	public static final long LOCATION_TIME_OUT = 10 * 60 * 1000;// 设置gps不好的情况下，超过10分钟采取补偿措施
	
	public static int validTimes = 0;//连续出现无效点的次数

	/** 纬度值 */
	public double mLatitude;
	/** 经度值 */
	public double mLongitude;
	/** 被记录的时间 */
	public long mBirthTime;
	/** 定位点处的速度 */
	public float mSpeed;
	
	public Dot(Double latitude, Double longitude) {
		mLatitude = latitude;
		mLongitude = longitude;
	}

	public Dot(Double latitude, Double longitude, float speed) {
		mLatitude = latitude;
		mLongitude = longitude;
		mSpeed = speed;
		mBirthTime = System.currentTimeMillis();
	}
	
	public double getmLatitude() {
		return mLatitude;
	}

	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getmLongitude() {
		return mLongitude;
	}

	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	/**
	 * 判断该点相对于上个定位点是否超时
	 * 
	 * @param d
	 *            上个定位点
	 * @return
	 */
	public boolean isOutTime(Dot d) {
		return mBirthTime - d.mBirthTime > LOCATION_TIME_OUT;
	}

	/**
	 * 判断该定位点是否有效
	 * @param pre 前一个定位点
	 * @param distance 两点间的距离
	 * @return
	 */
	public boolean isValid(double distance) {
		float v = 15;
		float s = v * LocusApplication.SCAN_SPAN * validTimes;
		return distance > 2 * s;
	}

	/**
	 * 将Dot转化为GeoPoint对象
	 * 
	 * @return
	 */
	public GeoPoint toGeoPoint() {
		return new GeoPoint((int) (mLatitude * 1E6), (int) (mLongitude * 1E6));
	}
}