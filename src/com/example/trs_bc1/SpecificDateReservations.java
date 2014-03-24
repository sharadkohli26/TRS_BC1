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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.example.trs_bc1.CustomDateDialogFrag.CustomDateDialogListenerReturns;
import com.example.trs_bc1.CustomNavigationListListener_SDR.CustomNavigationListReturns_SDR;

public class SpecificDateReservations extends SherlockFragmentActivity
		implements CustomNavigationListReturns_SDR,
		CustomDateDialogListenerReturns {
	// if specific date is todays date then go to live reservations only...
	// live reservation is the parent activity
	SharedPreferences prefname;
	SharedPreferences.Editor editor;

	SharedPreferences userpreference;
	SharedPreferences.Editor usereditor;
	String UserPrefFileName;
	String loginid;

	private TRSDBAdapter mdbhelper;
	private Cursor mResCursor;

	Calendar TodayViewingTimeDate;
	Calendar CurrentViewingTimeDate;
	Calendar NewViewingTimeDate;

	CustomNavigationListAdapter m_CustomNavigationListAdapter;
	CustomNavigationListListener_SDR m_CustomNavigationListListener;
	String[] NavList_Titles;
	String[] NavList_SubTitles;

	SDR_TV final_ll;
	SDR_TV wait_ll;
	SDR_TV advance_ll;
	TextView dummytext;
	ListView sdr_lv;
	String cwa_viewstate;

	int NumFinal;
	int NumWaiting;
	int NumAdvance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_date_reservations);
		prefname = getSharedPreferences(MainActivity.PreferenceName, 0);
		editor = prefname.edit();
		if (!CheckSetLoginIsAuth()) {
			// login is not set...
			Logout();
		}
		// load the intent recieved
		loadIntentVar();

		initClassVariables();
		initSupportActionBar();
		initSupportViews();
	}

	public void loadIntentVar() {
		Bundle extras = getIntent().getExtras();// returns the intent that
		// started this activity,//extras not null already checked..
		CurrentViewingTimeDate = (Calendar) extras
				.get(LiveReservations.EXTRA_SpecificViewingTimeDate);
		TodayViewingTimeDate = (Calendar) extras
				.get(LiveReservations.EXTRA_TodayViewingTimeDate);
		UserPrefFileName = extras
				.getString(LiveReservations.EXTRA_UserPrefFileName);
		/*
		 * if(extras.containsKey(LiveReservations.EXTRA_SpecificViewingTimeDate))
		 * { SpecificViewingTimeDate=(Calendar)
		 * extras.get(LiveReservations.EXTRA_SpecificViewingTimeDate); }
		 */

	}

	public void initClassVariables() {
		userpreference = getSharedPreferences(UserPrefFileName, 0);
		usereditor = userpreference.edit();
		NewViewingTimeDate = Calendar.getInstance(TimeZone.getDefault());

		NumFinal = 0;
		NumWaiting = 0;
		NumAdvance = 0;

		mdbhelper = new TRSDBAdapter(this);
		mdbhelper.open();

	}

	public void initSupportActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		NavList_Titles = getResources().getStringArray(R.array.CR_Title);
		// NavList_SubTitles=getResources().getStringArray(R.array.LR_SubTitle);

		// diferrent from LR
		String CurrDate = MySimpleDateFormat(CurrentViewingTimeDate);
		String[] temp = { CurrDate, MySimpleDateFormat(TodayViewingTimeDate),
				"" };
		NavList_SubTitles = temp;

		Context context = getSupportActionBar().getThemedContext();
		m_CustomNavigationListAdapter = new CustomNavigationListAdapter(
				context, NavList_Titles, NavList_SubTitles, NavList_Titles[0]);
		m_CustomNavigationListListener = new CustomNavigationListListener_SDR(
				this, NavList_Titles);
		getSupportActionBar().setListNavigationCallbacks(
				m_CustomNavigationListAdapter, m_CustomNavigationListListener);
	}

	public void initSupportViews() {
		final_ll = new SDR_TV(this, R.id.sdr_ll_final, R.id.sdr_tv_final,
				R.id.sdr_tv_final_update);
		wait_ll = new SDR_TV(this, R.id.sdr_ll_wait, R.id.sdr_tv_wait,
				R.id.sdr_tv_wait_update);
		advance_ll = new SDR_TV(this, R.id.sdr_ll_advance, R.id.sdr_tv_advance,
				R.id.sdr_tv_advance_update);
		// do the default initialisations...IN CASE DB CONNECTION FAILS...you
		// can show warning and show default inits
		final_ll.SDR_TV_setSubText(Integer.toString(NumFinal));
		wait_ll.SDR_TV_setSubText(Integer.toString(NumWaiting));
		advance_ll.SDR_TV_setSubText(Integer.toString(NumAdvance));

		dummytext = (TextView) findViewById(R.id.sdr_tv_dummytext);// properties
																	// of dummy
																	// text vary
																	// according
																	// to the
																	// view
		sdr_lv = (ListView) findViewById(R.id.sdr_listview);
		sdr_lv.setEmptyView(dummytext);

		SetActiveView();
		// Curr_onClickListener();
		// Wait_onClickListener();
		// Advance_onClickListener();
	}

	public void SetActiveView() {
		Bundle extras = getIntent().getExtras();// returns the intent that
		// started this activity

		if (extras.containsKey(LiveReservations.EXTRA_CurrentWaitingAdvance)) {
			cwa_viewstate = extras
					.getString(LiveReservations.EXTRA_CurrentWaitingAdvance);
			if (cwa_viewstate.equals(LiveReservations.CWA_VIEWSTATE_CURRENT)) {
				LO_Current(this
						.findViewById(R.layout.activity_specific_date_reservations));
			} else if (cwa_viewstate
					.equals(LiveReservations.CWA_VIEWSTATE_WAIT)) {
				LO_Wait(this
						.findViewById(R.layout.activity_specific_date_reservations));
			} else if (cwa_viewstate
					.equals(LiveReservations.CWA_VIEWSTATE_ADVANCE)) {
				LO_Advance(this
						.findViewById(R.layout.activity_specific_date_reservations));
			}
		} else {
			LO_Current(this
					.findViewById(R.layout.activity_specific_date_reservations));
			UpdateNumWaitingSQLite();
			UpdateNumAdvanceSQLite();
		}
	}

	private void UpdateNumAdvanceSQLite() {
		mResCursor = mdbhelper.fetchDetailsAdvance(CurrentTimeDateAsDate(
				CurrentViewingTimeDate).getTimeInMillis());
		NumAdvance = mResCursor.getCount();
		advance_ll.SDR_TV_setSubText(Integer.toString(NumAdvance));
	}

	public void UpdateNumWaitingSQLite() {
		mResCursor = mdbhelper.fetchDetailsWaiting(CurrentTimeDateAsDate(
				CurrentViewingTimeDate).getTimeInMillis());
		NumWaiting = mResCursor.getCount();
		wait_ll.SDR_TV_setSubText(Integer.toString(NumWaiting));
	}

	public void LO_Current(View view) {
		// set curr and unset the rest
		// also update the adapter list..later...
		final_ll.SDR_TV_selected(this);
		wait_ll.SDR_TV_notselected(this);
		advance_ll.SDR_TV_notselected(this);
		dummytext.setText("No Reservations");
		cwa_viewstate = LiveReservations.CWA_VIEWSTATE_CURRENT;
		UpdateIntent_String(LiveReservations.EXTRA_CurrentWaitingAdvance,
				cwa_viewstate);

		// set the close reservations text view
		// ShowCurrClosed(TodayDate().getTimeInMillis());
		// set the active reservations...
		fillDataFinal(CurrentTimeDateAsDate(CurrentViewingTimeDate)
				.getTimeInMillis());
	}

	public void LO_Wait(View view) {
		wait_ll.SDR_TV_selected(this);
		final_ll.SDR_TV_notselected(this);
		advance_ll.SDR_TV_notselected(this);
		cwa_viewstate = LiveReservations.CWA_VIEWSTATE_WAIT;
		UpdateIntent_String(LiveReservations.EXTRA_CurrentWaitingAdvance,
				cwa_viewstate);
		dummytext.setText("No Waiting!");
		fillDataWait(CurrentTimeDateAsDate(CurrentViewingTimeDate)
				.getTimeInMillis());
	}

	public void LO_Advance(View view) {
		advance_ll.SDR_TV_selected(this);
		final_ll.SDR_TV_notselected(this);
		wait_ll.SDR_TV_notselected(this);
		cwa_viewstate = LiveReservations.CWA_VIEWSTATE_ADVANCE;
		UpdateIntent_String(LiveReservations.EXTRA_CurrentWaitingAdvance,
				cwa_viewstate);
		dummytext.setText("No Advance Bookings for today");
		fillDataAdvance(CurrentTimeDateAsDate(CurrentViewingTimeDate)
				.getTimeInMillis());

	}

	private void fillDataFinal(long res_date_ts) {
		// TODO Auto-generated method stub
		// Get all of the rows from the database and create the item list
		mResCursor = mdbhelper.fetchDetailsFinal(res_date_ts);

		NumFinal = mResCursor.getCount();
		final_ll.SDR_TV_setSubText(Integer.toString(NumFinal));
		if (NumFinal == 0) {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		} else {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}

		startManagingCursor(mResCursor);

		// Create an array to specify the fields we want to display in the list
		//
		String[] from = new String[] { TRSDBAdapter.TBC_NumTables,
				TRSDBAdapter.TBC_NumGuests, TRSDBAdapter.TBC_Time,
				TRSDBAdapter.TBC_GuestName, TRSDBAdapter.TBC_GuestContactNo };

		// and an array of the fields we want to bind those fields to
		int[] to = new int[] { R.id.lr_curr_lv_NumTables,
				R.id.lr_curr_lv_NumGuests, R.id.lr_curr_lv_StartTime,
				R.id.lr_curr_lv_GuestName, R.id.lr_curr_lv_ContactNo };

		// Now create a simple cursor adapter and set it to display
		LR_Current_CustomCursorAdapter2 curreslv = new LR_Current_CustomCursorAdapter2(
				this, R.layout.lr_currres_listviews_rows2, mResCursor, from, to,LiveReservations.ListViewAsActive,LiveReservations.CurrentClosed);
		sdr_lv.setAdapter(curreslv);

		// Register the ListView for Context menu
		registerForContextMenu(sdr_lv);
	}

	private void fillDataWait(long res_date_ts) {
		// TODO Auto-generated method stub
		// Get all of the rows from the database and create the item list
		mResCursor = mdbhelper.fetchDetailsWaiting(res_date_ts);
		NumWaiting = mResCursor.getCount();
		wait_ll.SDR_TV_setSubText(Integer.toString(NumWaiting));
		if (NumWaiting == 0) {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		} else {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		startManagingCursor(mResCursor);
		// Create an array to specify the fields we want to display in the list
		String[] from = new String[] { TRSDBAdapter.TBW_NumGuests,
				TRSDBAdapter.TBW_ForTime, TRSDBAdapter.TBW_Time,
				TRSDBAdapter.TBW_EndTime, TRSDBAdapter.TBW_GuestName,
				TRSDBAdapter.TBW_GuestContactNo };
		// and an array of the fields we want to bind those fields to
		int[] to = new int[] { R.id.lr_wait_lv_NumGuests,
				R.id.lr_wait_lv_ResFor, R.id.lr_wait_lv_StartTime,
				R.id.lr_wait_lv_TimeElapsed, R.id.lr_wait_lv_GuestName,
				R.id.lr_wait_lv_ContactNo };

		// Now create a simple cursor adapter and set it to display
		LR_Waiting_CustomCursorAdapter waitreslv = new LR_Waiting_CustomCursorAdapter(
				this, R.layout.lr_waiting_listviews_rows, mResCursor, from, to);
		sdr_lv.setAdapter(waitreslv);

		// Register the ListView for Context menu
		registerForContextMenu(sdr_lv);
	}

	private void fillDataAdvance(long res_date_ts) {
		// Get all of the rows from the database and create the item list
		// shows all the advance booksing that wered one for this day
		mResCursor = mdbhelper.fetchDetailsAdvance(res_date_ts);
		NumAdvance = mResCursor.getCount();
		if (NumAdvance == 0) {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		} else {
			dummytext.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}
		advance_ll.SDR_TV_setSubText(Integer.toString(NumAdvance));
		startManagingCursor(mResCursor);

		// Create an array to specify the fields we want to display in the
		// list....NOT SURE IF THIS USED...
		String[] from = new String[] { TRSDBAdapter.TBA_NumTables,
				TRSDBAdapter.TBA_NumGuests, TRSDBAdapter.TBA_ForTime,
				TRSDBAdapter.TBA_GuestName, TRSDBAdapter.TBA_GuestContactNo };
		// and an array of the fields we want to bind those fields to
		int[] to = new int[] { R.id.lr_adv_lv_NumTables,
				R.id.lr_adv_lv_NumGuests, R.id.lr_adv_lv_ResFor,
				R.id.lr_adv_lv_GuestName, R.id.lr_adv_lv_ContactNo };

		// Now create a simple cursor adapter and set it to display
		LR_Advance_CustomCursorAdapter advreslv = new LR_Advance_CustomCursorAdapter(
				this, R.layout.lr_advance_listviews_rows, mResCursor, from, to);
		sdr_lv.setAdapter(advreslv);
		

		// Register the ListView for Context menu
		registerForContextMenu(sdr_lv);
	}

	public String[] setSubTitles(String m_first) {
		String[] m_subt = { m_first, "" };
		return m_subt;
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

	public void CustomNavListRet(int NavListItemCode) {
		if (NavListItemCode == LiveReservations.CustNavListItem_SAMEDATE) {

		} else if (NavListItemCode == LiveReservations.CustNavListItem_TODAYVIEWDATE) {
			// finish this activity
			Customfinish();
		} else if (NavListItemCode == LiveReservations.CustNavListItem_CHANGEDATE) {
			// launch the date dialog...get date...see if the date entered is
			// todays or different..
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			CustomDateDialogFrag m_datedialog = CustomDateDialogFrag
					.newInstance(CurrentViewingTimeDate);
			// CustomDateDialogFrag m_datedialog = new CustomDateDialogFrag();
			m_datedialog.show(ft, null);
		}
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
		getSupportMenuInflater().inflate(R.menu.specific_date_reservations,
				menu);
		return true;
	}

	public String MySimpleDateFormat(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
		return fmt.format(m_timedate.getTime());
	}

	@Override
	public void CustomDateDialogReturn(int year, int month, int day) {
		// TODO Auto-generated method stub
		NewViewingTimeDate.set(year, month, day);
		ToLaunch_SpecificDateReservations();
	}

	protected void ToLaunch_SpecificDateReservations() {
		// SpecificViewingTimeDate.get(field)
		// compare individual fields for both
		Calendar cvtd = CurrentViewingTimeDate;
		Calendar nvtd = NewViewingTimeDate;
		Calendar tvtd = TodayViewingTimeDate;

		if (nvtd.get(Calendar.DATE) == cvtd.get(Calendar.DATE)
				&& nvtd.get(Calendar.MONTH) == cvtd.get(Calendar.MONTH)
				&& nvtd.get(Calendar.YEAR) == cvtd.get(Calendar.YEAR)) {
			// DisplayToast("Viewing ".concat(MySimpleDateFormat(cvtd)));
		} else if (nvtd.get(Calendar.DATE) == tvtd.get(Calendar.DATE)
				&& nvtd.get(Calendar.MONTH) == tvtd.get(Calendar.MONTH)
				&& nvtd.get(Calendar.YEAR) == tvtd.get(Calendar.YEAR)) {
			Customfinish();
		} else {
			// prepapre intent ....
			// LaunchNewActivity....
			LaunchActivity_SpecificDateReservations();
		}
	}

	protected void DisplayToast(CharSequence text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public void Customfinish() {
		Intent intent = new Intent(this, LiveReservations.class);
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
	}

	public void LaunchActivity_SpecificDateReservations() {
		Intent intent = new Intent(this, SpecificDateReservations.class);
		// login has already been checked
		intent.putExtra(MainActivity.EXTRA_loginid, loginid);
		intent.putExtra(MainActivity.EXTRA_IsAuthenticated, true);
		intent.putExtra(LiveReservations.EXTRA_TodayViewingTimeDate,
				TodayViewingTimeDate);
		intent.putExtra(LiveReservations.EXTRA_SpecificViewingTimeDate,
				NewViewingTimeDate);
		intent.putExtra(LiveReservations.EXTRA_UserPrefFileName,
				UserPrefFileName);
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	public void UpdateIntent_String(String extra_str, String newstr) {
		Intent newIntent = getIntent();
		newIntent.putExtra(extra_str, newstr);
		setIntent(newIntent);
	}

	public Calendar CurrentTimeDateAsDate(Calendar res_date) {
		res_date.set(Calendar.HOUR_OF_DAY, 0);
		res_date.set(Calendar.MINUTE, 0);
		res_date.set(Calendar.SECOND, 0);
		res_date.set(Calendar.MILLISECOND, 0);
		return res_date;
	}

}
