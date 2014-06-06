package com.locus;

import com.locus1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IndexActivity extends Activity {

	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		mListView = (ListView) findViewById(R.id.list);

		String[] notes = new String[] { "开始行程", "历史路线" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, R.id.item_list, notes);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if (position == 0) {
					Intent intent = new Intent(IndexActivity.this, LocationActivity.class);
					startActivity(intent);
				} else {
					Intent it = new Intent(IndexActivity.this, HistoryActivity.class);
					startActivity(it);
				}

			}
		});
	}
}
