package com.locus.database;

import java.util.ArrayList;

import com.locus.LocusApplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBController {

	public DBController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 向本地数据库插入记录的方法，仅支持单条插�?	 * 
	 * @param context
	 * @param contact
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long insert(Context context, String s) {
		long flag = -1;
		DatabaseHelper helper = new DatabaseHelper(context, "userinfo.db", null, LocusApplication.DB_VERSION);
		SQLiteDatabase database = helper.getWritableDatabase();
		database.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("path", s);
		try {
			flag = database.insert("userinfo", null, values);

			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBController", "DB insert fail;and error:" + e.toString());
		} finally {
			database.endTransaction();
		}
		database.close();
		helper.close();

		return flag;
	}

	/**
	 * 修改本地数据库记录方�?	 * 
	 * @param context
	 * @param contact
	 * @return the number of rows affected
	 *//*
	public int update(Context context, String s) {
		int flag = 0;
		DatabaseHelper helper = new DatabaseHelper(context, "userinfo.db", null, LocusApplication.DB_VERSION);
		SQLiteDatabase database = helper.getWritableDatabase();
		database.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("path", s);
		try {
			Log.i("DBController", "id:" + contact.getId() + ";name:" + contact.getName() + ";phone:" + contact.getPhone() + ";address:" + contact.getAddress());
			flag = database.update("userinfo", values, "_id=?", new String[] { String.valueOf(contact.getId()) });
			Log.i("DBController", "notify flag:" + flag + ";id:" + contact.getId());
			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBController", "DB notify fail;and error:" + e.toString());
		} finally {
			database.endTransaction();
		}
		database.close();
		helper.close();

		return flag;
	}*/

	/**
	 * 删除本地数据库记录的方法，仅适于但条记录
	 * 
	 * @param context
	 * @param contact
	 * @return the number of rows affected if a whereClause is passed in, 0
	 *         otherwise.
	 */
	public int delete(Context context, String s) {
		int flag = 0;
		DatabaseHelper helper = new DatabaseHelper(context, "userinfo.db", null, LocusApplication.DB_VERSION);
		SQLiteDatabase database = helper.getWritableDatabase();

		database.beginTransaction();

		try {
			flag = database.delete("userinfo", "path=?", new String[] { s });

			database.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("DBController", "DB delete fail;and error:" + e.toString());
		} finally {
			database.endTransaction();
		}

		database.close();
		helper.close();

		return flag;
	}

	/**
	 * 查询本地数据库方�?	 * 
	 * @param context
	 * @return 如有记录，返回联系人对象的集合，否则返回空（null�?	 */
	public ArrayList<String> query(Context context) {
		ArrayList<String> list = null;
		try {
			DatabaseHelper helper = new DatabaseHelper(context, "userinfo.db", null, LocusApplication.DB_VERSION);
			SQLiteDatabase database = helper.getReadableDatabase();

			Cursor cursor = database.query("userinfo", null, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				if (list == null) {
					list = new ArrayList<String>();
				}

				list.add(cursor.getString(1));

				cursor.moveToNext();
			}

			cursor.close();
			database.close();
			helper.close();
		} catch (Exception e) {

		}
		return list;
	}
}
