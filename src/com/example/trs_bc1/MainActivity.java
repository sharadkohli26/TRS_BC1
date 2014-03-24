package com.example.trs_bc1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockActivity {
	TextView login_error;
	EditText login_id;
	EditText login_pass;
	public static final String PreferenceName = "TRSBC1_PreferenceFavFile";
	public final static String EXTRA_loginid = "com.example.trs1.loginid";
	public final static String EXTRA_IsAuthenticated = "com.example.trs1.isauthenticated";
	public final static String Pref_loginid="login_id";
	public final static String Pref_IsAuthenticated = "IsAuthenticated";

	SharedPreferences prefname;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		prefname = getSharedPreferences(PreferenceName, 0);
		editor = prefname.edit();

		if (prefname.getBoolean(Pref_IsAuthenticated, false)) {
			// user already logged in go to another activity
			// go to the next activity and give an option of log out
			
			ActivityLiveReservations(prefname.getString(Pref_loginid, "-1"));
		}

		login_error = (TextView) findViewById(R.id.loginerror);
		login_id = (EditText) findViewById(R.id.loginemail);
		login_pass = (EditText) findViewById(R.id.loginpass);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void Login(View view) {
		String mloginid = login_id.getText().toString();
		String mpass = login_pass.getText().toString();

		if (CanConnect(mloginid, mpass)) {
			if (loginexists(mloginid, mpass)) {
				// set IsAuthenticated to true and set it to false on logout				
				// login_error.setVisibility(View.GONE);
				// code for going to next activity
				ActivityLiveReservations(mloginid);

			} else {
				DisplayLoginError(R.string.login_auth_error);
			}
		}
	}

	public boolean CanConnect(String loginid, String pass) {

		if (loginid.isEmpty()) {
			DisplayLoginError(R.string.login_blank_id);
			return false;
		}
		if (pass.isEmpty()){
			DisplayLoginError(R.string.login_blank_password);
			return false;
		}
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null || !(networkInfo.isConnected())) {
			DisplayLoginError(R.string.login_network_error);
			return false;
		}
		// check if we can recieve data with a html time out request

		return true;
	}
	
	protected boolean loginexists(String login_id,String login_pass){
		//check email id online ...currently checking here
		if (login_id.equals("admin") && login_pass.equals("admin")) {
			return true;
		}
		//RemoveUserPref();
		return false;
	}

	protected void RemoveUserPref(){
		editor.putBoolean(Pref_IsAuthenticated, false);		
		if (prefname.contains(Pref_loginid)){
			editor.remove(Pref_loginid);
		}				
		editor.commit();
	}
	
	public void ActivityLiveReservations(String mloginid){		
		if (mloginid.equalsIgnoreCase("-1"))
			return;
		else{
			AddUserPref(mloginid);
			Intent returnintent = new Intent();
			returnintent.putExtra(EXTRA_loginid, mloginid);
			returnintent.putExtra(EXTRA_IsAuthenticated, true);
		    //startActivity(intent);
			setResult(RESULT_OK, returnintent);
		    finish();
		}		
    }
	
	protected void DisplayLoginError(int err) {
		login_error.setText(err);
		login_error.setTextSize(16);
		login_error.setVisibility(View.VISIBLE);
	}
	
	protected void AddUserPref(String mlogin_id){
		RemoveUserPref();
		editor.putBoolean(Pref_IsAuthenticated, true);
		editor.putString(Pref_loginid, mlogin_id);
		editor.commit();
	}


}
