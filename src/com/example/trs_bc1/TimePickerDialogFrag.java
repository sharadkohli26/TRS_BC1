package com.example.trs_bc1;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TimePickerDialogFrag extends SherlockDialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	public interface TimePickerDialogFragReturns {
		void TimePickerDialogReturn(int hourOfDay, int minute);
	}
	
	static TimePickerDialogFrag newInstance(Calendar CustomTimeDate){
		TimePickerDialogFrag CDD = new TimePickerDialogFrag();
		Bundle args = new Bundle();		
		args.putSerializable("CustomTimeDate", CustomTimeDate);
		CDD.setArguments(args);
		return CDD;		
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {								
		final Calendar c = (Calendar) getArguments().getSerializable("CustomTimeDate");
		int hourofday = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog dialog=new TimePickerDialog(getActivity(), this, hourofday,minute,true);
		//dialog.getDatePicker().
		//dialog.setOnDismissListener(mOnDismissListener);		
		return dialog; 		
	}

	@Override
	public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		TimePickerDialogFragReturns parentActivity = (TimePickerDialogFragReturns) getActivity();		
		parentActivity.TimePickerDialogReturn(hourOfDay, minute);		
	}

}
