package com.locus.bean;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.locus.LocusApplication;

public class Dot {

	public static final long LOCATION_TIME_OUT = 10 * 60 * 1000;// ����gps���õ�����£�����10���Ӳ�ȡ������ʩ
	
	public static int validTimes = 0;//����������Ч��Ĵ���

	/** γ��ֵ */
	public double mLatitude;
	/** ����ֵ */
	public double mLongitude;
	/** ����¼��ʱ�� */
	public long mBirthTime;
	/** ��λ�㴦���ٶ� */
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
	 * �жϸõ�������ϸ���λ���Ƿ�ʱ
	 * 
	 * @param d
	 *            �ϸ���λ��
	 * @return
	 */
	public boolean isOutTime(Dot d) {
		return mBirthTime - d.mBirthTime > LOCATION_TIME_OUT;
	}

	/**
	 * �жϸö�λ���Ƿ���Ч
	 * @param pre ǰһ����λ��
	 * @param distance �����ľ���
	 * @return
	 */
	public boolean isValid(double distance) {
		float v = 15;
		float s = v * LocusApplication.SCAN_SPAN * validTimes;
		return distance > 2 * s;
	}

	/**
	 * ��Dotת��ΪGeoPoint����
	 * 
	 * @return
	 */
	public GeoPoint toGeoPoint() {
		return new GeoPoint((int) (mLatitude * 1E6), (int) (mLongitude * 1E6));
	}
}