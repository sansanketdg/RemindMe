package com.example.remindme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class Description extends Service {

	String title, text;
	int requestCode;
	private static final int NOTIF_CODE = 0;

	DatabaseHandler db = new DatabaseHandler(this);

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		stopSelf();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		this.requestCode = intent.getIntExtra("requestCode", -1);
		if (requestCode == -1)
			stopSelf();

		int count = db.getReminderCountLessThan(System.currentTimeMillis());
		if (count == 1) {
			this.title = intent.getStringExtra("tag");
			this.text = intent.getStringExtra("description");
		} else {
			this.title = "Remind ME";
			this.text = "" + count + " pending reminders";
		}

		Intent showAllFinally = new Intent(this, AlarmTrigger.class);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(this.title)
				.setContentText(this.text)
				.setAutoCancel(true)
				.setContentIntent(
						PendingIntent.getActivity(this, this.requestCode,
								showAllFinally,
								PendingIntent.FLAG_UPDATE_CURRENT))
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_ALARM));
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIF_CODE, mBuilder.build());

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
