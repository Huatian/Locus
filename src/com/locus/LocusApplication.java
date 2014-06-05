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
                    "BMapManager  ��ʼ������!", Toast.LENGTH_LONG).show();
        }
	}
	
	public static LocusApplication instance(){
		return instance;
	}
	
	// �����¼���������������ͨ�������������Ȩ��֤�����
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(LocusApplication.instance().getApplicationContext(), "���������������",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(LocusApplication.instance().getApplicationContext(), "������ȷ�ļ���������",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//����ֵ��ʾkey��֤δͨ��
            if (iError != 0) {
                //��ȨKey����
                Toast.makeText(LocusApplication.instance().getApplicationContext(), 
                        "AndroidManifest.xml �ļ�������ȷ����ȨKey,������������������Ƿ�������error: "+iError, Toast.LENGTH_LONG).show();
                LocusApplication.instance().m_bKeyRight = false;
            }
            else{
            	LocusApplication.instance().m_bKeyRight = true;
            	Toast.makeText(LocusApplication.instance().getApplicationContext(), 
                        "key��֤�ɹ�", Toast.LENGTH_LONG).show();
            }
        }
    }
}
