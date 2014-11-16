package com.example.remindme;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AlarmTrigger extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	DatabaseHandler db = new DatabaseHandler(this);
	ListView listView;
	TextView rTitle, rDescription;

	ArrayList<String> title = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	ArrayList<String> time = new ArrayList<String>();
	ArrayList<String> id = new ArrayList<String>();

	String[] titles = null, descs = null, times = null, ids = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.triggered_alarms);

		listView = (ListView) findViewById(R.id.listView2);
		rTitle = (TextView) findViewById(R.id.triggerTitle);
		rDescription = (TextView) findViewById(R.id.triggerDesc);

		retriveDataFromDatabase();

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		if (titles != null && descs != null && times != null && ids != null) {
			TriggeredAlarmList adapter = new TriggeredAlarmList(
					AlarmTrigger.this, titles, descs, times, ids);
			listView.setAdapter(adapter);
		}

	}

	public void retriveDataFromDatabase() {
		// TODO Auto-generated method stub

		title.clear();
		description.clear();
		time.clear();
		id.clear();

		titles = descs = times = ids = null;

		ArrayList<Reminder> TriggeredReminders = db.getReminderLessThan(System
				.currentTimeMillis());
		for (int i = 0; i < TriggeredReminders.size(); i++) {
			title.add(TriggeredReminders.get(i).getTitle());
			description.add(TriggeredReminders.get(i).getDescription());
			time.add(String.valueOf(TriggeredReminders.get(i).getMillis()));
			id.add(String.valueOf(TriggeredReminders.get(i).getId()));
		}

		listView.setVisibility(View.VISIBLE);
		makeArrays();

	}

	public void makeArrays() {
		titles = title.toArray(new String[title.size()]);
		descs = description.toArray(new String[description.size()]);
		times = time.toArray(new String[time.size()]);
		ids = id.toArray(new String[id.size()]);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();

	}

	@Override
	public void onItemClick(AdapterView<?> a, View v, int pos, long l) {
		// TODO Auto-generated method stub
		rTitle.setText(titles[pos]);
		rDescription.setText(descs[pos]);

	}

	public class TriggeredAlarmList extends ArrayAdapter<String> {

		public String[] colors = { "#1F7872", "#D13F31", "#1A202C" };
		private String[] reminderTitle;
		private Activity context;
		private String[] time;
		private String[] day;
		private String[] ids;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = context.getLayoutInflater();

			View rowView = inflater.inflate(R.layout.list_style, null, true);
			TextView reminder = (TextView) rowView.findViewById(R.id.textView1);
			TextView rDay = (TextView) rowView.findViewById(R.id.textView2);
			TextView rTime = (TextView) rowView.findViewById(R.id.textView3);
			TextView rId = (TextView) rowView.findViewById(R.id.textView4);
			TextView rDate = (TextView) rowView.findViewById(R.id.textView5);
			ImageView image = (ImageView) rowView.findViewById(R.id.list_image);

			reminder.setText(reminderTitle[position]);
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			rTime.setText(sdf.format(Long.parseLong(time[position])));

			sdf = new SimpleDateFormat("dd-MM-yyyy");
			rDate.setText(sdf.format(Long.parseLong(time[position])));

			rDay.setText(day[position]);
			rId.setText(ids[position]);

			image.setBackgroundColor(Color.parseColor(colors[position
					% colors.length]));

			return rowView;
		}

		public TriggeredAlarmList(Activity context, String[] reminderTitle,
				String[] day, String[] time, String[] ids) {
			super(context, R.layout.list_style, reminderTitle);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.reminderTitle = reminderTitle;
			this.day = day;
			this.time = time;
			this.ids = ids;
		}
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> a, View v,
			final int pos, long l) {
		// TODO Auto-generated method stub
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("Do you want to delete this Reminder?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								db.deleteReminder(Integer.parseInt(ids[pos]));
								if (a.getCount() == 1) {
									finish();
								}
								retriveDataFromDatabase();
								if (titles != null && descs != null
										&& times != null && ids != null) {
									TriggeredAlarmList adapter = new TriggeredAlarmList(
											AlarmTrigger.this, titles, descs,
											times, ids);
									listView.setAdapter(adapter);
								}
								listView.invalidateViews();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(AlarmTrigger.this, "Not deleting",
								Toast.LENGTH_LONG).show();

					}
				});
		dialog.show();
		return false;
	}

}