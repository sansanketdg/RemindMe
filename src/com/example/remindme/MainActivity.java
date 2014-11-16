package com.example.remindme;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {

	private final int DELETE = 0, NEW_REMINDER = 3, UPDATE_REMINDER = 4;

	DatabaseHandler db;
	ArrayList<String> title = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	ArrayList<String> time = new ArrayList<String>();
	ArrayList<String> id = new ArrayList<String>();

	String[] titles = null, descs = null, times = null, ids = null;
	TextView v;
	Intent i;

	ListView listView;

	int requestCode = 0;

	private Button addReminder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHandler(this);

		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listView1);
		v = (TextView) findViewById(R.id.appNameShow);
		v.setTypeface(Typeface.createFromAsset(this.getAssets(), "royalacid.ttf"));

		retriveDataFromDatabase();
		showDataOntoList();

		addReminder = (Button) findViewById(R.id.plusReminder);
		addReminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						AddReminder.class);
				i.putExtra("requestCode", requestCode);
				i.putExtra("functionType", NEW_REMINDER);
				startActivityForResult(i, NEW_REMINDER);

			}
		});

	}

	public void retriveDataFromDatabase() {
		// TODO Auto-generated method stub

		int count = db.getReminderCount();
		if (count == 0) {
			listView.setVisibility(View.GONE);
			v.setVisibility(View.VISIBLE);
			requestCode = 0;
			return;
		}

		title.clear();
		description.clear();
		time.clear();
		id.clear();

		titles = descs = times = ids = null;

		ArrayList<Reminder> allReminders = db.getAllReminders();
		for (int i = 0; i < allReminders.size(); i++) {
			title.add(allReminders.get(i).getTitle());
			description.add(allReminders.get(i).getDescription());
			time.add(String.valueOf(allReminders.get(i).getMillis()));
			id.add(String.valueOf(allReminders.get(i).getId()));

			if (allReminders.get(i).getId() >= requestCode) {
				requestCode = allReminders.get(i).getId() + 1;

			}
		}

		listView.setVisibility(View.VISIBLE);
		v.setVisibility(View.GONE);

		makeArrays();

	}

	public void makeArrays() {
		titles = title.toArray(new String[title.size()]);
		descs = description.toArray(new String[description.size()]);
		times = time.toArray(new String[time.size()]);
		ids = id.toArray(new String[id.size()]);
	}

	private void showDataOntoList() {
		// TODO Auto-generated method stub

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		if (titles != null && descs != null && times != null && ids != null) {
			CustomList adapter = new CustomList(MainActivity.this, titles,
					descs, times, ids);
			listView.setAdapter(adapter);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == NEW_REMINDER || requestCode == UPDATE_REMINDER) {
				retriveDataFromDatabase();
				if (titles != null && descs != null && times != null
						&& ids != null) {
					CustomList adapter = new CustomList(MainActivity.this,
							titles, descs, times, ids);
					listView.setAdapter(adapter);
				}
				listView.invalidateViews();

			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.action_quit:
			Toast.makeText(getApplicationContext(), "Miss you, bye",
					Toast.LENGTH_LONG).show();
			finish();

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int pos, long lng) {
		// TODO Auto-generated method stub
		Intent i = new Intent(getApplicationContext(), AddReminder.class);
		TextView view = (TextView) v.findViewById(R.id.textView4);
		int requestCode = Integer.parseInt(view.getText().toString());
		i.putExtra("requestCode", requestCode);
		i.putExtra("functionType", UPDATE_REMINDER);
		startActivityForResult(i, NEW_REMINDER);

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> a, final View v, int pos,
			long lng) {
		// TODO Auto-generated method stub
		CharSequence options[] = new CharSequence[] { "Delete" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose an option");

		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface a, int options) {
				// TODO Auto-generated method stub
				switch (options) {

				case DELETE:
					AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(
							MainActivity.this);
					deleteBuilder
							.setMessage("Are you sure you want to remove this Reminder?");
					deleteBuilder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									TextView id = (TextView) v
											.findViewById(R.id.textView4);
									db.deleteReminder(Integer.parseInt(id
											.getText().toString()));

									Intent remindIntent = new Intent(
											getApplicationContext(),
											Description.class);
									PendingIntent pi = PendingIntent
											.getService(
													getApplicationContext(),
													Integer.parseInt(id
															.getText()
															.toString()),
													remindIntent, 0);
									AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
									am.cancel(pi);

									retriveDataFromDatabase();
									if (titles != null && descs != null
											&& times != null && ids != null) {
										CustomList adapter = new CustomList(
												MainActivity.this, titles,
												descs, times, ids);
										listView.setAdapter(adapter);
										listView.invalidateViews();
									}

								}
							});
					deleteBuilder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Toast.makeText(getApplicationContext(),
											"The reminder shall live",
											Toast.LENGTH_LONG).show();

								}
							});
					a.cancel();
					deleteBuilder.show();
					break;
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();

		return false;
	}

}
