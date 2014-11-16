package com.example.remindme;

public class Reminder {

	int id;
	String title, description;
	long millis;

	public Reminder(int id, String title, String description, long millis) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.title = new String();
		this.title = "" + title;
		this.description = new String();
		this.description = "" + description;
		this.millis = millis;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getMillis() {
		return this.millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

}
