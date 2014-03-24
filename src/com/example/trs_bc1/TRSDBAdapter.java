package com.example.trs_bc1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TRSDBAdapter {

	private static final String DB_CWAName = "trsdata";
	private static final String DBTable_Current = "trscurrent";
	private static final String DBTable_Waiting = "trswaiting";
	private static final String DBTable_Advance = "trsadvance";
	private static final int DATABASE_VERSION = 9;
	
	public static final int WaitingCancelled = -1;
	public static final int WaitingAlive=-99;
	public static final int WaitingUnknown=0;
	
	public static final int AdvanceAlive = -89;
	public static final int AdvanceNotAliveNotConvCurrent = -50;
	public static final int AdvanceNotAliveConvCurrent = -10;
	public static final int AdvanceAliveIsWaiting = -30;
	public static final int AdvanceNotAliveNoWaiting = -35;
	
	
	public static final int CurrentLive=-1;
	
	

	public static final String TBC_Title = "title";
	public static final String TBC_UID = "_id";
	public static final String TBC_Date = "rsdate";
	public static final String TBC_Time = "rstime";
	public static final String TBC_EndTime = "rsendtime";
	public static final String TBC_NumTables = "numtables";
	public static final String TBC_NumGuests = "numguests";
	public static final String TBC_ExpDuration = "expduration";
	public static final String TBC_GuestName = "guestname";
	public static final String TBC_GuestContactNo = "contactno";
	public static final String TBC_WasWaiting = "waswaiting";
	public static final String TBC_WasAdvance = "wasadvance";
	private static final String DB_CurrentCreation = "create table "
			+ DBTable_Current + "(" + TBC_UID + " integer, "
			+ TBC_Date + " integer not null, " + TBC_Time
			+ " integer not null, " + TBC_EndTime + " integer, "
			+ TBC_NumTables + " integer not null, " + TBC_NumGuests
			+ " integer not null, " + TBC_ExpDuration + " integer, "
			+ TBC_GuestName + " text, " + TBC_GuestContactNo + " text, "
			+ TBC_WasWaiting + " integer, " + TBC_WasAdvance + " integer);";

	public static final String TBW_Title = "title";
	public static final String TBW_UID = "_id";
	public static final String TBW_Date = "rsdate";
	public static final String TBW_Time = "rstime";
	public static final String TBW_EndTime = "waitendtime";
	public static final String TBW_ForTime = "rsfortime";
	public static final String TBW_NumTables = "numtables";
	public static final String TBW_NumGuests = "numguests";
	public static final String TBW_ExpWaiting = "expwaiting";
	public static final String TBW_GuestName = "guestname";
	public static final String TBW_GuestContactNo = "contactno";
	public static final String TBW_ConvCurrent = "convertedcurrent";
	public static final String TBW_WasAdvance = "wasadvance";
	private static final String DB_WaitingCreation = "create table "
			+ DBTable_Waiting + "(" + TBW_UID + " integer primary key, "
			+ TBW_Date + " integer not null, " + TBW_Time
			+ " integer not null, " + TBW_EndTime + " integer," + TBW_ForTime
			+ " integer not null, " + TBW_NumTables + " integer not null, "
			+ TBW_NumGuests + " integer not null, " + TBW_ExpWaiting
			+ " integer, " + TBW_GuestName + " text, " + TBW_GuestContactNo
			+ " text, " + TBW_ConvCurrent + " integer, " + TBW_WasAdvance
			+ " integer);";

	public static final String TBA_Title = "title";
	public static final String TBA_UID = "_id";
	public static final String TBA_Date = "rsdate";
	public static final String TBA_Time = "rstime";
	public static final String TBA_ForDate = "rsfordate";
	public static final String TBA_ForTime = "rsfortime";
	public static final String TBA_NumTables = "numtables";
	public static final String TBA_NumGuests = "numguests";
	public static final String TBA_ExpDuration = "expduration";
	public static final String TBA_GuestName = "guestname";
	public static final String TBA_GuestContactNo = "contactno";
	public static final String TBA_ConvCurrent = "convertedcurrent";
	public static final String TBA_WasWaiting = "waswaiting";
	private static final String DB_AdvanceCreation = "create table "
			+ DBTable_Advance + "(" + TBA_UID + " integer primary key, "
			+ TBA_Date + " integer not null, " + TBA_Time
			+ " integer not null, " + TBA_ForDate + " integer not null, "
			+ TBA_ForTime + " integer not null, " + TBA_NumTables
			+ " integer not null, " + TBA_NumGuests + " integer not null, "
			+ TBA_ExpDuration + " integer, " + TBA_GuestName + " text, "
			+ TBA_GuestContactNo + " text, " + TBA_ConvCurrent + " integer, "
			+ TBA_WasWaiting + " integer);";

	private static final String TAG = "TRSDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mCtx;

	private class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_CWAName, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DB_CurrentCreation);
			db.execSQL(DB_WaitingCreation);
			db.execSQL(DB_AdvanceCreation);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DBTable_Current);
			db.execSQL("DROP TABLE IF EXISTS " + DBTable_Waiting);
			db.execSQL("DROP TABLE IF EXISTS " + DBTable_Advance);
			onCreate(db);

		}

	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public TRSDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw an exception to signal
	 * the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public TRSDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new current entry using the info provided. If entry is
	 * successfully created return the new rowId for that entry, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param request_ts
	 *            long:timestamp in milliseconds representing when the request
	 *            was generated
	 * @param res_date
	 *            long:current date in milliseconds representing the date of
	 *            reservation
	 * @param res_time
	 *            long:time in milliseconds representing the time of reservation
	 * @param numtables
	 *            int:number of tables to be booked for current reservation
	 * @param numguests
	 *            int:number of guests
	 * @param expduration
	 *            int:the expected duration entered or default duration
	 * @param guestname
	 *            String: name of the guest
	 * @param contactno
	 *            String: contact number of guest
	 * @param waswaiting
	 *            long: if the current guest was waiting, then the waiting UID
	 *            else if not waiting then -1 else if no idea whether he was
	 *            waiting or not then 0
	 * @param wasadvance
	 *            long: if the current guest was advance booking, then the
	 *            advance UID else if not advance then -1 else if no idea
	 *            whether he was advance or not then 0
	 * @return rowId or -1 if failed
	 */
	public long NewCurrentEntry(long request_ts, long res_date, long res_time,
			int numtables, int numguests, int expduration, String guestname,
			String contactno, long waswaiting, long wasadvance) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TBC_UID, request_ts);
		initialValues.put(TBC_Date, res_date);
		initialValues.put(TBC_Time, res_time);
		initialValues.put(TBC_EndTime, (long)CurrentLive);
		initialValues.put(TBC_NumTables, numtables);
		initialValues.put(TBC_NumGuests, numguests);
		initialValues.put(TBC_ExpDuration, expduration);
		initialValues.put(TBC_GuestName, guestname);
		initialValues.put(TBC_GuestContactNo, contactno);
		initialValues.put(TBC_WasWaiting, waswaiting);
		initialValues.put(TBC_WasAdvance, wasadvance);
		long newcurentry=mDb.insert(DBTable_Current, null, initialValues);
		if (newcurentry!=-1){			
			//added...now if the waswaiting is not -1 then update the waiting table
			if(waswaiting!=-1){
				UpdateWaitingToCurrent(waswaiting, request_ts, res_time);
				//how do we know whether this has been updated 
			}
			if(wasadvance!=-1){
				UpdateAdvanceToCurrent(wasadvance, request_ts);
			}
		}			
		return newcurentry;
	}

	/**
	 * Create a new waiting entry using the info provided. If entry is
	 * successfully created return the new rowId for that entry, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param request_ts
	 *            long:timestamp in milliseconds representing when the request
	 *            was generated
	 * @param res_date
	 *            long:current date in milliseconds representing the date of
	 *            reservation
	 * @param res_time
	 *            long:time in milliseconds representing the time of waiting
	 *            start
	 * @param res_for_time
	 *            long:time in milliseconds representing the time of reservation
	 *            asked for
	 * @param numtables
	 *            int:number of tables to be booked for current reservation
	 * @param numguests
	 *            int:number of guests
	 * @param expduration
	 *            int:the expected duration entered or default duration
	 * @param guestname
	 *            String: name of the guest
	 * @param contactno
	 *            String: contact number of guest
	 * @param convcurrent
	 *            long: if the this waiting was converted into current booking
	 *            then current UID, else if not converted then -1 else 0
	 * @param wasadvance
	 *            long: if the current guest was advance booking, then the
	 *            advance UID else if not advance then -1 else if no idea
	 *            whether he was advance or not then 0
	 * @return rowId or -1 if failed
	 */
	public long NewWaitingEntry(long request_ts, long res_date, long res_time,
			long res_for_time, int numtables, int numguests, int expwaiting,
			String guestname, String contactno, long wasadvance) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TBW_UID, request_ts);
		initialValues.put(TBW_Date, res_date);
		initialValues.put(TBW_Time, res_time);
		initialValues.put(TBW_EndTime, (long)WaitingAlive);
		initialValues.put(TBW_ForTime, res_for_time);
		initialValues.put(TBW_NumTables, numtables);
		initialValues.put(TBW_NumGuests, numguests);
		initialValues.put(TBW_ExpWaiting, expwaiting);
		initialValues.put(TBW_GuestName, guestname);
		initialValues.put(TBW_GuestContactNo, contactno);
		initialValues.put(TBW_ConvCurrent, (long)WaitingAlive);
		initialValues.put(TBW_WasAdvance, wasadvance);
		return mDb.insert(DBTable_Waiting, null, initialValues);
	}

	/**
	 * Create a new waiting entry using the info provided. If entry is
	 * successfully created return the new rowId for that entry, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param request_ts
	 *            long:timestamp in milliseconds representing when the request
	 *            was generated
	 * @param res_date
	 *            long:current date in milliseconds representing the date of
	 *            reservation
	 * @param res_time
	 *            long:time in milliseconds representing the time of waiting
	 *            start
	 * @param res_for_time
	 *            long:time in milliseconds representing the time of reservation
	 *            asked for..it only stores the time...date is stored for date field...
	 * @param numtables
	 *            int:number of tables to be booked for current reservation
	 * @param numguests
	 *            int:number of guests
	 * @param expduration
	 *            int:the expected duration entered or default duration
	 * @param guestname
	 *            String: name of the guest
	 * @param contactno
	 *            String: contact number of guest
	 * @param convcurrent
	 *            long: if the this advance was converted into current booking
	 *            then current UID, if live booking then AdvanceAlive...
	 * @param waswaiting
	 *            long: if the current guest was waiting, then the waiting UID
	 *            else if not waiting then -1 else if no idea whether he was
	 *            waiting or not then 0
	 * @return rowId or -1 if failed
	 */
	public long NewAdvanceEntry(long request_ts, long res_date, long res_time,
			long res_for_date, long res_for_time, int numtables, int numguests,
			int expduration, String guestname, String contactno) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(TBA_UID, request_ts);
		initialValues.put(TBA_Date, res_date);
		initialValues.put(TBA_Time, res_time);
		initialValues.put(TBA_ForDate, res_for_date);
		initialValues.put(TBA_ForTime, res_for_time);
		initialValues.put(TBA_NumTables, numtables);
		initialValues.put(TBA_NumGuests, numguests);
		initialValues.put(TBA_ExpDuration, expduration);
		initialValues.put(TBA_GuestName, guestname);
		initialValues.put(TBA_GuestContactNo, contactno);
		initialValues.put(TBA_ConvCurrent, (long)AdvanceAlive);
		initialValues.put(TBA_WasWaiting, (long)AdvanceAlive);
		return mDb.insert(DBTable_Advance, null, initialValues);
	}
	
    /**
     * Close the current using the details provided. The current to be updated is
     * specified using the Current UID, 
     * @param UID id of current to update
     * @param endtime :long time in milliseconds representing close of the current res
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean CloseCurrent(long uid, long closetime) {
        ContentValues args = new ContentValues();
        args.put(TBC_EndTime, closetime);                
        return mDb.update(DBTable_Current, args, TBC_UID + "=" + uid, null) > 0;
    }    
    
    /**
     * Close the waiting by updating ConvCurrent and TBW_EndTime with the values supplied. This method is called when a waiting is shifted to current
     * specified using the Current UID, 
     * @param wait_uid id of waiting to update
     * @param curr_uid long: current id to be supplied to convcurrent, showing which current uid does this waiting represnt
     * @param wait_endtime :long time in milliseconds representing close of the waiting
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean UpdateWaitingToCurrent(long wait_uid,long curr_uid,long wait_endtime){
    	ContentValues args = new ContentValues();
        args.put(TBW_EndTime, wait_endtime);
        args.put(TBW_ConvCurrent, curr_uid);
        return mDb.update(DBTable_Waiting, args, TBW_UID + "=" + Long.toString(wait_uid) , null) > 0;
    }
    
    /**
     * Close the advance by updating ConvCurrent with the values supplied. This method is called when a advance is shifted to current
     * specified Current UID, 
     * @param advance_uid id of waiting to update
     * @param curr_uid long: current id to be supplied to convcurrent, showing which current uid does this waiting represnt     * 
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean UpdateAdvanceToCurrent(long advance_uid,long curr_uid){
    	ContentValues args = new ContentValues();        
        args.put(TBA_ConvCurrent, curr_uid);
        return mDb.update(DBTable_Advance, args, TBA_UID + "=" + Long.toString(advance_uid) , null) > 0;
    }
    
    public boolean UpdateAdvanceToWaiting(long advance_uid,long wait_uid){
    	ContentValues args = new ContentValues();        
        args.put(TBA_WasWaiting, wait_uid);
        return mDb.update(DBTable_Advance, args, TBA_UID + "=" + Long.toString(advance_uid) , null) > 0;
    }
	
    /**
     * Cancels the waiting using the details provided. The waiting to be updated is
     * specified using the Current UID. Sets TBW_EndTime to WaitingCancelled 
     * @param UID id of waiting to update
     * @param long endtimets : the time at which event was closed
     * 
     * @return true if the waiting was successfully updated, false otherwise
     */
    public boolean CancelWaiting(long uid,long endtimets) {
        ContentValues args = new ContentValues();
        args.put(TBW_EndTime, endtimets);    
        args.put(TBW_ConvCurrent,(long)-1);
        return mDb.update(DBTable_Waiting, args, TBW_UID + "=" + Long.toString(uid), null) > 0;
    }
    
    /**
     * Cancels the advance using the details provided. The advance to be updated is
     * specified using the advance UID. Sets TBA_ConvCurrent to AdvanceNotAliveNotConvCurrent 
     * @param UID id of waiting to update
     * 
     * @return true if the waiting was successfully updated, false otherwise
     */
    public boolean CancelAdvance(long uid) {
        ContentValues args = new ContentValues();
        args.put(TBA_ConvCurrent, (long)AdvanceNotAliveNotConvCurrent);                
        return mDb.update(DBTable_Advance, args, TBA_UID + "=" + Long.toString(uid), null) > 0;
    }
    
    
    /**
     * Delete the current with the given UID
     * 
     * @param UId id of current to delete
     * @return true if deleted, false otherwise
     */
    public boolean DeleteCurrent(long uid) {
        return mDb.delete(DBTable_Current, TBC_UID + "=" + Long.toString(uid), null) > 0;
    }
    
    /**
     * Delete the waiting with the given UID
     * 
     * @param UId id of waiting to delete
     * @return true if deleted, false otherwise
     */
    public boolean DeleteWaiting(long uid) {
        return mDb.delete(DBTable_Waiting, TBW_UID + "=" + Long.toString(uid), null) > 0;
    }
    
    /**
     * Delete the advance with the given UID
     * 
     * @param UId id of advance to delete
     * @return true if deleted, false otherwise
     */
    public boolean DeleteAdvance(long uid) {
        return mDb.delete(DBTable_Advance, TBA_UID + "=" + Long.toString(uid), null) > 0;
    }
    

    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate which are open/active
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsCurrentOpen(long resdate) {
    	String[] columns = {TBC_UID,TBC_Date,TBC_Time,	TBC_EndTime,TBC_ExpDuration,TBC_NumTables,TBC_NumGuests,TBC_GuestName,TBC_GuestContactNo};
    	String selection =TBC_Date+"=? and "+TBC_EndTime+"=?";    
    	String[] selectionArgs= {Long.toString(resdate),Integer.toString(CurrentLive)};
    	String orderBy=TBC_UID+" DESC";
        return mDb.query(DBTable_Current, columns, selection, selectionArgs, null, null, orderBy);
    }
    

    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate which are closed
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsCurrentClosed(long resdate) {
    	String[] columns = {TBC_UID,TBC_Date,TBC_Time,	TBC_EndTime,TBC_ExpDuration,TBC_NumTables,TBC_NumGuests,TBC_GuestName,TBC_GuestContactNo};
    	String selection =TBC_Date+"=? and "+TBC_EndTime+"!=?";    
    	String[] selectionArgs= {Long.toString(resdate),Integer.toString(CurrentLive)};
    	String orderBy=TBC_UID+" DESC";
        return mDb.query(DBTable_Current, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsWaitingAlive(long resdate,int WaitingStatus) {
    	String[] columns = {TBW_UID,TBW_Date,TBW_Time,TBW_ForTime,TBW_EndTime,TBW_ExpWaiting,TBW_NumTables,TBW_NumGuests,TBW_GuestName,TBW_GuestContactNo,TBW_ConvCurrent};
    	String selection =TBW_Date+"=? and "+TBW_EndTime+"=?";    
    	String[] selectionArgs= {Long.toString(resdate),Integer.toString(WaitingStatus)};
    	String orderBy=TBW_UID+" ASC";
        return mDb.query(DBTable_Waiting, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    
    public Cursor fetchDetailsWaitingNotAlive(long resdate) {
    	String[] columns = {TBW_UID,TBW_Date,TBW_Time,TBW_ForTime,TBW_EndTime,TBW_ExpWaiting,TBW_NumTables,TBW_NumGuests,TBW_GuestName,TBW_GuestContactNo,TBW_ConvCurrent};
    	String selection =TBW_Date+"=? and "+TBW_ConvCurrent+"!=?";    
    	String[] selectionArgs= {Long.toString(resdate),Integer.toString(WaitingAlive)};
    	String orderBy=TBW_UID+" ASC";
        return mDb.query(DBTable_Waiting, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsAdvance(long resdate) {
    	String[] columns = {TBA_UID,TBA_Date,TBA_Time,TBA_ForDate,TBA_ForTime,TBA_ExpDuration,TBA_NumTables,TBA_NumGuests,TBA_GuestName,TBA_GuestContactNo,TBA_ConvCurrent,TBA_WasWaiting};
    	String selection =TBA_ForDate+"=?";    
    	String[] selectionArgs= {Long.toString(resdate)};
    	String orderBy=TBA_Date+" ASC, "+ TBA_ForTime+" ASC";
        return mDb.query(DBTable_Advance, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all the alive advanced bookings beyond this date
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsAdvanceAlive(long resdate) {
    	String[] columns = {TBA_UID,TBA_Date,TBA_Time,TBA_ForDate,TBA_ForTime,TBA_ExpDuration,TBA_NumTables,TBA_NumGuests,TBA_GuestName,TBA_GuestContactNo,TBA_ConvCurrent,TBA_WasWaiting};
    	String selection =TBA_ForDate+">=?  and "+TBA_ConvCurrent+"=?";    
    	String[] selectionArgs= {Long.toString(resdate),Integer.toString(AdvanceAlive)};
    	String orderBy=TBA_ForDate+" ASC, "+TBA_ForTime+" ASC";
        return mDb.query(DBTable_Advance, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all the alive advanced bookings beyond this date
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsAdvanceNotAlive(long resdate) {
    	String[] columns = {TBA_UID,TBA_Date,TBA_Time,TBA_ForDate,TBA_ForTime,TBA_ExpDuration,TBA_NumTables,TBA_NumGuests,TBA_GuestName,TBA_GuestContactNo,TBA_ConvCurrent,TBA_WasWaiting};
    	//String selection =TBA_ForDate+"<=?  and "+TBA_ConvCurrent+"!=?";    
    	String selection =TBA_ConvCurrent+"!=?";
    	//String[] selectionArgs= {Long.toString(resdate),Integer.toString(AdvanceAlive)};
    	String[] selectionArgs= {Integer.toString(AdvanceAlive)};
    	String orderBy=TBA_ForDate+" ASC, "+TBA_ForTime+" ASC";
        return mDb.query(DBTable_Advance, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate..basically all final reservations...
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsFinal(long resdate) {
    	String[] columns = {TBC_UID,TBC_Date,TBC_Time,	TBC_EndTime,TBC_ExpDuration,TBC_NumTables,TBC_NumGuests,TBC_GuestName,TBC_GuestContactNo};
    	String selection =TBC_Date+"=?";    
    	String[] selectionArgs= {Long.toString(resdate)};
    	String orderBy=TBC_UID+" ASC";
        return mDb.query(DBTable_Current, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    /**
     * Return a Cursor over the list of all entries in the corresponding table for resdate
     * 
     * @return Cursor containing all the details 
     */
    public Cursor fetchDetailsWaiting(long resdate) {
    	String[] columns = {TBW_UID,TBW_Date,TBW_Time,TBW_ForTime,TBW_EndTime,TBW_ExpWaiting,TBW_NumTables,TBW_NumGuests,TBW_GuestName,TBW_GuestContactNo,TBW_ConvCurrent};
    	String selection =TBW_Date+"=?";    
    	String[] selectionArgs= {Long.toString(resdate)};
    	String orderBy=TBW_UID+" ASC";
        return mDb.query(DBTable_Waiting, columns, selection, selectionArgs, null, null, orderBy);
    }
    
    
    

}
