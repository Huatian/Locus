package com.locus;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.locus.bean.Dot;
import com.locus.database.DBController;
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

public class HistoryActivity extends Activity {
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		mListView = (ListView) findViewById(R.id.list_history);

		final DBController db = new DBController();
		ArrayList<String> list = db.query(getApplicationContext());
		for (String s : list) {
			Log.i("db", "" + s);
		}
		Log.i("path's num", "" + list.size());
		final ArrayList<ArrayList<Dot>> data = parseJson(list);
		final ArrayList<String> names = new ArrayList<String>();
		int i = 0;
		while (i < data.size()) {
			names.add("Â·Ïß" + (i + 1));
			i++;
		}

		mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.item_list, R.id.item_list, names));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LocusApplication.currentPath = data.get(position);
				Intent it = new Intent(HistoryActivity.this, MainActivity.class);
				Bundle b = new Bundle();
				b.putInt("position", 0);
				it.putExtra("bundle", b);
				startActivity(it);
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Gson gson = new Gson();
				db.delete(getApplicationContext(), gson.toJson(data.get(position)));
				
				names.remove(position);
				
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) mListView.getAdapter();
				adapter.notifyDataSetChanged();
				
				return false;
			}
		});
	}

	private ArrayList<ArrayList<Dot>> parseJson(ArrayList<String> list) {
		ArrayList<ArrayList<Dot>> a = new ArrayList<ArrayList<Dot>>();
		Gson gson = new Gson();
		for (String s : list) {
			ArrayList<Dot> temp = gson.fromJson(s, new TypeToken<ArrayList<Dot>>() {
			}.getType());

			a.add(temp);
		}

		return a;
	}
}
