package com.example.remindme;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class InitializeAllAlarms extends Service {

	DatabaseHandler db;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		db = new DatabaseHandler(this);

		ArrayList<Reminder> allReminders = db.getAllReminders();
		for (int i = 0; i < allReminders.size(); i++) {
			Intent reminders = new Intent(getApplicationContext(),
					Description.class);

			String title = allReminders.get(i).getTitle();
			String description = allReminders.get(i).getDescription();
			long millis = allReminders.get(i).getMillis();
			int requestCode = allReminders.get(i).getId();

			reminders.putExtra("requestCode", requestCode);
			reminders.putExtra("tag", title);
			reminders.putExtra("description", description);

			PendingIntent pi = PendingIntent.getService(
					getApplicationContext(), requestCode, reminders,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, millis, pi);

		}

		stopSelf();
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}