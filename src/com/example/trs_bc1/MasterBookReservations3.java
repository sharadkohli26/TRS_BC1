package com.example.trs_bc1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MasterBookReservations3 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_book_reservations3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.master_book_reservations3, menu);
		return true;
	}

}
