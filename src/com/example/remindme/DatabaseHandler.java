package com.example.remindme;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "reminderManager";

	private static final String TABLE_REMINDER = "reminder";

	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_DESC = "description";
	private static final String KEY_TIME = "time";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMINDER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
				+ KEY_DESC + " TEXT," + KEY_TIME + " INTEGER" + ")";
		db.execSQL(CREATE_REMINDER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
		onCreate(db);
	}

	public void addReminder(Reminder reminder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_ID, String.valueOf(reminder.getId()));
		values.put(KEY_TITLE, reminder.getTitle());
		values.put(KEY_DESC, reminder.getDescription());
		values.put(KEY_TIME, String.valueOf(reminder.getMillis()));

		db.insert(TABLE_REMINDER, null, values);
		db.close();
	}

	public Reminder getReminder(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_REMINDER, new String[] { KEY_ID,
				KEY_TITLE, KEY_DESC, KEY_TIME }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Reminder retReminder = new Reminder(Integer.parseInt(cursor
				.getString(0)), cursor.getString(1), cursor.getString(2),
				Long.parseLong(cursor.getString(3)));
		// return contact
		return retReminder;
	}

	public int getReminderCountLessThan(long millis) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_REMINDER, new String[] { KEY_ID },
				KEY_TIME + "<=?", new String[] { String.valueOf(millis) },
				null, null, null, null);

		return cursor.getCount();

	}

	public ArrayList<Reminder> getReminderLessThan(long millis) {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Reminder> reminderList = new ArrayList<Reminder>();

		Cursor cursor = db
				.query(TABLE_REMINDER, new String[] { KEY_ID, KEY_TITLE,
						KEY_DESC, KEY_TIME }, KEY_TIME + "<=?",
						new String[] { String.valueOf(millis) }, null, null,
						null, null);
		if (cursor.moveToFirst()) {
			do {
				Reminder reminder = new Reminder(Integer.parseInt(cursor
						.getString(0)), cursor.getString(1),
						cursor.getString(2),
						Long.parseLong(cursor.getString(3)));
				reminderList.add(reminder);
			} while (cursor.moveToNext());
		}
		return reminderList;
	}

	public ArrayList<Reminder> getAllReminders() {
		ArrayList<Reminder> reminderList = new ArrayList<Reminder>();

		String selectQuery = "SELECT  * FROM " + TABLE_REMINDER + " ORDER BY "
				+ KEY_TIME + " DESC";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Reminder reminder = new Reminder(Integer.parseInt(cursor
						.getString(0)), cursor.getString(1),
						cursor.getString(2),
						Long.parseLong(cursor.getString(3)));
				reminderList.add(reminder);
			} while (cursor.moveToNext());
		}

		return reminderList;
	}

	public int getReminderCount() {
		String countQuery = "SELECT  * FROM " + TABLE_REMINDER;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	public void deleteReminder(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_REMINDER, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public int updateReminder(Reminder reminder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_DESC, reminder.getDescription());
		values.put(KEY_TITLE, reminder.getTitle());
		values.put(KEY_TIME, reminder.getMillis());

		// updating row
		return db.update(TABLE_REMINDER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(reminder.getId()) });
	}
}