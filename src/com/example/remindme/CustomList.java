package com.example.remindme;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {

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

		if (System.currentTimeMillis() > Long.parseLong(time[position])) {
			
			image.setBackgroundColor(Color.parseColor("#000000"));
			reminder.setText("(Expired) " + reminderTitle[position]);
			reminder.setTextColor(Color.parseColor("#ffffff"));
			rDate.setTextColor(Color.parseColor("#ffffff"));
			rDay.setTextColor(Color.parseColor("#ffffff"));
			rTime.setTextColor(Color.parseColor("#ffffff"));

			rowView.setBackgroundResource(R.drawable.unchecked_reminder);
		}
		

		return rowView;
	}

	public CustomList(Activity context, String[] reminderTitle, String[] day,
			String[] time, String[] ids) {
		super(context, R.layout.list_style, reminderTitle);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.reminderTitle = reminderTitle;
		this.day = day;
		this.time = time;
		this.ids = ids;
	}

}
