package com.example.trs_bc1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LR_Current_CustomCursorAdapter2 extends SimpleCursorAdapter {

	private int layout;
	Calendar currenttime_ts;
	int ActiveViewAs;
	int status;

	public LR_Current_CustomCursorAdapter2(Context context, int layout,
			Cursor c, String[] from, int[] to, int mActiveViewAs, int mstatus) {
		super(context, layout, c, from, to);
		this.layout = layout;
		currenttime_ts = Calendar.getInstance(TimeZone.getDefault());
		ActiveViewAs = mActiveViewAs;
		status=mstatus;
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
		int lr_sysblack = context.getResources().getColor(R.color.sysBlack);
		int lr_gray = context.getResources().getColor(R.color.sysGray);
		int lr_activegreen = context.getResources().getColor(R.color.DarkGreen);

		// these initialisations change depeing on whether ListView or GridView
		TextView tv_numtables;
		TextView tv_numguests;
		TextView tv_starttime;
		TextView tv_guestname;
		TextView tv_guestcontactno;
		TextView tv_timeelapsed;
		LinearLayout ll_guestname;			
		
		if (ActiveViewAs == LiveReservations.GridViewAsActive) {
			
			tv_numtables = (TextView) v.findViewById(R.id.lr_curr_gv_NumTables);			
			tv_numguests = (TextView) v.findViewById(R.id.lr_curr_gv_NumGuests);			
			tv_starttime = (TextView) v.findViewById(R.id.lr_curr_gv_StartTime);			
			tv_guestname = (TextView) v.findViewById(R.id.lr_curr_gv_GuestName);			
			tv_guestcontactno = (TextView) v
					.findViewById(R.id.lr_curr_gv_ContactNo);			
			tv_timeelapsed = (TextView) v
					.findViewById(R.id.lr_curr_gv_TimeElapsed);
			ll_guestname = (LinearLayout) v
					.findViewById(R.id.lr_curr_gv_lrguestname);			
		} else {			
			tv_numtables = (TextView) v.findViewById(R.id.lr_curr_lv_NumTables);
			tv_numguests = (TextView) v.findViewById(R.id.lr_curr_lv_NumGuests);
			tv_starttime = (TextView) v.findViewById(R.id.lr_curr_lv_StartTime);
			tv_guestname = (TextView) v.findViewById(R.id.lr_curr_lv_GuestName);
			tv_guestcontactno = (TextView) v
					.findViewById(R.id.lr_curr_lv_ContactNo);
			tv_timeelapsed = (TextView) v
					.findViewById(R.id.lr_curr_lv_TimeElapsed);
			ll_guestname = (LinearLayout) v
					.findViewById(R.id.lr_curr_lv_lrguestname);
		}

		int numtables = c.getInt(c.getColumnIndex(TRSDBAdapter.TBC_NumTables));
		int numguests = c.getInt(c.getColumnIndex(TRSDBAdapter.TBC_NumGuests));
		long starttime_ts = c.getLong(c.getColumnIndex(TRSDBAdapter.TBC_Time));
		long endtime_ts = c.getLong(c.getColumnIndex(TRSDBAdapter.TBC_EndTime));

		//check elapsed time if it is live reservations
		if(status==LiveReservations.CurrentLive){			
			int elpmin = (int) (currenttime_ts.getTimeInMillis()/60000 - starttime_ts/60000);
			if (elpmin>c.getInt(c.getColumnIndex((TRSDBAdapter.TBC_ExpDuration)))){
				//show as flagged so change background
				//v.setBackground(context.getResources().getDrawable(R.drawable.res_lv_defaultattention));
				v.setBackgroundResource(R.drawable.res_lvattention);
			}
		}
		
		
		String guestname = c.getString(c
				.getColumnIndex(TRSDBAdapter.TBC_GuestName));
		String contactno = c.getString(c
				.getColumnIndex(TRSDBAdapter.TBC_GuestContactNo));

		Spannable TextToSpan;
		String Textstr;

		// show table or tables depending on how many tables.default is 1

		if (numtables > 1) {
			Textstr = Integer.toString(numtables) + " Tables";
			TextToSpan = new SpannableString(Textstr);
			int Tstart = Textstr.indexOf("T");
			TextToSpan
					.setSpan(new RelativeSizeSpan(.8f), Tstart, Tstart + 6, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Tstart,
					Tstart + 6, 0);// set color
			TextToSpan.setSpan(new RelativeSizeSpan(1.5f), 0, Tstart - 1, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Tstart - 1, 0);// set color
		} else {
			Textstr = Integer.toString(numtables) + " Table";
			TextToSpan = new SpannableString(Textstr);
			int Tstart = Textstr.indexOf("T");
			TextToSpan
					.setSpan(new RelativeSizeSpan(.8f), Tstart, Tstart + 5, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Tstart,
					Tstart + 5, 0);// set color

			TextToSpan.setSpan(new RelativeSizeSpan(1.5f), 0, Tstart - 1, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Tstart - 1, 0);// set color
		}
		tv_numtables.setText(TextToSpan);

		if (numguests > 1) {
			Textstr = Integer.toString(numguests) + " Guests";
			TextToSpan = new SpannableString(Textstr);
			int Gstart = Textstr.indexOf("G");
			TextToSpan
					.setSpan(new RelativeSizeSpan(.8f), Gstart, Gstart + 6, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Gstart,
					Gstart + 6, 0);// set color

			TextToSpan.setSpan(new RelativeSizeSpan(1.5f), 0, Gstart - 1, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Gstart - 1, 0);// set color
		} else {
			Textstr = Integer.toString(numguests) + " Guest";
			TextToSpan = new SpannableString(Textstr);
			int Gstart = Textstr.indexOf("G");
			TextToSpan
					.setSpan(new RelativeSizeSpan(.8f), Gstart, Gstart + 5, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Gstart,
					Gstart + 5, 0);// set color

			TextToSpan.setSpan(new RelativeSizeSpan(1.5f), 0, Gstart - 1, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Gstart - 1, 0);// set color
		}
		tv_numguests.setText(TextToSpan);

		// set Start time
		Calendar starttime_cal = Calendar.getInstance();
		starttime_cal.setTimeInMillis(starttime_ts);
		Textstr = MySimpleDateFormatTime(starttime_cal).trim() + " (S)";
		int Bracstart = Textstr.indexOf("(");
		TextToSpan = new SpannableString(Textstr);
		TextToSpan.setSpan(new RelativeSizeSpan(.8f), Bracstart, Bracstart + 3,
				0);
		if (endtime_ts == (long) TRSDBAdapter.CurrentLive)
			TextToSpan.setSpan(new ForegroundColorSpan(lr_activegreen),
					Bracstart, Bracstart + 3, 0);// set color
		else
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Bracstart,
					Bracstart + 3, 0);// set color

		TextToSpan.setSpan(new RelativeSizeSpan(1.4f), 0, Bracstart - 1, 0);
		TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
				Bracstart - 1, 0);// set color

		tv_starttime.setText(TextToSpan);

		// time elapsed
		// show time elapsed
		if (endtime_ts == (long) TRSDBAdapter.CurrentLive) {
			long difference = currenttime_ts.getTimeInMillis() - starttime_ts;
			long x = difference / 1000;
			int seconds = (int) (x % 60);
			x /= 60;
			int minutes = (int) (x % 60);
			x /= 60;
			int hours = (int) (x % 24);
			x /= 24;
			int days = (int) x;
			if (days > 1) {
				Textstr = Integer.toString(days) + " days";
			} else if (days == 1) {
				Textstr = Integer.toString(days) + " day";
			} else if (hours > 0) {
				Textstr = Integer.toString(hours) + "H "
						+ Integer.toString(minutes) + "m";
			} else {
				Textstr = Integer.toString(minutes) + "m";
			}
			Textstr = Textstr + " (A)";
			TextToSpan = new SpannableString(Textstr);
			int Statusstart = Textstr.indexOf("(");
			TextToSpan.setSpan(new RelativeSizeSpan(.8f), Statusstart,
					Statusstart + 3, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_activegreen),
					Statusstart, Statusstart + 3, 0);// set color

			TextToSpan.setSpan(new RelativeSizeSpan(1.4f), 0, Statusstart - 1,
					0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Statusstart - 1, 0);// set color
			tv_timeelapsed.setText(TextToSpan);
		} else {
			Calendar endtime_cal = Calendar.getInstance();
			endtime_cal.setTimeInMillis(endtime_ts);
			Textstr = MySimpleDateFormatTime(endtime_cal).trim() + " (C)";
			Bracstart = Textstr.indexOf("(");
			TextToSpan = new SpannableString(Textstr);
			TextToSpan.setSpan(new RelativeSizeSpan(.8f), Bracstart,
					Bracstart + 3, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Bracstart,
					Bracstart + 3, 0);// set color

			TextToSpan.setSpan(new RelativeSizeSpan(1.4f), 0, Bracstart - 1, 0);
			TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
					Bracstart - 1, 0);// set color
			tv_timeelapsed.setText(TextToSpan);
		}

		// show name and phone symbol if each exists
		if (guestname == null || guestname.isEmpty()) {
			tv_guestname.setText("!!!");
			tv_guestname.setTextColor(lr_gray);
			tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			// tv_guestname.setVisibility(View.GONE);
			// ll_guestname.setVisibility(View.GONE);
		} else {
			tv_guestname.setText(guestname);
			tv_guestname.setTextColor(lr_sysblack);
			tv_guestname.setVisibility(View.VISIBLE);
			if (contactno == null || contactno.isEmpty()) {
				tv_guestname
						.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				tv_guestcontactno.setText(null);
			} else {
				tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,
						R.drawable.whiteback_greenphone, 0);
				tv_guestcontactno.setText(contactno);
			}
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
