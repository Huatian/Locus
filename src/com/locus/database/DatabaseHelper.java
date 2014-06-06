package com.locus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*values.put("timeStart", info.mTimeStart);
		values.put("timeEnd", info.mTimeEnd);
		values.put("duration", info.mDuration);
		values.put("distance", info.mDistance);
		values.put("pathString", info.mPathString);*/
		db.execSQL("CREATE TABLE IF NOT EXISTS " + "userinfo" + "(" + "_id" + " integer primary key," 
				+ "timestart"+ " integer," + "timeend"+ " integer,"
				+ "duration"+ " integer," + "distance" + " double,"
				+ "path" + " varchar"  + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
