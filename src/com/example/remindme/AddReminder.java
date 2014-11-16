package com.example.remindme;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddReminder extends FragmentActivity implements OnClickListener {

	EditText remindTag, remindDesc;
	Button setDate, setTime, setReminder;

	FragmentManager fm;

	static TextView date, time;

	private static boolean dset = false, tset = false;

	Calendar remindTime;
	static Calendar pickerDefaultTime = Calendar.getInstance();

	private static int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0,
			NEW_REMINDER = 3, UPDATE_REMINDER = 4;
	public int requestCode, functionType;

	Reminder newReminder;

	DatabaseHandler db = new DatabaseHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_reminder);
		requestCode = getIntent().getIntExtra("requestCode", -1);
		functionType = getIntent().getIntExtra("functionType", -1);
		if (requestCode == -1 || functionType == -1) {
			this.setResult(RESULT_OK);
			finish();
		}
		setup();

		if (functionType == UPDATE_REMINDER) {
			Reminder r = db.getReminder(requestCode);
			remindTag.setText(r.getTitle());
			remindDesc.setText(r.getDescription());
			pickerDefaultTime.setTimeInMillis(r.getMillis());
			mYear = pickerDefaultTime.get(Calendar.YEAR);
			mMonth = pickerDefaultTime.get(Calendar.MONTH);
			mDay = pickerDefaultTime.get(Calendar.DAY_OF_MONTH);
			mHour = pickerDefaultTime.get(Calendar.HOUR_OF_DAY);
			mMinute = pickerDefaultTime.get(Calendar.MINUTE);
			remindDesc.setVisibility(View.VISIBLE);

			Calendar c = Calendar.getInstance();
			c.set(mYear, mMonth, mDay, mHour, mMinute, 0);

			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			time.setText(sdf.format(c.getTimeInMillis()));
			sdf = new SimpleDateFormat("EEE dd-MM-yyyy");
			date.setText(sdf.format(c.getTimeInMillis()));

			dset = tset = true;
		} else {
			pickerDefaultTime.setTimeInMillis(System.currentTimeMillis());
		}

		setDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(fm, "Pick a date");

			}
		});

		setTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(fm, "Pick a Time");

			}
		});

		setReminder.setOnClickListener(this);

	}

	public void setup() {

		dset = false;
		tset = false;

		remindTime = Calendar.getInstance();

		fm = getSupportFragmentManager();

		remindTag = (EditText) findViewById(R.id.title);
		remindDesc = (EditText) findViewById(R.id.description);

		setDate = (Button) findViewById(R.id.date);
		setTime = (Button) findViewById(R.id.time);
		setReminder = (Button) findViewById(R.id.remindme);
		date = (TextView) findViewById(R.id.calendar);
		time = (TextView) findViewById(R.id.timeSet);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MM-yyyy");
		date.setText(sdf.format(System.currentTimeMillis()));
		sdf = new SimpleDateFormat("h:mm a");
		time.setText(sdf.format(System.currentTimeMillis()));

	}

	public void DatabaseEntry() {
		// TODO Auto-generated method stub
		newReminder = new Reminder(requestCode, remindTag.getText().toString(),
				remindDesc.getText().toString(), remindTime.getTimeInMillis());
		if (functionType == NEW_REMINDER)
			db.addReminder(newReminder);
		else
			db.updateReminder(newReminder);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (tset == true && dset == true) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE dd-MM-yyyy 'at' HH:mm:ss");
			remindTime.set(mYear, mMonth, mDay, mHour, mMinute, 0);
			if (System.currentTimeMillis() > remindTime.getTimeInMillis()) {
				Toast.makeText(getApplicationContext(), "Invalid time",
						Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(getApplicationContext(),
					"Reminder set for " + sdf.format(remindTime.getTime()),
					Toast.LENGTH_LONG).show();
			DatabaseEntry();
			makeAlarm();
			this.setResult(RESULT_OK);
			finish();

		} else if (tset == false && dset == false) {
			Toast.makeText(getApplicationContext(), "Set the date and time",
					Toast.LENGTH_SHORT).show();
		} else if (tset == false) {
			Toast.makeText(getApplicationContext(), "Set the time",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Set the date",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void makeAlarm() {

		Intent remindIntent = new Intent(getApplicationContext(),
				Description.class);
		remindIntent.putExtra("requestCode", requestCode);
		remindIntent.putExtra("tag", remindTag.getText().toString());
		remindIntent.putExtra("description", remindDesc.getText().toString());

		PendingIntent pi = PendingIntent.getService(getApplicationContext(),
				requestCode, remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, remindTime.getTimeInMillis(), pi);

	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int year = pickerDefaultTime.get(Calendar.YEAR);
			int month = pickerDefaultTime.get(Calendar.MONTH);
			int day = pickerDefaultTime.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {

			AddReminder.dset = true;
			AddReminder.mYear = year;
			AddReminder.mMonth = month;
			AddReminder.mDay = day;
			SimpleDateFormat sdf = new SimpleDateFormat("EEE dd-MM-yyyy");
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			date.setText(sdf.format(c.getTimeInMillis()));

		}
	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int hour = pickerDefaultTime.get(Calendar.HOUR_OF_DAY);
			int minute = pickerDefaultTime.get(Calendar.MINUTE);

			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			AddReminder.tset = true;
			AddReminder.mHour = hourOfDay;
			AddReminder.mMinute = minute;
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			Calendar c = Calendar.getInstance();
			c.set(0, 0, 0, hourOfDay, minute, 0);
			time.setText(sdf.format(c.getTimeInMillis()));

		}
	}

}