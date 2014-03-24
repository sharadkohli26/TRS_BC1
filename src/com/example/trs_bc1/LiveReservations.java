package com.example.trs_bc1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.trs_bc1.CurrentReservationDialog.CurrentReservationDialogListener;
import com.example.trs_bc1.CustomDateDialogFrag.CustomDateDialogListenerReturns;
import com.example.trs_bc1.CustomNavigationListListener.CustomNavigationListReturns;
import com.example.trs_bc1.EditNumDialog.EditNumDialogListener;

public class LiveReservations extends SherlockFragmentActivity implements
		CustomNavigationListReturns, CustomDateDialogListenerReturns,
		EditNumDialogListener, CurrentReservationDialogListener {

	public static final String EXTRA_TodayViewingTimeDate = "com.example.trs1.todayviewingtimedate";
	public static final String EXTRA_SpecificViewingTimeDate = "com.example.trs1.specificviewingtimedate";
	public static final String EXTRA_UserPrefFileName = "com.example.trs1.userpreffilename";
	public static final String EXTRA_CurrentWaitingAdvance = "com.example.trs1.view_currwaitadv";

	public static final String EXTRA_MBRMode = "com.example.trs1.view_mbrmode";
	public static final String Extra_MBRDate = "com.example.trs1.view_mbrdate";
	public static final String Extra_MBRTime = "com.example.trs1.view_mbrtime";
	public static final String Extra_MBR_StatusReturn = "com.example.trs1.mbrstatusreturn";

	public static final String Extra_MaxNumTables = "com.example.trs1.view_maxnumtables";
	public static final String Extra_DefOccupancyTime = "com.example.trs1.view_defoccupancytime";
	public static final String Extra_DefWaitingTime = "com.example.trs1.view_defwaitingtime";
	public static final String Extra_NumWaiting = "com.example.trs1.view_numwaiting";
	public static final String Extra_NumAdvance = "com.example.trs1.view_advance";

	public static final String UserPrefFileHeader = "userpreffile";
	public static final String Pref_NumTables = "NumTables";
	public static final String Pref_DefOccupancyTime = "DefaultOccupancyTime";
	public static final String Pref_DefWaitingTime = "DefaultWaitingTime";
	public static final String Pref_ActiveViewAs = "ActiveViewAs";

	public static final String CustNavListItemStr_TODAY = "Today";
	public static final String CustNavListItemStr_CUSTOM = "Custom";
	public static final int CustNavListItem_SAMEDATE = 101;
	public static final int CustNavListItem_CHANGEDATE = 102;
	public static final int CustNavListItem_TODAYVIEWDATE = 103;

	public static final int MainActivity_LaunchCode = 2603;

	public static final int MBR_LaunchCode = 400;
	public static final int MBR_Mode_Waiting = 401;
	public static final int MBR_Mode_Advance = 402;

	public static final int MBR_StatusRet_NewWaiting = 600;
	public static final int MBR_StatusRet_NewAdvance = 601;
	public static final int MBR_StatusRet_NoAction = 602;

	private static final int TRSDB_CurrentTable = 1;
	private static final int TRSDB_WaitingTable = 2;
	private static final int TRSDB_AdvanceTable = 3;

	public static final int ListViewAsActive = 1234;
	public static final int GridViewAsActive = 4321;

	// context menu for listview lr_current
	public static final int LR_LVCM_Current_Group = 100;
	public static final int LR_LVCM_Current_Call = LR_LVCM_Current_Group + 1;
	public static final int LR_LVCM_Current_Close = LR_LVCM_Current_Group + 2;
	public static final int LR_LVCM_Current_Cancel = LR_LVCM_Current_Group + 3;
	public static final int LR_LVCM_Current_ClosedGroup = LR_LVCM_Current_Group + 4;

	private static final int LR_LVCM_Waiting_Group = LR_LVCM_Current_Group + 5;
	private static final int LR_LVCM_Waiting_ConvCurrent = LR_LVCM_Current_Group + 6;
	private static final int LR_LVCM_Waiting_Call = LR_LVCM_Current_Group + 7;
	private static final int LR_LVCM_Waiting_Close = LR_LVCM_Current_Group + 8;
	private static final int LR_LVCM_Waiting_Cancel = LR_LVCM_Current_Group + 9;
	private static final int LR_LVCM_Waiting_CloseGroup = LR_LVCM_Current_Group + 10;

	private static final int LR_LVCM_Advance_Group = LR_LVCM_Current_Group + 11;
	private static final int LR_LVCM_Advance_ConvCurrent = LR_LVCM_Current_Group + 12;
	private static final int LR_LVCM_Advance_Call = LR_LVCM_Current_Group + 13;
	private static final int LR_LVCM_Advance_Cancel = LR_LVCM_Current_Group + 14;
	private static final int LR_LVCM_Advance_Delete = LR_LVCM_Current_Group + 15;
	private static final int LR_LVCM_Advance_CloseGroup = LR_LVCM_Current_Group + 16;

	public static final String CWA_VIEWSTATE_CURRENT = "CWA_VIEWSTATE_CURRENT";
	public static final String CWA_VIEWSTATE_WAIT = "CWA_VIEWSTATE_WAIT";
	public static final String CWA_VIEWSTATE_ADVANCE = "CWA_VIEWSTATE_ADVANCE";
	
	//entries live or closed
	public static final int CurrentLive=-123;
	public static final int CurrentClosed=CurrentLive+10;
	public static final int WaitingCancelled = -145;
	public static final int WaitingAlive=-99;
	public static final int WaitingUnknown=0;
	public static final int AdvanceAlive = -89;
	public static final int AdvanceNotAliveNotConvCurrent = -50;
	public static final int AdvanceNotAliveConvCurrent = -10;
	public static final int AdvanceAliveIsWaiting = -30;
	public static final int AdvanceNotAliveNoWaiting = -35;
	
	
	

	private TRSDBAdapter mdbhelper;
	private Cursor mResCursor;

	LR_TV current_ll;
	LR_TV wait_ll;
	LR_TV advance_ll;
	String cwa_viewstate;
	int ActiveViewAs;

	TextView rlbot_tv_listgridview;

	TextView dummytext;
	ListView lrc_lv;
	ListView lrc_lv_closed;
	TextView rl_ll_showclosed;
	TextView rl_bottom_showclosed;

	GridView lrc_gv;

	SharedPreferences prefname;
	SharedPreferences.Editor editor;

	SharedPreferences userpreference;
	SharedPreferences.Editor usereditor;
	String UserPrefFileName;
	String loginid;

	int NumTables;// this is maximum number of tables
	int DefOccupancyTime;
	int DefWaitingTime;
	int CurrentReservations;
	int NumWaiting;
	int NumAdvance;
	Calendar TodayViewingTimeDate;
	Calendar SpecificViewingTimeDate;

	CustomNavigationListAdapter m_CustomNavigationListAdapter;
	CustomNavigationListListener m_CustomNavigationListListener;
	String[] NavList_Titles;
	String[] NavList_SubTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_reservations);
		prefname = getSharedPreferences(MainActivity.PreferenceName, 0);
		editor = prefname.edit();
		// login check n set at this activity...this check will be execute
		// everytime this activity gets created...if not login then call the
		// MainActivity by start activity for result...

		if (!CheckSetLoginIsAuth()) {
			// login is not set...
			Logout();
		} else {
			InitAllMethods();
		}
	}

	private void InitAllMethods() {
		// TODO Auto-generated method stub
		initClassVariables();
		initSupportActionBar();
		initSupportViews();
	}

	protected void initClassVariables() {
		UserPrefFileName = UserPrefFileHeader.concat(loginid);
		userpreference = getSharedPreferences(UserPrefFileName, 0);
		usereditor = userpreference.edit();
		// you will have to get these online...variables specific from the
		// server ..so load them from server....and then save them ...
		NumTables = userpreference.getInt(Pref_NumTables, 10);
		DefOccupancyTime = userpreference.getInt(Pref_DefOccupancyTime, 60);
		DefWaitingTime = userpreference.getInt(Pref_DefWaitingTime, 30);
		CurrentReservations = 0;
		NumWaiting = 0;
		NumAdvance = 0;

		// this variable can be in the users shareed pref....
		ActiveViewAs = userpreference.getInt(Pref_ActiveViewAs,
				ListViewAsActive);

		TodayViewingTimeDate = Calendar.getInstance(TimeZone.getDefault());
		SpecificViewingTimeDate = Calendar.getInstance(TimeZone.getDefault());
		// TodayViewingTimeDate=new Time(TimeZone.getDefault().toString());
		// TodayViewingTimeDate.setToNow();

		mdbhelper = new TRSDBAdapter(this);
		mdbhelper.open();

		// aftr all are set then call

	}

	public void initSupportActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		NavList_Titles = getResources().getStringArray(R.array.LR_Title);
		// NavList_SubTitles=getResources().getStringArray(R.array.LR_SubTitle);
		String TodayCurrDate = MySimpleDateFormat(TodayViewingTimeDate);
		NavList_SubTitles = setSubTitles(TodayCurrDate);

		Context context = getSupportActionBar().getThemedContext();
		m_CustomNavigationListAdapter = new CustomNavigationListAdapter(
				context, NavList_Titles, NavList_SubTitles, NavList_Titles[0]);
		m_CustomNavigationListListener = new CustomNavigationListListener(this,
				NavList_Titles);
		getSupportActionBar().setListNavigationCallbacks(
				m_CustomNavigationListAdapter, m_CustomNavigationListListener);

	}

	public void initSupportViews() {
		current_ll = new LR_TV(this, R.id.lr_lo_curr, R.id.lr_tv_current,
				R.id.lr_tv_curr_update);
		wait_ll = new LR_TV(this, R.id.lr_lo_wait, R.id.lr_tv_wait,
				R.id.lr_tv_wait_update);
		advance_ll = new LR_TV(this, R.id.lr_lo_advance, R.id.lr_tv_advance,
				R.id.lr_tv_advance_update);
		current_ll.LR_TV_setSubText(Integer.toString(CurrentReservations)
				+ " | Duration:" + Integer.toString(DefOccupancyTime));

		wait_ll.LR_TV_setSubText(Integer.toString(NumWaiting) + " | WaitTime:"
				+ Integer.toString(DefWaitingTime));
		advance_ll.LR_TV_setSubText(Integer.toString(NumAdvance));

		dummytext = (TextView) findViewById(R.id.lr_tv_dummytext);

		rlbot_tv_listgridview = (TextView) findViewById(R.id.rl_tv_listgridview);

		lrc_lv = (ListView) findViewById(R.id.lrc_listview);
		lrc_lv.setEmptyView(dummytext);

		// grid view variables....
		lrc_gv = (GridView) findViewById(R.id.lrc_gridview);
		lrc_gv.setEmptyView(dummytext);

		rl_ll_showclosed = (TextView) findViewById(R.id.rl_ll_showclosed);
		rl_bottom_showclosed = (TextView) findViewById(R.id.lr_closedres_outsidell);
		lrc_lv_closed = (ListView) findViewById(R.id.lrc_lv_closed);

		SetActiveView();
		// Curr_onClickListener();
		// Wait_onClickListener();
		// Advance_onClickListener();

	}

	public void SetActiveView() {
		Bundle extras = getIntent().getExtras();// returns the intent that
		// started this activity

		SetUp_ListGridView();
		// correct view type chosen
		// populating this correct view type happens in methods called by
		// LO_Current and respective

		if (extras.containsKey(EXTRA_CurrentWaitingAdvance)) {
			cwa_viewstate = extras.getString(EXTRA_CurrentWaitingAdvance);
			if (cwa_viewstate.equals(CWA_VIEWSTATE_CURRENT)) {
				LO_Current(this
						.findViewById(R.layout.activity_live_reservations));
			} else if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
				LO_Wait(this.findViewById(R.layout.activity_live_reservations));
			} else if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
				LO_Advance(this
						.findViewById(R.layout.activity_live_reservations));
			}
		} else {
			LO_Current(this.findViewById(R.layout.activity_live_reservations));
			UpdateNumWaitingSQLite();
			UpdateNumAdvanceSQLite();
		}
	}

	public void LO_Current(View view) {
		/**
		 * sets the view to current...unsets the rest...right now just gets the
		 * data from sqllite table and updates the list view by updateing the
		 * adapter.. TO DO: rather than simply updating check the lat time you
		 * updated...see the user might keep clicking this fataphat.....keep a
		 * time out .and if click after that time then update...LiveReservations
		 */
		current_ll.LR_TV_selected(LiveReservations.this);
		wait_ll.LR_TV_notselected(LiveReservations.this);
		advance_ll.LR_TV_notselected(LiveReservations.this);
		dummytext.setText("Currently No Reservation");

		cwa_viewstate = CWA_VIEWSTATE_CURRENT;
		UpdateIntent_String(EXTRA_CurrentWaitingAdvance, cwa_viewstate);

		/*
		 * set the close reservations text view
		 * ShowCurrClosed(TodayDate().getTimeInMillis()); set the active
		 * reservations... should happen in a seprate thread.. TODO: implement
		 * the below command call ina separate thread...
		 */
		fillDataCurrActive(TodayDate().getTimeInMillis());
	}

	public void LO_Wait(View view) {
		wait_ll.LR_TV_selected(this);
		current_ll.LR_TV_notselected(this);
		advance_ll.LR_TV_notselected(this);
		cwa_viewstate = CWA_VIEWSTATE_WAIT;
		UpdateIntent_String(EXTRA_CurrentWaitingAdvance, cwa_viewstate);
		dummytext.setText("Currently No Waiting");
		fillDataWaitAlive(TodayDate().getTimeInMillis());
	}

	public void LO_Advance(View view) {
		advance_ll.LR_TV_selected(this);
		current_ll.LR_TV_notselected(this);
		wait_ll.LR_TV_notselected(this);
		cwa_viewstate = CWA_VIEWSTATE_ADVANCE;
		UpdateIntent_String(EXTRA_CurrentWaitingAdvance, cwa_viewstate);
		dummytext.setText("No Advance Bookings");
		fillDataAdvanceAlive(TodayDate().getTimeInMillis());
	}

	public void ShowClosedReservations_LV(View view) {
		fillDataCurrClosed(TodayDate().getTimeInMillis());
	}

	public void ShowClosedReservations_TV(View view) {
		ShowCurrClosed(TodayDate().getTimeInMillis());
	}

	public void AddGuestsView(View view) {
		AddGuests();
	}

	public void LR_AddWait(View view) {
		LaunchMBR(MBR_Mode_Waiting);
	}

	public void LR_AddAdvance(View view) {
		LaunchMBR(MBR_Mode_Advance);
	}

	public void ShowGridView(View view) {
		//DisplayToast("Currently Not Available!");
		// check what view it is....and then toggle it...
		if (ActiveViewAs == ListViewAsActive) {
			ActiveViewAs = GridViewAsActive;
			DisplayToast("Switching to Grid View");
		} else if (ActiveViewAs == GridViewAsActive) {
			ActiveViewAs = ListViewAsActive;
			DisplayToast("Switching to List View");
		}
		// now view has been switched so call the function which will change the
		// display text and image
		//SetUp_ListGridView();
		SetActiveView();
	}

	public void LaunchMBR(int MBRMode) {
		Intent mbrintent = new Intent(this, MasterBookReservations4.class);

		mbrintent.putExtra(MainActivity.EXTRA_loginid, loginid);
		mbrintent.putExtra(MainActivity.EXTRA_IsAuthenticated, true);
		mbrintent.putExtra(EXTRA_UserPrefFileName, UserPrefFileName);

		mbrintent.putExtra(EXTRA_MBRMode, MBRMode);
		mbrintent.putExtra(Extra_MBRDate, TodayDate());
		// the second arguement is just the date..time component set to 0...
		mbrintent.putExtra(Extra_DefOccupancyTime, DefOccupancyTime);
		mbrintent.putExtra(Extra_DefWaitingTime, DefWaitingTime);
		mbrintent.putExtra(Extra_NumWaiting, NumWaiting);
		mbrintent.putExtra(Extra_NumAdvance, NumAdvance);
		mbrintent.putExtra(Extra_MaxNumTables, NumTables);
		startActivityForResult(mbrintent, MBR_LaunchCode);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MBR_LaunchCode) {
			if (resultCode == RESULT_OK) {
				int mbr_statusret_waitadv = data.getIntExtra(
						Extra_MBR_StatusReturn, -1);
				if (mbr_statusret_waitadv == MBR_StatusRet_NewWaiting) {
					// new entry added to waiting..now update the numwaiting
					// field...if current view is waiting then update view..else
					if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
						fillDataWaitAlive(TodayDate().getTimeInMillis());
					} else {
						UpdateNumWaitingSQLite();// just update the header
					}
					// nothing
				} else if (mbr_statusret_waitadv == MBR_StatusRet_NewAdvance) {
					if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
						fillDataAdvanceAlive(TodayDate().getTimeInMillis());
					} else {
						UpdateNumAdvanceSQLite();// just update the header
					}
					// nothing
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		} else if (requestCode == MainActivity_LaunchCode) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras.containsKey(MainActivity.EXTRA_loginid)) {
					loginid = extras.getString(MainActivity.EXTRA_loginid);
					UpdateIntent_String(MainActivity.EXTRA_loginid, loginid);
					DisplayToast(loginid + " Logged in");
					InitAllMethods();
				} else {
					finish();
				}
			} else if (resultCode == RESULT_CANCELED) {
				finish();
			}
		}
	}

	private void UpdateNumAdvanceSQLite() {
		mResCursor = mdbhelper.fetchDetailsAdvanceAlive(TodayDate()
				.getTimeInMillis());
		NumAdvance = mResCursor.getCount();
		advance_ll.LR_TV_setSubText(Integer.toString(NumAdvance));
	}

	public void UpdateNumWaitingSQLite() {
		mResCursor = mdbhelper.fetchDetailsWaitingAlive(TodayDate()
				.getTimeInMillis(), TRSDBAdapter.WaitingAlive);
		NumWaiting = mResCursor.getCount();
		wait_ll.LR_TV_setSubText(Integer.toString(NumWaiting) + " | WaitTime:"
				+ Integer.toString(DefWaitingTime));
	}

	public void UpdateIntent_String(String extra_str, String newstr) {
		Intent newIntent = getIntent();
		newIntent.putExtra(extra_str, newstr);
		setIntent(newIntent);
	}

	private void SetUp_ListGridView() {
		// first toggle the button displayed and text

		if (ActiveViewAs == ListViewAsActive) {
			rlbot_tv_listgridview.setText("Gird View");
			// update image also later...
			lrc_lv.setVisibility(View.VISIBLE);
			lrc_gv.setVisibility(View.GONE);
		} else if (ActiveViewAs == GridViewAsActive) {
			rlbot_tv_listgridview.setText("List View");
			// update image also later...
			lrc_lv.setVisibility(View.GONE);
			lrc_gv.setVisibility(View.VISIBLE);
		}
		
	}

	/*
	 * public void Curr_onClickListener(){
	 * current_ll.container.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * current_ll.LR_TV_selected(LiveReservations.this);
	 * wait_ll.LR_TV_notselected(LiveReservations.this);
	 * advance_ll.LR_TV_notselected(LiveReservations.this); } });
	 * 
	 * }
	 */

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
		if (prefname.contains(MainActivity.Pref_loginid)) {
			loginid = prefname.getString(MainActivity.Pref_loginid, "-1");
			if (loginid.equals("-1"))
				return false;
			else {
				UpdateIntent_String(MainActivity.EXTRA_loginid, loginid);
				return true;
			}
		} else {
			return false;
		}
		/*
		 * if(prefname.getString(MainActivity.Pref_loginid, "-1")) Bundle extras
		 * = getIntent().getExtras();// returns the intent that // started this
		 * activity if (extras == null) { return false; } if
		 * (extras.containsKey(MainActivity.EXTRA_loginid)) { String mloginid =
		 * extras.getString(MainActivity.EXTRA_loginid); if (mloginid.isEmpty()
		 * || mloginid.equalsIgnoreCase("-1")) { return false; } loginid =
		 * mloginid; // DisplayToast(loginid); return true; } return false;
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.live_reservations, menu);
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (cwa_viewstate.equals(CWA_VIEWSTATE_CURRENT)) {
			switch (v.getId()) {
			case R.id.lrc_listview: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				// not fully sure how this works....
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno;
				if(ActiveViewAs==GridViewAsActive){
					tv_guestcontactno= (TextView) itemID
							.findViewById(R.id.lr_curr_gv_ContactNo);					
				}
				else{
					tv_guestcontactno= (TextView) itemID
							.findViewById(R.id.lr_curr_lv_ContactNo);					
				}
				
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();

				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Current_Group, LR_LVCM_Current_Call, 0,
							"Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Current_Group, LR_LVCM_Current_Close, 1,
						"Close");
				menu.add(LR_LVCM_Current_Group, LR_LVCM_Current_Cancel, 2,
						"Delete");
				break;
			}
			case R.id.lrc_lv_closed: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno = (TextView) itemID
						.findViewById(R.id.lr_curr_lv_ContactNo);
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();
				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Current_ClosedGroup, LR_LVCM_Current_Call,
							0, "Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Current_ClosedGroup, LR_LVCM_Current_Cancel,
						1, "Delete");
				break;
			}
			}

		} else if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
			switch (v.getId()) {
			case R.id.lrc_listview: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno = (TextView) itemID
						.findViewById(R.id.lr_wait_lv_ContactNo);
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();
				menu.add(LR_LVCM_Waiting_Group, LR_LVCM_Waiting_ConvCurrent, 0,
						"Move To Current");
				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Waiting_Group, LR_LVCM_Waiting_Call, 1,
							"Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Waiting_Group, LR_LVCM_Waiting_Close, 2,
						"Close");
				menu.add(LR_LVCM_Waiting_Group, LR_LVCM_Waiting_Cancel, 3,
						"Delete");
				break;
			}
			case R.id.lrc_lv_closed: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno = (TextView) itemID
						.findViewById(R.id.lr_wait_lv_ContactNo);
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();

				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Waiting_CloseGroup, LR_LVCM_Waiting_Call,
							1, "Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Waiting_CloseGroup, LR_LVCM_Waiting_Cancel, 2,
						"Delete");
				break;
			}
			}
		}
		else if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
			switch (v.getId()) {
			case R.id.lrc_listview: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno = (TextView) itemID
						.findViewById(R.id.lr_adv_lv_ContactNo);
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();
				menu.add(LR_LVCM_Advance_Group, LR_LVCM_Advance_ConvCurrent, 0,
						"Move To Current");
				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Advance_Group, LR_LVCM_Advance_Call, 1,
							"Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Advance_Group, LR_LVCM_Advance_Cancel, 3,
						"Cancel");
				menu.add(LR_LVCM_Advance_Group, LR_LVCM_Advance_Delete, 4,
						"Delete");
				break;
			}
			case R.id.lrc_lv_closed: {
				menu.setHeaderTitle("Select");
				// get the row that was clicked
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
				View itemID = (info.targetView);
				// now access the contact no text viw from this
				TextView tv_guestcontactno = (TextView) itemID
						.findViewById(R.id.lr_adv_lv_ContactNo);
				// check whether there is a number associated
				String guestcontactno = tv_guestcontactno.getText().toString();

				if (guestcontactno != null && !guestcontactno.isEmpty()) {
					menu.add(LR_LVCM_Advance_CloseGroup, LR_LVCM_Advance_Call,
							1, "Call:" + guestcontactno);
				}
				menu.add(LR_LVCM_Advance_CloseGroup, LR_LVCM_Advance_Delete, 2,
						"Delete");
				break;
			}
			}
		}

	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		// http://stackoverflow.com/questions/8606523/oncontextitemselected-does-not-seem-to-be-called
		// Jens solution

		// Get extra info about list item that was long-pressed
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		// DisplayToast("Gadbad ho gaye re");
		// first check item group id
		if (item.getGroupId() == LR_LVCM_Current_Group) {
			// Perform action according to selected item from context menu
			switch (item.getItemId()) {
			case LR_LVCM_Current_Call:
				// open the dialer
				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_curr_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			case LR_LVCM_Current_Close:
				// end the dinner update the database...this is to be updated to
				// be done in asynchronous thread
				if (mdbhelper.CloseCurrent(menuInfo.id, Calendar.getInstance()
						.getTimeInMillis())) {
					DisplayToast("Reservation Closed");
					fillDataCurrActive(TodayDate().getTimeInMillis());
				} else {
					DisplayToast("Unable to close!");
				}

				break;
			case LR_LVCM_Current_Cancel:
				// delete the dinner.. update the database...this is to be
				// updated to be done in asynchronous thread
				if (mdbhelper.DeleteCurrent(menuInfo.id)) {
					fillDataCurrActive(TodayDate().getTimeInMillis());
					DisplayToast("Current Reservation Canceled");
				} else {
					DisplayToast("Cancellation Failed");
				}
				break;
			}

		} else if (item.getGroupId() == LR_LVCM_Current_ClosedGroup) {
			switch (item.getItemId()) {
			case LR_LVCM_Current_Call:
				// open the dialer
				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_curr_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			case LR_LVCM_Current_Cancel:
				// delete the dinner.. update the database...this is to be
				// updated to be done in asynchronous thread
				if (mdbhelper.DeleteCurrent(menuInfo.id)) {
					fillDataCurrClosed(TodayDate().getTimeInMillis());
					DisplayToast("Reservation Deleted");
				} else {
					DisplayToast("Deletion Failed");
				}
				break;
			}
		} else if (item.getGroupId() == LR_LVCM_Waiting_Group) {
			switch (item.getItemId()) {
			case LR_LVCM_Waiting_Call: {

				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_wait_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			}
			case LR_LVCM_Waiting_Close: {
				if (mdbhelper.CancelWaiting(menuInfo.id, Calendar.getInstance()
						.getTimeInMillis())) {
					DisplayToast("Waiting Removed");
					fillDataWaitAlive(TodayDate().getTimeInMillis());

				} else {
					DisplayToast("Unable to Remove!");
				}
				break;
			}
			case LR_LVCM_Waiting_ConvCurrent: {
				// get all the variables fromt this view
				View v = (menuInfo.targetView);
				TextView tv_numtables = (TextView) v
						.findViewById(R.id.lr_wait_lv_NumTables);
				TextView tv_numguests = (TextView) v
						.findViewById(R.id.lr_wait_lv_NumGuests);
				TextView tv_guestname = (TextView) v
						.findViewById(R.id.lr_wait_lv_GuestName);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_wait_lv_ContactNo);
				AddGuestsWithInfo(((Integer) tv_numtables.getTag()).intValue(),
						((Integer) tv_numguests.getTag()).intValue(),
						tv_guestname.getText().toString(), tv_guestcontactno
								.getText().toString(), menuInfo.id, -1);
				break;
			}
			case LR_LVCM_Waiting_Cancel: {
				if (mdbhelper.DeleteWaiting(menuInfo.id)) {
					fillDataWaitAlive(TodayDate().getTimeInMillis());
					DisplayToast("Waiting Deleted");
				} else {
					DisplayToast("Deletion Failed");
				}
				break;
			}
			}
		} else if (item.getGroupId() == LR_LVCM_Waiting_CloseGroup) {
			switch (item.getItemId()) {
			case LR_LVCM_Waiting_Call: {
				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_wait_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			}
			case LR_LVCM_Waiting_Cancel: {
				if (mdbhelper.DeleteWaiting(menuInfo.id)) {
					fillDataCurrClosed(TodayDate().getTimeInMillis());
					DisplayToast("Waiting Deleted");
				} else {
					DisplayToast("Deletion Failed");
				}
				break;
			}
			}
		} else if (item.getGroupId() == LR_LVCM_Advance_Group) {
			switch (item.getItemId()) {
			case LR_LVCM_Advance_Call: {
				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_adv_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			}
			case LR_LVCM_Advance_Cancel: {
				if (mdbhelper.CancelAdvance(menuInfo.id)) {
					DisplayToast("Booking Cancelled");
					fillDataAdvanceAlive(TodayDate().getTimeInMillis());

				} else {
					DisplayToast("Unable to Cancel!");
				}
				break;
			}
			case LR_LVCM_Advance_ConvCurrent: {
				// get all the variables fromt this view
				// there can be a discreplancy here..say i hava booking for
				// tomorrow andi come today and i move it to current...chill
				// hai...
				View v = (menuInfo.targetView);
				TextView tv_numtables = (TextView) v
						.findViewById(R.id.lr_adv_lv_NumTables);
				TextView tv_numguests = (TextView) v
						.findViewById(R.id.lr_adv_lv_NumGuests);
				TextView tv_guestname = (TextView) v
						.findViewById(R.id.lr_adv_lv_GuestName);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_adv_lv_ContactNo);
				AddGuestsWithInfo(((Integer) tv_numtables.getTag()).intValue(),
						((Integer) tv_numguests.getTag()).intValue(),
						tv_guestname.getText().toString(), tv_guestcontactno
								.getText().toString(), -1, menuInfo.id);
				break;
			}
			case LR_LVCM_Advance_Delete: {
				if (mdbhelper.DeleteAdvance(menuInfo.id)) {
					fillDataAdvanceAlive(TodayDate().getTimeInMillis());
					DisplayToast("Entry Deleted");
				} else {
					DisplayToast("Deletion Failed");
				}
				break;
			}
			}
		} else if (item.getGroupId() == LR_LVCM_Advance_CloseGroup) {
			switch (item.getItemId()) {
			case LR_LVCM_Advance_Call: {
				View v = (menuInfo.targetView);
				TextView tv_guestcontactno = (TextView) v
						.findViewById(R.id.lr_adv_lv_ContactNo);
				OpenDialer(tv_guestcontactno.getText().toString());
				break;
			}
			case LR_LVCM_Advance_Delete: {
				if (mdbhelper.DeleteAdvance(menuInfo.id)) {
					fillDataCurrClosed(TodayDate().getTimeInMillis());
					DisplayToast("Deleted");
				} else {
					DisplayToast("Deletion Failed");
				}
				break;
			}
			}
		} else {
			DisplayToast("Not Recognised");
		}
		return true;
	}

	public void Logout() {
		editor.putBoolean(MainActivity.Pref_IsAuthenticated, false);
		if (prefname.contains(MainActivity.Pref_loginid)) {
			editor.remove(MainActivity.Pref_loginid);
		}
		editor.commit();
		Intent intent = new Intent(this, MainActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// |Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
		// flag_activity_clear_task for api >=11
		startActivityForResult(intent, MainActivity_LaunchCode);
		// finish();
	}

	public void CustomNavListRet(int NavListItemCode) {
		if (NavListItemCode == CustNavListItem_SAMEDATE) {

		} else if (NavListItemCode == CustNavListItem_CHANGEDATE) {
			// launch the date dialog...get date...see if the date entered is
			// todays or different..
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();

			CustomDateDialogFrag m_datedialog = CustomDateDialogFrag
					.newInstance(TodayViewingTimeDate);
			m_datedialog.show(ft, null);
		}
	}

	public void AddGuestsMenu(MenuItem item) {
		AddGuests();
	}

	public void AddGuests() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CurrentReservationDialog crd = CurrentReservationDialog.newInstance1(
				Integer.toString(DefOccupancyTime), NumTables);
		crd.show(ft, "add_live_booking");
	}

	public void AddGuestsWithInfo(int numtables, int numguests, String name,
			String contact, long waituid, long advuid) {
		// (String DefOccupancy,int m_MaxNumTables, int numtables,int numguests,
		// String name,String contact,long waituid) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CurrentReservationDialog crd = CurrentReservationDialog.newInstance2(
				Integer.toString(DefOccupancyTime), NumTables, numtables,
				numguests, name, contact, waituid, advuid);
		crd.show(ft, "add_live_booking");
	}

	public void SetNumTables(MenuItem item) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		EditNumDialog editnumDialog = EditNumDialog.newInstance(
				"Number of Tables", Pref_NumTables, NumTables);
		editnumDialog.show(ft, "edit_num_dialog");
	}

	public void ChangeDefaultOccupancyTime(MenuItem item) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		EditNumDialog editnumDialog = EditNumDialog.newInstance(
				"Default Occupancy Time(m)", Pref_DefOccupancyTime,
				DefOccupancyTime);
		editnumDialog.show(ft, "edit_num_dialog");
	}

	public void ChangeDefaultWaitingTime(MenuItem item) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		EditNumDialog editnumDialog = EditNumDialog.newInstance(
				"Default Waiting Time(m)", Pref_DefWaitingTime, DefWaitingTime);
		editnumDialog.show(ft, "edit_num_dialog");
	}

	public void MenuLogout(MenuItem item) {
		Logout();
	}

	protected void DisplayToast(CharSequence text) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public String MySimpleDateFormat(Calendar m_timedate) {
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
		return fmt.format(m_timedate.getTime());
	}

	@Override
	public void CustomDateDialogReturn(int year, int month, int day) {
		SpecificViewingTimeDate.set(year, month, day);
		// now check if different from TodayViewingDateTime then launch new
		// class else set header do nothing
		ToLaunch_SpecificDateReservations();
	}

	public void ToLaunch_SpecificDateReservations() {
		// SpecificViewingTimeDate.get(field)
		// compare individual fields for both
		Calendar svtd = SpecificViewingTimeDate;
		Calendar tvtd = TodayViewingTimeDate;
		if (svtd.get(Calendar.DATE) == tvtd.get(Calendar.DATE)
				&& svtd.get(Calendar.MONTH) == tvtd.get(Calendar.MONTH)
				&& svtd.get(Calendar.YEAR) == tvtd.get(Calendar.YEAR)) {
			DisplayToast("Viewing ".concat(MySimpleDateFormat(tvtd)));
		} else {
			// prepapre intent ....
			// LaunchNewActivity....
			LaunchActivity_SpecificDateReservations();
		}
	}

	public void LaunchActivity_SpecificDateReservations() {
		Intent intent = new Intent(this, SpecificDateReservations.class);
		// login has already been checked
		intent.putExtra(MainActivity.EXTRA_loginid, loginid);
		intent.putExtra(MainActivity.EXTRA_IsAuthenticated, true);
		intent.putExtra(EXTRA_TodayViewingTimeDate, TodayViewingTimeDate);
		intent.putExtra(EXTRA_SpecificViewingTimeDate, SpecificViewingTimeDate);
		intent.putExtra(EXTRA_UserPrefFileName, UserPrefFileName);
		startActivity(intent);
	}

	@Override
	public void onFinishEditNumDialog(String TargetTitle,
			String TargetPrefName, String inputText) {
		// TODO Auto-generated method stub
		int finalnum = Integer.parseInt(inputText);
		usereditor.putInt(TargetPrefName, finalnum);
		usereditor.commit();
		if (TargetPrefName.equals(Pref_DefOccupancyTime)) {
			DefOccupancyTime = finalnum;
			// update the header...
			current_ll.LR_TV_setSubText(Integer.toString(CurrentReservations)
					+ " | Duration:" + Integer.toString(finalnum));
		} else if (TargetPrefName.equals(Pref_NumTables)) {
			NumTables = finalnum;
		} else if (TargetPrefName.equals(Pref_DefWaitingTime)) {
			DefWaitingTime = finalnum;
			wait_ll.LR_TV_setSubText(Integer.toString(NumWaiting)
					+ " | WaitTime:" + Integer.toString(DefWaitingTime));
		}

		DisplayToast(TargetTitle + " Changed to " + inputText);
	}

	@Override
	public void CurrentReservation(int crdNumTables, int crdNumGuests,
			int crdDuration, String crdGuestNames, String crdContactNos,
			long crdwaituid, long crdadvuid) {
		// TODO Auto-generated method stub
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

		// what happens if it fails...havent handled that case...
		int succorfail = (int) mdbhelper.NewCurrentEntry(request_ts,
				res_date_ts, res_time_ts, crdNumTables, crdNumGuests,
				crdDuration, crdGuestNames, crdContactNos, crdwaituid,
				crdadvuid);
		if (succorfail == -1) {
			DisplayToast("Reservation Failed!!");
		} else {
			DisplayToast("Reservation Added!!");
			if (cwa_viewstate.equals(CWA_VIEWSTATE_CURRENT)) {
				fillDataCurrActive(res_date_ts);
			} else {
				mResCursor = mdbhelper.fetchDetailsCurrentOpen(res_date_ts);

				CurrentReservations = mResCursor.getCount();
				current_ll.LR_TV_setSubText(Integer
						.toString(CurrentReservations)
						+ " | Duration:"
						+ Integer.toString(DefOccupancyTime));
			}
			if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
				fillDataWaitAlive(res_date_ts);
			} else {
				UpdateNumWaitingSQLite();
			}
			if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
				fillDataAdvanceAlive(res_date_ts);
			} else {
				UpdateNumAdvanceSQLite();
			}
		}

		//
		// TodayViewingTimeDate

	}

	public Calendar TodayDate() {
		Calendar res_date = Calendar.getInstance(TimeZone.getDefault());
		res_date.set(Calendar.HOUR_OF_DAY, 0);
		res_date.set(Calendar.MINUTE, 0);
		res_date.set(Calendar.SECOND, 0);
		res_date.set(Calendar.MILLISECOND, 0);
		return res_date;

	}

	private void fillDataCurrActive(long res_date_ts) {
		// shouldnt this all happen inside a separate thread...
		// ideally u save the old view and qury for the new view ....with
		// Updating dialog showing..if successul then update the view else show
		// the old view with alert as UPDATE Failed

		// Get all of the rows from the database and create the item list
		mResCursor = mdbhelper.fetchDetailsCurrentOpen(res_date_ts);

		CurrentReservations = mResCursor.getCount();
		if (CurrentReservations > 0) {
			current_ll.LR_TV_setSubText(Integer.toString(CurrentReservations)
					+ " | Duration:" + Integer.toString(DefOccupancyTime));
		} else {
			current_ll.LR_TV_setSubText("0 | Duration:"
					+ Integer.toString(DefOccupancyTime));
		}

		startManagingCursor(mResCursor);

		// Create an array to specify the fields we want to display in the list
		//
		String[] from = new String[] { TRSDBAdapter.TBC_NumTables,
				TRSDBAdapter.TBC_NumGuests, TRSDBAdapter.TBC_Time,
				TRSDBAdapter.TBC_GuestName, TRSDBAdapter.TBC_GuestContactNo };

		int[] to;
		LR_Current_CustomCursorAdapter2 curreslv;
		// check which view is active
		if (ActiveViewAs == GridViewAsActive) {
			// and an array of the fields we want to bind those fields to
			to = new int[] { R.id.lr_curr_gv_NumTables,
					R.id.lr_curr_gv_NumGuests, R.id.lr_curr_gv_StartTime,
					R.id.lr_curr_gv_GuestName, R.id.lr_curr_gv_ContactNo };
			// Now create a simple cursor adapter and set it to display			
			curreslv = new LR_Current_CustomCursorAdapter2(this,
					R.layout.lr_curres_gridview_cell, mResCursor, from, to,
					ActiveViewAs,CurrentLive);
			lrc_gv.setAdapter(curreslv);	
			registerForContextMenu(lrc_gv);
			
		} else {
			// and an array of the fields we want to bind those fields to
			to = new int[] { R.id.lr_curr_lv_NumTables,
					R.id.lr_curr_lv_NumGuests, R.id.lr_curr_lv_StartTime,
					R.id.lr_curr_lv_GuestName, R.id.lr_curr_lv_ContactNo };
			// Now create a simple cursor adapter and set it to display
			curreslv = new LR_Current_CustomCursorAdapter2(this,
					R.layout.lr_currres_listviews_rows2, mResCursor, from, to,
					ActiveViewAs,CurrentLive);
			lrc_lv.setAdapter(curreslv);
			// Register the ListView for Context menu
			registerForContextMenu(lrc_lv);
		}
		

		
		ShowCurrClosed(res_date_ts);
	}

	private void ShowCurrClosed(long res_date_ts) {
		if (cwa_viewstate.equals(CWA_VIEWSTATE_CURRENT)) {
			mResCursor = mdbhelper.fetchDetailsCurrentClosed(res_date_ts);
		} else if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
			mResCursor = mdbhelper.fetchDetailsWaitingNotAlive(res_date_ts);
		} else if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
			mResCursor = mdbhelper.fetchDetailsAdvanceNotAlive(res_date_ts);
		}

		if (mResCursor != null && mResCursor.getCount() > 0) {
			rl_bottom_showclosed.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		} else {
			rl_bottom_showclosed.getLayoutParams().height = 0;
		}
		
		
		rl_ll_showclosed.setVisibility(View.GONE);
		lrc_lv_closed.setAdapter(null);
		lrc_lv_closed.setVisibility(View.GONE);
	}

	private void fillDataCurrClosed(long res_date_ts) {

		if (cwa_viewstate.equals(CWA_VIEWSTATE_CURRENT)) {
			// Get all of the rows from the database and create the item list
			mResCursor = mdbhelper.fetchDetailsCurrentClosed(res_date_ts);
			if (mResCursor.getCount() > 0) {
				startManagingCursor(mResCursor);
				// set the other views
				rl_ll_showclosed.setVisibility(View.VISIBLE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				lrc_lv_closed.setVisibility(View.VISIBLE);

				// Create an array to specify the fields we want to display in
				// the
				// list

				String[] from = new String[] { TRSDBAdapter.TBC_NumTables,
						TRSDBAdapter.TBC_NumGuests, TRSDBAdapter.TBC_Time,
						TRSDBAdapter.TBC_GuestName,
						TRSDBAdapter.TBC_GuestContactNo };

				// and an array of the fields we want to bind those fields to
				int[] to = new int[] { R.id.lr_curr_lv_NumTables,
						R.id.lr_curr_lv_NumGuests, R.id.lr_curr_lv_StartTime,
						R.id.lr_curr_lv_GuestName, R.id.lr_curr_lv_ContactNo };

				// Now create a simple cursor adapter and set it to display
				LR_Current_CustomCursorAdapter2 curreslv = new LR_Current_CustomCursorAdapter2(
						this, R.layout.lr_currres_listviews_rows2, mResCursor,
						from, to,ListViewAsActive,CurrentClosed);
				lrc_lv_closed.setAdapter(curreslv);
				// Register the ListView for Context menu
				registerForContextMenu(lrc_lv_closed);
			} else {
				rl_ll_showclosed.setVisibility(View.GONE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				// rl_bottom_showclosed.setHeight(0);
				// rl_bottom_showclosed.setWidth(0);
				lrc_lv_closed.setVisibility(View.GONE);
			}
		} else if (cwa_viewstate.equals(CWA_VIEWSTATE_WAIT)) {
			// Get all of the rows from the database and create the item list
			mResCursor = mdbhelper.fetchDetailsWaitingNotAlive(res_date_ts);
			if (mResCursor.getCount() > 0) {
				startManagingCursor(mResCursor);
				// set the other views
				rl_ll_showclosed.setVisibility(View.VISIBLE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				lrc_lv_closed.setVisibility(View.VISIBLE);

				// Create an array to specify the fields we want to display in
				// the
				// list
				// Create an array to specify the fields we want to display in
				// the list
				String[] from = new String[] { TRSDBAdapter.TBW_NumGuests,
						TRSDBAdapter.TBW_ForTime, TRSDBAdapter.TBW_Time,
						TRSDBAdapter.TBW_EndTime, TRSDBAdapter.TBW_GuestName,
						TRSDBAdapter.TBW_GuestContactNo };
				// and an array of the fields we want to bind those fields to
				int[] to = new int[] { R.id.lr_wait_lv_NumGuests,
						R.id.lr_wait_lv_ResFor, R.id.lr_wait_lv_StartTime,
						R.id.lr_wait_lv_TimeElapsed, R.id.lr_wait_lv_GuestName,
						R.id.lr_wait_lv_ContactNo };
				LR_Waiting_CustomCursorAdapter waitreslv = new LR_Waiting_CustomCursorAdapter(
						this, R.layout.lr_waiting_listviews_rows, mResCursor,
						from, to);
				lrc_lv_closed.setAdapter(waitreslv);
				// Register the ListView for Context menu
				registerForContextMenu(lrc_lv_closed);
			} else {
				rl_ll_showclosed.setVisibility(View.GONE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				lrc_lv_closed.setVisibility(View.GONE);
			}
		} else if (cwa_viewstate.equals(CWA_VIEWSTATE_ADVANCE)) {
			mResCursor = mdbhelper.fetchDetailsAdvanceNotAlive(res_date_ts);
			if (mResCursor.getCount() > 0) {
				rl_ll_showclosed.setVisibility(View.VISIBLE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				lrc_lv_closed.setVisibility(View.VISIBLE);
				// Create an array to specify the fields we want to display in
				// the
				// list....NOT SURE IF THIS USED...
				String[] from = new String[] { TRSDBAdapter.TBA_NumTables,
						TRSDBAdapter.TBA_NumGuests, TRSDBAdapter.TBA_ForTime,
						TRSDBAdapter.TBA_GuestName,
						TRSDBAdapter.TBA_GuestContactNo };
				// and an array of the fields we want to bind those fields to
				int[] to = new int[] { R.id.lr_adv_lv_NumTables,
						R.id.lr_adv_lv_NumGuests, R.id.lr_adv_lv_ResFor,
						R.id.lr_adv_lv_GuestName, R.id.lr_adv_lv_ContactNo };

				// Now create a simple cursor adapter and set it to display
				LR_Advance_CustomCursorAdapter advreslv = new LR_Advance_CustomCursorAdapter(
						this, R.layout.lr_advance_listviews_rows, mResCursor,
						from, to);
				lrc_lv_closed.setAdapter(advreslv);

				// Register the ListView for Context menu
				registerForContextMenu(lrc_lv_closed);
			} else {
				rl_ll_showclosed.setVisibility(View.GONE);
				rl_bottom_showclosed.getLayoutParams().height = 0;
				lrc_lv_closed.setVisibility(View.GONE);
			}

		}
	}

	private void fillDataWaitAlive(long res_date_ts) {
		// Get all of the rows from the database and create the item list
		mResCursor = mdbhelper.fetchDetailsWaitingAlive(res_date_ts,
				TRSDBAdapter.WaitingAlive);
		NumWaiting = mResCursor.getCount();
		wait_ll.LR_TV_setSubText(Integer.toString(NumWaiting) + " | WaitTime:"
				+ Integer.toString(DefWaitingTime));

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
		lrc_lv.setAdapter(waitreslv);

		// Register the ListView for Context menu
		registerForContextMenu(lrc_lv);
		ShowCurrClosed(res_date_ts);
	}

	private void fillDataAdvanceAlive(long res_date_ts) {
		// Get all of the rows from the database and create the item list
		// shows all the advance booksing that are alive ahead of this
		// date...i.e >=res_date_ts
		mResCursor = mdbhelper.fetchDetailsAdvanceAlive(res_date_ts);
		NumAdvance = mResCursor.getCount();
		advance_ll.LR_TV_setSubText(Integer.toString(NumAdvance));
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
		lrc_lv.setAdapter(advreslv);

		// Register the ListView for Context menu
		registerForContextMenu(lrc_lv);
		ShowCurrClosed(res_date_ts);
	}

	/*
	 * private void ShowWaitClosedCancel(long res_date_ts) { mResCursor =
	 * mdbhelper.fetchDetailsWaitingNotAlive(res_date_ts); if
	 * (mResCursor.getCount() > 0) {
	 * rl_bottom_showclosed.getLayoutParams().height =
	 * ViewGroup.LayoutParams.WRAP_CONTENT; } else {
	 * rl_bottom_showclosed.getLayoutParams().height = 0; }
	 * rl_ll_showclosed.setVisibility(View.GONE);
	 * lrc_lv_closed.setAdapter(null); lrc_lv_closed.setVisibility(View.GONE); }
	 */

	public void OpenDialer(String ContactNum) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + ContactNum));
		startActivity(intent);
	}

}
