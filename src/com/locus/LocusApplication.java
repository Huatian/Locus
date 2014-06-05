package com.locus;

import java.util.ArrayList;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.locus.bean.Dot;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class LocusApplication extends Application {
	private static LocusApplication instance = null;
    public boolean m_bKeyRight = true;
    public static BMapManager mapManager = null;
    public LocationClient mLocationClient;
    public static long time_begin;
    
    public static final int SCAN_SPAN = 5000;
    
    public static final String TAG = "demo";
    public static final int DB_VERSION = 1;
    
    public static ArrayList<Dot> currentPath;
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		time_begin = System.currentTimeMillis();
		initEngineManager(this);
		mLocationClient = new LocationClient(this);
	}
	
	public void initEngineManager(Context context) {
        if (mapManager == null) {
            mapManager = new BMapManager(context);
        }

        if (!mapManager.init(new MyGeneralListener())) {
            Toast.makeText(LocusApplication.instance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static LocusApplication instance(){
		return instance;
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(LocusApplication.instance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(LocusApplication.instance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(LocusApplication.instance().getApplicationContext(), 
                        "AndroidManifest.xml 文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                LocusApplication.instance().m_bKeyRight = false;
            }
            else{
            	LocusApplication.instance().m_bKeyRight = true;
            	Toast.makeText(LocusApplication.instance().getApplicationContext(), 
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
