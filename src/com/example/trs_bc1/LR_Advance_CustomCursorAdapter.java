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

public class LR_Advance_CustomCursorAdapter extends SimpleCursorAdapter {

	private int layout;
	Calendar currenttime_ts;

	public LR_Advance_CustomCursorAdapter(Context context, int layout,
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
		int lr_sysblack = context.getResources().getColor(R.color.sysBlack);
		int lr_gray = context.getResources().getColor(R.color.sysGray);
		int lr_activegreen = context.getResources().getColor(R.color.DarkGreen);

		TextView tv_separator = (TextView) v.findViewById(R.id.lr_adv_separator);
		TextView tv_numtables = (TextView) v
				.findViewById(R.id.lr_adv_lv_NumTables);
		TextView tv_numguests = (TextView) v
				.findViewById(R.id.lr_adv_lv_NumGuests);

		TextView tv_resfor = (TextView) v.findViewById(R.id.lr_adv_lv_ResFor);
	
		TextView tv_guestname = (TextView) v
				.findViewById(R.id.lr_adv_lv_GuestName);
		TextView tv_guestcontactno = (TextView) v
				.findViewById(R.id.lr_adv_lv_ContactNo);

		LinearLayout ll_guestname = (LinearLayout) v
				.findViewById(R.id.lr_adv_lv_lrguestname);

		int numtables = c.getInt(c.getColumnIndex(TRSDBAdapter.TBA_NumTables));
		int numguests = c.getInt(c.getColumnIndex(TRSDBAdapter.TBA_NumGuests));
		long resfor_ts = c.getLong(c.getColumnIndex(TRSDBAdapter.TBA_ForTime));
		long resdatefor_ts_curr = c.getLong(c.getColumnIndex(TRSDBAdapter.TBA_ForDate));
		long resdatefor_ts_prev=-1;//initialised below... 
		long convcurrent = c.getLong(c.getColumnIndex(TRSDBAdapter.TBW_ConvCurrent));
		
		//intentionally not storing any data abt res date
		

		String guestname = c.getString(c.getColumnIndex(TRSDBAdapter.TBA_GuestName));
		String contactno = c.getString(c.getColumnIndex(TRSDBAdapter.TBA_GuestContactNo));

		Spannable TextToSpan;
		String Textstr;
		int Bracstart;			
		
		//check to show separator or not...		
		if(c.getPosition()>0 && c.moveToPrevious()){
			resdatefor_ts_prev=c.getLong(c.getColumnIndex(TRSDBAdapter.TBA_ForDate));
			c.moveToNext();
		}		
		
		
		 // enable section heading if it's the first one, or 
	    // different from the previous one
	    if (resdatefor_ts_prev==-1 || resdatefor_ts_prev!=resdatefor_ts_curr) {
	    	tv_separator.setVisibility(View.VISIBLE);	    	
	    	//set current date as header
	    	Calendar resdatefor_curr = Calendar.getInstance();resdatefor_curr.setTimeInMillis(resdatefor_ts_curr);
	    	tv_separator.setText(MySimpleDateFormat(resdatefor_curr));	    	
	    } else {
	    	tv_separator.setVisibility(View.GONE);
	    }
	    

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
		tv_numtables.setTag(Integer.valueOf(numtables));

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
		tv_numguests.setTag(Integer.valueOf(numguests));
		tv_numguests.setText(TextToSpan);

		// set res time for
		
		 Calendar restime_cal = Calendar.getInstance();
		 restime_cal.setTimeInMillis(resfor_ts);
		 Textstr=MySimpleDateFormatTime(restime_cal).trim()+" (For)";
		 Bracstart = Textstr.indexOf("("); TextToSpan = new
		 SpannableString(Textstr); TextToSpan.setSpan(new
		 RelativeSizeSpan(.8f), Bracstart, Bracstart+5, 0);
		 if(convcurrent==(long) TRSDBAdapter.AdvanceAlive)
			 TextToSpan.setSpan(new ForegroundColorSpan(lr_activegreen), Bracstart, Bracstart+5, 0);// set color 
		 else 
			 TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Bracstart, Bracstart+5, 0);// set color
		  
		  TextToSpan.setSpan(new RelativeSizeSpan(1.4f), 0, Bracstart-1, 0);
		  TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0, Bracstart-1, 0);// set color tv_resfor.setText(TextToSpan);
		  tv_resfor.setText(TextToSpan);		  
		 

/*		// set Start time
		Calendar starttime_cal = Calendar.getInstance();
		starttime_cal.setTimeInMillis(starttime_ts);
		Textstr = MySimpleDateFormatTime(starttime_cal).trim() + " (S)";
		Bracstart = Textstr.indexOf("(");
		TextToSpan = new SpannableString(Textstr);
		TextToSpan.setSpan(new RelativeSizeSpan(.8f), Bracstart, Bracstart + 3,
				0);
		if (endtime_ts == (long) TRSDBAdapter.WaitingAlive)
			TextToSpan.setSpan(new ForegroundColorSpan(lr_activegreen),
					Bracstart, Bracstart + 3, 0);// set color
		else
			TextToSpan.setSpan(new ForegroundColorSpan(lr_gray), Bracstart,
					Bracstart + 3, 0);// set color

		TextToSpan.setSpan(new RelativeSizeSpan(1.4f), 0, Bracstart - 1, 0);
		TextToSpan.setSpan(new ForegroundColorSpan(lr_sysblack), 0,
				Bracstart - 1, 0);// set color

		tv_starttime.setText(TextToSpan);*/
/*
		// time elapsed
		// show time elapsed
		if (endtime_ts == (long) TRSDBAdapter.WaitingAlive) {
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
*/
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
			tv_guestcontactno.setText(contactno);
			// if alive then display phone if alredy closed or cancelled show
			// other
			if (convcurrent == TRSDBAdapter.AdvanceAlive) {
				if (contactno == null || contactno.isEmpty()) {
					tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,0, 0);
				} else {
					tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.whiteback_greenphone, 0);
				}
			} else if (convcurrent == TRSDBAdapter.AdvanceNotAliveNotConvCurrent) {
				tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.redcross_lightback, 0);
			} else if (convcurrent > 0) {
				tv_guestname.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.greenright_lightback, 0);
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
	
	public String MySimpleDateFormat(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd");
		return fmt.format(m_timedate.getTime());
	}

}
