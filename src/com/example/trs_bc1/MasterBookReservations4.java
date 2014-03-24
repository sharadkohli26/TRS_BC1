package com.example.trs_bc1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.example.trs_bc1.CustomDateDialogFrag.CustomDateDialogListenerReturns;
import com.example.trs_bc1.TimePickerDialogFrag.TimePickerDialogFragReturns;

public class MasterBookReservations4 extends SherlockFragmentActivity implements
		TimePickerDialogFragReturns, CustomDateDialogListenerReturns {

	private TRSDBAdapter mdbhelper;
	private Cursor mResCursor;

	SharedPreferences prefname;
	SharedPreferences.Editor editor;
	SharedPreferences userpreference;
	SharedPreferences.Editor usereditor;
	String UserPrefFileName;
	String loginid;

	int MBRMode;
	Calendar MBRDate;// mbr date is ust the date that is currently displayed in
						// advance tab....so waiting list is to be added to
						// today and not mbrdate
	Calendar MBRTime;//default value for date is 201,JAN,Sunday...
	int DefOccupancyTime;
	int DefWaitingTime;
	int NumWaiting;
	int MaxNumTables;// this is maximum number of tables
	int NumAdvance;

	MBR_Header Waiting_ll;
	MBR_Header Advance_ll;
	// TextView MBR_TV_HeaderWaiting;
	// TextView MBR_TV_HeaderAdvance;
	TextView MBR_TV_DateHeader;
	LinearLayout MBR_LO_Date;
	TextView MBR_TV_Date;
	LinearLayout MBR_LO_Time;
	TextView MBR_TV_TimeHeader;
	TextView MBR_TV_Time;
	TextView MBR_TV_Error;
	LinearLayout MBR_LO_Duration;
	EditText MBR_ET_Duration;
	LinearLayout MBR_LO_Waiting;
	EditText MBR_ET_Waiting;
	EditText MBR_ET_NumTables;
	EditText MBR_ET_NumGuests;
	EditText MBR_ET_GuestNames;
	EditText MBR_ET_ContactNos;

	// edit text maintains its own content on change of orientation

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_book_reservations4);

		prefname = getSharedPreferences(MainActivity.PreferenceName, 0);
		editor = prefname.edit();

		if (!CheckSetLoginIsAuth()) {
			// login is not set...
			Logout();
		}
		
		loadIntentVar();
		initClassVariables();
		SetViewMode(MBRMode);
	}
	

	public void loadIntentVar() {
		Bundle extras = getIntent().getExtras();// returns the intent that
		// started this activity,//extras not null already checked..
		UserPrefFileName = extras
				.getString(LiveReservations.EXTRA_UserPrefFileName);
		MBRMode = extras.getInt(LiveReservations.EXTRA_MBRMode);
		MBRDate = (Calendar) extras.get(LiveReservations.Extra_MBRDate);
		DefOccupancyTime = extras
				.getInt(LiveReservations.Extra_DefOccupancyTime);
		DefWaitingTime = extras.getInt(LiveReservations.Extra_DefWaitingTime);
		NumWaiting = extras.getInt(LiveReservations.Extra_NumWaiting);
		NumAdvance = extras.getInt(LiveReservations.Extra_NumAdvance);
		MaxNumTables = extras.getInt(LiveReservations.Extra_MaxNumTables);
		// variables that might not get passed in the intent like mbrtime
		if (extras.containsKey(LiveReservations.Extra_MBRTime)) {
			MBRTime = (Calendar) extras.get(LiveReservations.Extra_MBRTime);
		} else {
			MBRTime = Calendar.getInstance(TimeZone.getDefault());
		}
	}

	public void initClassVariables() {
		mdbhelper = new TRSDBAdapter(this);
		mdbhelper.open();

		userpreference = getSharedPreferences(UserPrefFileName, 0);
		usereditor = userpreference.edit();
		initAllViews();
	}


	public void initAllViews() {
		Waiting_ll = new MBR_Header(this, R.id.mbr_ll_headerwait,
				R.id.mbr_tv_headerwait, R.id.mbr_tv_waitupdate);
		Advance_ll = new MBR_Header(this, R.id.mbr_ll_headeradvance,
				R.id.mbr_tv_headeradvance, R.id.mbr_tv_advanceupdate);
		// MBR_TV_HeaderWaiting = (TextView) findViewById(R.id.mbr_waiting);
		// MBR_TV_HeaderAdvance = (TextView) findViewById(R.id.mbr_advance);
		MBR_TV_Error = (TextView) findViewById(R.id.mbr_tv_error);
		MBR_TV_DateHeader = (TextView) findViewById(R.id.mbr_tv_dateheader);
		MBR_LO_Date = (LinearLayout) findViewById(R.id.mbr_lo_date);
		MBR_TV_Date = (TextView) findViewById(R.id.mbr_tv_date);
		MBR_LO_Time = (LinearLayout) findViewById(R.id.mbr_lo_time);
		MBR_TV_TimeHeader = (TextView) findViewById(R.id.mbr_tv_timeheader);
		MBR_TV_Time = (TextView) findViewById(R.id.mbr_tv_time);
		MBR_LO_Duration = (LinearLayout) findViewById(R.id.mbr_lo_duration);
		MBR_ET_Duration = (EditText) findViewById(R.id.mbr_et_duration);
		MBR_LO_Waiting = (LinearLayout) findViewById(R.id.mbr_lo_waiting);
		MBR_ET_Waiting = (EditText) findViewById(R.id.mbr_et_waitingtime);
		MBR_ET_NumTables = (EditText) findViewById(R.id.mbr_et_numtables);
		MBR_ET_NumGuests = (EditText) findViewById(R.id.mbr_et_numguests);
		MBR_ET_GuestNames = (EditText) findViewById(R.id.mbr_et_guestname);
		MBR_ET_ContactNos = (EditText) findViewById(R.id.mbr_et_guestphone);

		Waiting_ll.MBR_TV_SubSet(Integer.toString(NumWaiting) + " | WaitTime:"
				+ Integer.toString(DefWaitingTime));
		Advance_ll.MBR_TV_SubSet(Integer.toString(NumAdvance));

		MBR_TV_Time.setText(MySimpleDateFormatTime(MBRTime));

	}
	

	public void MBR_Waiting(View view) {
		MBRMode = LiveReservations.MBR_Mode_Waiting;
		this.getIntent().putExtra(LiveReservations.EXTRA_MBRMode, MBRMode);
		SetViewMode(MBRMode);
	}

	public void MBR_Advance(View view) {
		MBRMode = LiveReservations.MBR_Mode_Advance;
		this.getIntent().putExtra(LiveReservations.EXTRA_MBRMode, MBRMode);
		SetViewMode(MBRMode);
	}
	
	public void SetViewMode(int m_MBRMode) {
		// check mode and set views
		if (m_MBRMode == LiveReservations.MBR_Mode_Waiting) {
			Waiting_ll.MBR_selected(this);
			Advance_ll.MBR_notselected(this);
			MBR_TV_DateHeader.setVisibility(View.GONE);
			MBR_LO_Date.setVisibility(View.GONE);
			MBR_LO_Time.setVisibility(View.GONE);
			MBR_LO_Duration.setVisibility(View.GONE);
			MBR_LO_Waiting.setVisibility(View.VISIBLE);

			MBR_ET_Waiting.setText(Integer.toString(DefWaitingTime));

		} else if (m_MBRMode == LiveReservations.MBR_Mode_Advance) {
			Advance_ll.MBR_selected(this);
			Waiting_ll.MBR_notselected(this);
			MBR_TV_DateHeader.setVisibility(View.VISIBLE);
			MBR_LO_Date.setVisibility(View.VISIBLE);
			MBR_LO_Time.setVisibility(View.VISIBLE);
			MBR_LO_Duration.setVisibility(View.VISIBLE);
			MBR_LO_Waiting.setVisibility(View.GONE);
			MBR_ET_Duration.setText(Integer.toString(DefOccupancyTime));
			MBR_TV_Date.setText(MySimpleDateFormat(MBRDate));
		}

	}

	public void MBR_DateDialog(View view) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CustomDateDialogFrag m_datedialog = CustomDateDialogFrag
				.newInstance(MBRDate);
		m_datedialog.show(ft, null);
	}

	public void MBR_DateInc(View view) {
		MBRDate.add(Calendar.DAY_OF_YEAR, 1);
		MBR_TV_Date.setText(MySimpleDateFormat(MBRDate));
		UpdateIntentCalendar(LiveReservations.Extra_MBRTime, MBRDate);

	}

	public void MBR_DateDec(View view) {
		MBRDate.add(Calendar.DAY_OF_YEAR, -1);
		MBR_TV_Date.setText(MySimpleDateFormat(MBRDate));
		UpdateIntentCalendar(LiveReservations.Extra_MBRTime, MBRDate);
	}

	public void MBR_ChangeResTime(View view) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		TimePickerDialogFrag m_timedialog = TimePickerDialogFrag
				.newInstance(MBRTime);
		m_timedialog.show(ft, null);
	}


	public void MBR_Set(View view) {
		// error check
		if (MBR_ET_NumTables.getText().toString().length() == 0
				|| MBR_ET_NumTables.getText().toString().equals("0")) {
			MBR_TV_Error.setText("Number of Tables is blank");
			MBR_TV_Error.setVisibility(View.VISIBLE);
			MBR_ET_NumTables.setFocusable(true);
			MBR_ET_NumTables.requestFocus();
		} else if (Integer.parseInt(MBR_ET_NumTables.getText().toString()) > MaxNumTables) {
			MBR_TV_Error.setText("#Tables exceeds Tables available("
					+ Integer.toString(MaxNumTables) + ")");
			MBR_TV_Error.setVisibility(View.VISIBLE);
			MBR_ET_NumTables.setFocusable(true);
			MBR_ET_NumTables.requestFocus();
		} else if (MBR_ET_NumGuests.getText().toString().length() == 0) {
			MBR_TV_Error.setText("Number of Guests is blank");
			MBR_TV_Error.setVisibility(View.VISIBLE);
			MBR_ET_NumGuests.setFocusable(true);
			MBR_ET_NumGuests.requestFocus();
		} else if (MBR_ET_GuestNames.getText().toString().length() == 0) {
			MBR_TV_Error.setText("Name of Guest is blank");
			MBR_TV_Error.setVisibility(View.VISIBLE);
			MBR_ET_GuestNames.setFocusable(true);
			MBR_ET_GuestNames.requestFocus();
		} else {
			// general check upcomplete
			if (MBRMode == LiveReservations.MBR_Mode_Waiting) {
				// call method for addding to waiting
				AddToWaiting();
			} 
			else if (MBRMode == LiveReservations.MBR_Mode_Advance) {
				// call method for addding to advance
				//error check for advance date and time should be greater than now not before
				//check date is equal to today or advance
				if(!AdvanceDateCheck()){
					//show advance date error decrease date. if advdate<today
					MBR_TV_Error.setText("Unable to advance book on a past date!");
					MBR_TV_Error.setVisibility(View.VISIBLE);										
				}
				else if(MBRDate.equals(TodayDate())){
					//it is today ...now check of time is before current time 
					//for later also check if res time lies in time at which res are allowed					
					if(AdvanceTimeCheck()){
						//that is for today the time specified is greater then current so proceed					
						AddToAdvance();
					}else{
						//error time
						MBR_TV_Error.setText("Unable to advance book on past time!");
						MBR_TV_Error.setVisibility(View.VISIBLE);						
					}
				}
				else{					
					AddToAdvance();
					//date is greater than today so proceed ...
					//for later do time check here that res time is in time allowed
				}
				
			}
		}
	}
	

	/**
	 * Add this reservation to waiting table in db...
	 * 
	 * @return return -1 for failure or rowid on success.
	 */
	public void AddToWaiting() {
		Calendar request_timedate = Calendar.getInstance(TimeZone.getDefault());
		Calendar res_date = Calendar.getInstance(TimeZone.getDefault());
		Calendar res_time = Calendar.getInstance(TimeZone.getDefault());
		res_date.set(Calendar.HOUR_OF_DAY, 0);
		res_date.set(Calendar.MINUTE, 0);
		res_date.set(Calendar.SECOND, 0);
		res_date.set(Calendar.MILLISECOND, 0);

		long request_ts = request_timedate.getTimeInMillis();
		long res_date_ts = res_date.getTimeInMillis();
		long res_time_ts = res_time.getTimeInMillis();

		int NumTables = Integer.parseInt(MBR_ET_NumTables.getText().toString());
		int NumGuests = Integer.parseInt(MBR_ET_NumGuests.getText().toString());
		int ExpWaiting = Integer.parseInt(MBR_ET_Waiting.getText().toString());
		String GuestNames = MBR_ET_GuestNames.getText().toString();
		String ContactNos = MBR_ET_ContactNos.getText().toString();
		long succorfail = mdbhelper.NewWaitingEntry(request_ts, res_date_ts,
				res_time_ts, res_time_ts, NumTables, NumGuests, ExpWaiting,
				GuestNames, ContactNos, (long) -1);
		if (succorfail == -1) {
			// fail
			DisplayToast("Waiting List Entry Failed! Please try again");
		} else {
			DisplayToast("Added to Waiting");
			// call method to return
			Intent returnIntent = new Intent();
			returnIntent.putExtra(LiveReservations.Extra_MBR_StatusReturn,
					LiveReservations.MBR_StatusRet_NewWaiting);
			setResult(RESULT_OK, returnIntent);
			finish();
		}
	}

	private void AddToAdvance() {
		// TODO Auto-generated method stub
		Calendar request_timedate = Calendar.getInstance(TimeZone.getDefault());
		Calendar res_date = TodayDate();
		Calendar res_time = Calendar.getInstance(TimeZone.getDefault());
		
		long request_ts = request_timedate.getTimeInMillis();
		long res_date_ts = res_date.getTimeInMillis();
		long res_time_ts = res_time.getTimeInMillis();
		
		//sanity check...since u update the date and all..bette rto save this way...
		MBRDate.set(Calendar.HOUR_OF_DAY, 0);
		MBRDate.set(Calendar.MINUTE, 0);
		MBRDate.set(Calendar.SECOND, 0);
		MBRDate.set(Calendar.MILLISECOND, 0);

		int NumTables = Integer.parseInt(MBR_ET_NumTables.getText().toString());
		int NumGuests = Integer.parseInt(MBR_ET_NumGuests.getText().toString());
		int ExpDuration = Integer.parseInt(MBR_ET_Duration.getText().toString());		
		String GuestNames = MBR_ET_GuestNames.getText().toString();
		String ContactNos = MBR_ET_ContactNos.getText().toString();
		long succorfail = mdbhelper.NewAdvanceEntry(request_ts, res_date_ts, res_time_ts, MBRDate.getTimeInMillis(), MBRTime.getTimeInMillis(), NumTables, NumGuests, ExpDuration, GuestNames, ContactNos); 		
		if (succorfail == -1) {
			// fail
			DisplayToast("Advance booking failed, please try again!");
		} else {
			DisplayToast("Added to Advance");
			// call method to return
			Intent returnIntent = new Intent();
			returnIntent.putExtra(LiveReservations.Extra_MBR_StatusReturn,LiveReservations.MBR_StatusRet_NewAdvance);
			setResult(RESULT_OK, returnIntent);
			finish();
		}		
	}

	private boolean AdvanceTimeCheck() {
		Calendar currtime=Calendar.getInstance(TimeZone.getDefault());
		currtime.set(2012, Calendar.JANUARY, Calendar.SUNDAY);
		Log.w("AdvTimeCurr--",MySimpleTimeDateFormat(currtime));
		Log.w("AdvTimeRES--",MySimpleTimeDateFormat(MBRTime));
		if(MBRTime.after(currtime)){
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	private boolean AdvanceDateCheck() {
		if(MBRDate.equals(TodayDate())||MBRDate.after(TodayDate())){
			return true;
		}
		// TODO Auto-generated method stub
		return false;
	}

	public void MBR_Cancel(View view) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(LiveReservations.Extra_MBR_StatusReturn,LiveReservations.MBR_StatusRet_NoAction);
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	protected boolean CheckSetLoginIsAuth() {
		if (prefname.getBoolean(MainActivity.Pref_IsAuthenticated, false) == false) {
			// redundant security feature....if no user log in and u land on
			// this activity
			// go to the base activity
			return false;
		}
		Bundle extras = getIntent().getExtras();// returns the intent that
												// started this activity
		if (extras == null) {
			return false;
		}
		if (extras.containsKey(MainActivity.EXTRA_loginid)) {
			String mloginid = extras.getString(MainActivity.EXTRA_loginid);
			if (mloginid.isEmpty() || mloginid.equalsIgnoreCase("-1")) {
				return false;
			}
			loginid = mloginid;
			return true;
		}
		return false;
	}

	public void Logout() {
		editor.putBoolean(MainActivity.EXTRA_IsAuthenticated, false);
		if (prefname.contains(MainActivity.EXTRA_loginid)) {
			editor.remove(MainActivity.EXTRA_loginid);
		}
		editor.commit();
		// Intent intent = new Intent(this, MainActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
		// flag_activity_clear_task for api >=11
		// startActivity(intent);
		// log out ke saath panga hai ..just finish..
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater()
				.inflate(R.menu.master_book_reservations4, menu);
		return true;
	}

	protected void DisplayToast(CharSequence text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public String MySimpleDateFormatTime(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
		return fmt.format(m_timedate.getTime());
	}

	public String MySimpleDateFormat(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd");
		return fmt.format(m_timedate.getTime());
	}
	
	public String MySimpleTimeDateFormat(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd, HH:mm");
		return fmt.format(m_timedate.getTime());
	}


	@Override
	public void CustomDateDialogReturn(int year, int month, int day) {
		// TODO Auto-generated method stub
		MBRDate.set(year, month, day);
		MBRDate.set(Calendar.HOUR_OF_DAY, 0);
		MBRDate.set(Calendar.MINUTE, 0);
		MBRDate.set(Calendar.SECOND, 0);
		MBRDate.set(Calendar.MILLISECOND, 0);
		MBR_TV_Date.setText(MySimpleDateFormat(MBRDate));
		UpdateIntentCalendar(LiveReservations.Extra_MBRTime, MBRDate);
	}

	@Override
	public void TimePickerDialogReturn(int hourOfDay, int minute) {
		MBRTime.set(Calendar.HOUR_OF_DAY, hourOfDay);			
		MBRTime.set(Calendar.MINUTE, minute);
		MBRTime.set(Calendar.SECOND,0);
		MBRTime.set(Calendar.MILLISECOND,0);
		MBRTime.set(2012,Calendar.JANUARY, Calendar.SUNDAY);
		// update elsewhere?
		MBR_TV_Time.setText(MySimpleDateFormatTime(MBRTime));
		UpdateIntentCalendar(LiveReservations.Extra_MBRTime, MBRTime);
	}

	public void UpdateIntentCalendar(String extra_str, Calendar c) {
		Intent newIntent = getIntent();
		newIntent.putExtra(extra_str, c);
		setIntent(newIntent);
	}
	
	public Calendar TodayDate() {
		Calendar res_date = Calendar.getInstance(TimeZone.getDefault());
		res_date.set(Calendar.HOUR_OF_DAY, 0);
		res_date.set(Calendar.MINUTE, 0);
		res_date.set(Calendar.SECOND, 0);
		res_date.set(Calendar.MILLISECOND, 0);
		return res_date;
	}

}

class MBR_Header {
	LinearLayout container;
	TextView header;
	TextView sub;

	public MBR_Header(MasterBookReservations4 p_activity, int contid,
			int headid, int subid) {
		container = (LinearLayout) p_activity.findViewById(contid);
		header = (TextView) p_activity.findViewById(headid);
		sub = (TextView) p_activity.findViewById(subid);
	}

	public void MBR_selected(MasterBookReservations4 p_activity) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_selected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,
				LinearLayout.LayoutParams.MATCH_PARENT, 2f));
		header.setTextAppearance(p_activity, R.style.MainTextViewSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewSelected);
	}

	public void MBR_notselected(MasterBookReservations4 p_activity) {
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_notselected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,
				LinearLayout.LayoutParams.MATCH_PARENT, 1f));
		header.setTextAppearance(p_activity, R.style.MainTextViewNotSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewNotSelected);
	}

	public void MBR_TV_SubSet(String text) {
		sub.setText(text);
	}

}
