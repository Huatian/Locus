package com.locus.bean;

import java.util.ArrayList;

public class LocationInfo {
	public long mTimeStart;
	public long mTimeEnd;
	public long mDuration;
	public double mDistance;
	public String mPathString;
	
	public LocationInfo() {
	}
	
	public LocationInfo(long timeStart,long timeEnd,long duration, double distance,String pathString){
		mTimeStart = timeStart;
		mTimeEnd = timeEnd;
		mDuration = duration;
		mDistance = distance;
		mPathString = pathString;
	}
	
	public void reSet(){
		mTimeStart = 0;
		mTimeEnd = 0;
		mDuration = 0;
		mDistance = 0;
		mPathString = "";
	}
	
	public ArrayList<Dot> parseStrToDots(){
		return new ArrayList<Dot>();
	}
	
	@Override
	public String toString() {
		return "·����Ϣ����ʼʱ��--" + mTimeStart + ",����ʱ�䣺" + mTimeEnd + ",����ʱ�䣺" + mDuration+ ",�г̾��룺"+ mDistance + ",��λ�㣺" + mPathString;
	}
}
