package com.locus;

import java.util.ArrayList;
import com.google.gson.Gson;
import com.locus.bean.LocationInfo;
import com.locus.database.DBController;
import com.locus.util.Common;
import com.locus1.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	private ListView mListView;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		mListView = (ListView) findViewById(R.id.list_history);
		mTextView = (TextView) findViewById(R.id.tv_none);

		final DBController db = new DBController();
		final ArrayList<LocationInfo> list = db.query(getApplicationContext());
		if(list == null){
			mListView.setVisibility(View.GONE);
			mTextView.setVisibility(View.VISIBLE);
			mTextView.setText("暂无历史路线...");
			return;
		}
		for (LocationInfo info : list) {
			Log.i("db", "" + info.toString());
		}
		Log.i("path's num", "" + list.size());
		final ArrayList<String> names = new ArrayList<String>();
		int i = 0;
		while (i < list.size()) {
			names.add("" + Common.longToString(list.get(i).mTimeStart));
			i++;
		}

		mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.item_list, R.id.item_list, names));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LocusApplication.currentLocationInfo = list.get(position);
				Intent it = new Intent(HistoryActivity.this, PathShowActivity.class);
				startActivity(it);
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Gson gson = new Gson();
				db.delete(getApplicationContext(), gson.toJson(list.get(position)));
				
				names.remove(position);
				
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) mListView.getAdapter();
				adapter.notifyDataSetChanged();
				
				return false;
			}
		});
	}

}
