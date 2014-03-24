package com.example.trs_bc1;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class CustomDateDialogFrag extends SherlockDialogFragment implements
DatePickerDialog.OnDateSetListener {
	// CustomDateDialogListenerReturns m_listener;	
	
	public interface CustomDateDialogListenerReturns {
		void CustomDateDialogReturn(int year, int month, int day);
	}
	
	static CustomDateDialogFrag newInstance(Calendar CustomTimeDate){
		CustomDateDialogFrag CDD = new CustomDateDialogFrag();
		Bundle args = new Bundle();		
		args.putSerializable("CustomTimeDate", CustomTimeDate);
		CDD.setArguments(args);
		return CDD;		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {		
		// Use the current date as the default date in the picker
		
		//final Calendar c = Calendar.getInstance(TimeZone.getDefault());
		final Calendar c = (Calendar) getArguments().getSerializable("CustomTimeDate");
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		DatePickerDialog dialog=new DatePickerDialog(getActivity(), this, year, month, day);
		//dialog.getDatePicker().
		//dialog.setOnDismissListener(mOnDismissListener);		
		return dialog; 		
	}
	
	
	public void onDateSet(DatePicker view, int year, int month, int day) {
		CustomDateDialogListenerReturns parentActivity = (CustomDateDialogListenerReturns) getActivity();		
		parentActivity.CustomDateDialogReturn(year, month, day);
	}
	
/*	private DialogInterface.OnDismissListener mOnDismissListener =
	        new DialogInterface.OnDismissListener() {
	            public void onDismiss(DialogInterface dialog) {	                
	                if (!isDataSet) {  //do something, date no selected	                			
	            		CustomDateDialogListenerReturns parentActivity = (CustomDateDialogListenerReturns) getActivity();		
	            		parentActivity.CustomDateDialogReturn(false,-1, -1, -1);	                	
	                }    
	            }
	        };
*/

	
}