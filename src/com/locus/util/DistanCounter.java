package com.locus.util;

import com.baidu.mapapi.utils.DistanceUtil;
import com.locus.bean.Dot;
/**
 * 行程计算类
 * @author wanghuatian1987@gmil.com
 *
 */
public class DistanCounter {
	public static double distance = 0;
	
	private static DistanCounter instance;
	
	public static DistanCounter getInstance(){
		if(instance == null){
			instance = new DistanCounter();
		}
		
		return instance;
	}
	
	public void increaseDistance(Dot pre,Dot now){
		distance += getDistanceFromTwoDots(pre, now);
	}
	
	public void increaseDistance(int dis){
		distance += dis;
	}
	
	public void increaseDistance(double dis){
		distance += dis;
	}
	
	public double getDistanceFromTwoDots(Dot pre,Dot now){
		return DistanceUtil.getDistance(pre.toGeoPoint(), now.toGeoPoint());
	}
}
