package com.example.trs_bc1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LR_Current_CustomCursorAdapter extends SimpleCursorAdapter {
	
	private int layout;
	Calendar currenttime_ts;

	public LR_Current_CustomCursorAdapter(Context context, int layout,
			Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.layout = layout;
		currenttime_ts = Calendar.getInstance(TimeZone.getDefault());
		// TODO Auto-generated constructor stub
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Cursor c = getCursor();
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layout, parent, false);
		return v;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {

		TextView tv_numtables = (TextView) v
				.findViewById(R.id.lr_curr_lv_NumTables);
		TextView tv_numtablesheader = (TextView) v
				.findViewById(R.id.lr_curr_lv_NumTablesHeader);

		TextView tv_numguests = (TextView) v
				.findViewById(R.id.lr_curr_lv_NumGuests);
		TextView tv_numguestsheader = (TextView) v
				.findViewById(R.id.lr_curr_lv_NumGuestsHeader);

		TextView tv_starttime = (TextView) v
				.findViewById(R.id.lr_curr_lv_StartTime);

		TextView tv_guestname = (TextView) v
				.findViewById(R.id.lr_curr_lv_GuestName);
		TextView tv_guestcontactno = (TextView) v
				.findViewById(R.id.lr_curr_lv_ContactNo);
		TextView tv_timeelapsed = (TextView) v
				.findViewById(R.id.lr_curr_lv_TimeElapsed);

		int numtables = c.getInt(c.getColumnIndex(TRSDBAdapter.TBC_NumTables));
		int numguests = c.getInt(c.getColumnIndex(TRSDBAdapter.TBC_NumGuests));
		long starttime_ts = c.getLong(c.getColumnIndex(TRSDBAdapter.TBC_Time));
		String guestname = c.getString(c
				.getColumnIndex(TRSDBAdapter.TBC_GuestName));
		String contactno = c.getString(c
				.getColumnIndex(TRSDBAdapter.TBC_GuestContactNo));

		
		
		//show table or tables depending on how many tables.default is 1
		tv_numtables.setText(Integer.toString(numtables));
		if(numtables>1){
			tv_numtablesheader.setText(context.getString(R.string.lr_curr_lvrow_Tables));
		}
		
		tv_numguests.setText(Integer.toString(numguests));
		if (numguests>1){
			tv_numguestsheader.setText(context.getString(R.string.lr_curr_lvrow_Guests));
		}
		
		//set time
		Calendar starttime_cal = Calendar.getInstance();
		starttime_cal.setTimeInMillis(starttime_ts);
		tv_starttime.setText(MySimpleDateFormatTime(starttime_cal));
		
		//show name and phone symbol if each exists
		if (guestname == null || guestname.isEmpty()) {
			tv_guestname.setVisibility(View.GONE);
		} else {
			tv_guestname.setText(guestname);
			tv_guestname.setVisibility(View.VISIBLE);
			if (contactno == null || contactno.isEmpty()) {
				tv_guestname
						.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			} else {
				tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.whiteback_greenphone, 0);
				tv_guestcontactno.setText(contactno);
			}
		}
		
		//show time elapsed
		long difference = currenttime_ts.getTimeInMillis()-starttime_ts;
		long x =difference/1000;	    
	    int seconds = (int) (x % 60);
	    x /= 60;
	    int minutes = (int) (x % 60);
	    x /= 60;
	    int hours = (int) (x % 24);
	    x /= 24;
	    int days = (int) x;
	    if (days>1){
	    	tv_timeelapsed.setText(Integer.toString(days)+ " days");
	    }
	    else if (days==1){
	    	tv_timeelapsed.setText(Integer.toString(days)+ " day");
	    }
	    else if(hours > 0){
	    	tv_timeelapsed.setText(Integer.toString(hours)+ " H " + Integer.toString(minutes)+" m");
	    }
	    else{
	    	tv_timeelapsed.setText(Integer.toString(minutes)+" m");
	    }

		/**
		 * Next set the name of the entry.
		 */		
	}
	
	public String MySimpleDateFormatTime(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
		return fmt.format(m_timedate.getTime());
	}

}
