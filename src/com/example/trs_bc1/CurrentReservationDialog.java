package com.example.trs_bc1;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;

public class CurrentReservationDialog extends SherlockDialogFragment {
	/*
	 * Initialise view variables i.e all the text view and edit view
	 */
	
	private TextView CRD_Error;	

	private EditText Num_Table_View;
	private EditText Num_Guests_View;
	private EditText Occupancy_View;
	private EditText GuestName_View;
	private EditText ContactNo_View;

	private TextView Set_View;
	private TextView Cancel_View;
	
	int MaxNumTables;
	long mwaituid;
	long madvuid;

	public CurrentReservationDialog() {
		// Empty constructor required for DialogFragment
	}

	static CurrentReservationDialog newInstance1(String DefOccupancy,int m_MaxNumTables){
		CurrentReservationDialog crd = new CurrentReservationDialog();
		Bundle args = new Bundle();
		args.putString("DefOccupancy", DefOccupancy);
		args.putInt("MaxNumTables", m_MaxNumTables);
		crd.setArguments(args);
		return crd;		
	}
	static CurrentReservationDialog newInstance2(String DefOccupancy,int m_MaxNumTables, int numtables,int numguests, String name,String contact,long waituid, long advuid) {
		CurrentReservationDialog crd = new CurrentReservationDialog();
		Bundle args = new Bundle();
		args.putString("DefOccupancy", DefOccupancy);
		args.putInt("MaxNumTables", m_MaxNumTables);
		if(numtables!=-1)
			args.putInt("numtables", numtables);
		if(numguests!=-1)
			args.putInt("numguests", numguests);
		if(name!=null)
			args.putString("guestname", name);
		if(contact!=null)
			args.putString("contactno", contact);
		if(waituid!=-1)
			args.putLong("waituid", waituid);
		if(advuid!=-1)
			args.putLong("advuid", advuid);
		//now load the args if supplied
		
		
		crd.setArguments(args);
		return crd;
	}

	public interface CurrentReservationDialogListener {
		void CurrentReservation(int crdNumTables,int crdNumGuests,int crdDuration,String crdGuestNames,String crdContactNos,long waituid,long advuid);		
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_live_booking, container);
		
		MaxNumTables=this.getArguments().getInt("MaxNumTables");
		mwaituid = this.getArguments().getLong("waituid");
		madvuid=this.getArguments().getLong("advuid");
		
		initiliaseAllViews(view);
		
		if(this.getArguments().containsKey("numtables"))
			Num_Table_View.setText(Integer.toString(this.getArguments().getInt("numtables")));
		if(this.getArguments().containsKey("numguests"))
			Num_Guests_View.setText(Integer.toString(this.getArguments().getInt("numguests")));
		if(this.getArguments().containsKey("guestname"))
			GuestName_View.setText(this.getArguments().getString("guestname"));
		if(this.getArguments().containsKey("contactno"))
			ContactNo_View.setText(this.getArguments().getString("contactno"));							
		
		getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);
		getDialog().setTitle("Current Reservation");
		getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		// getDialog().getWindow().setLayout(-2,600);

		return view;
	}

	public void initiliaseAllViews(View view) {

		String DefOcc = this.getArguments().getString("DefOccupancy");
		
		CRD_Error = (TextView) view.findViewById(R.id.crd_Err);

		Num_Table_View = (EditText) view.findViewById(R.id.num_tables);
		Num_Guests_View = (EditText) view.findViewById(R.id.num_guests);
		Occupancy_View = (EditText) view.findViewById(R.id.exp_occupancy);
		
		Occupancy_View.setText(DefOcc);
		
		GuestName_View = (EditText) view.findViewById(R.id.guest_name);
		ContactNo_View = (EditText) view.findViewById(R.id.guest_phone);
		Set_View = (TextView) view.findViewById(R.id.crd_set);
		Cancel_View = (TextView) view.findViewById(R.id.crd_cancel);
		
		SetClickListener();
		CancelClickListener();
	}

	private void CancelClickListener() {
		// TODO Auto-generated method stub
		Cancel_View.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//CurrentReservationDialogListener parentactivity = (CurrentReservationDialogListener) getActivity();				
				dismiss();
			}
		});	
	}

	private void SetClickListener() {
		// TODO Auto-generated method stub
		Set_View.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CurrentReservationDialogListener parentactivity = (CurrentReservationDialogListener) getActivity();
				if(Num_Table_View.getText().toString().length()==0||Num_Table_View.getText().toString().equals("0")){					
					CRD_Error.setText("Number of Tables is blank");
					CRD_Error.setVisibility(View.VISIBLE);
					Num_Table_View.setFocusable(true);
					Num_Table_View.requestFocus();					
				}
				else if(Integer.parseInt(Num_Table_View.getText().toString())>MaxNumTables){
					CRD_Error.setText("#Tables exceeds Tables available("+Integer.toString(MaxNumTables)+")");
					CRD_Error.setVisibility(View.VISIBLE);
					Num_Table_View.setFocusable(true);
					Num_Table_View.requestFocus();							
				}
				else if(Num_Guests_View.getText().toString().length()==0){
					CRD_Error.setText("Number of Guests is blank");
					CRD_Error.setVisibility(View.VISIBLE);
					Num_Guests_View.setFocusable(true);
					Num_Guests_View.requestFocus();							
				}
				else{
					int NumTables = Integer.parseInt(Num_Table_View.getText().toString());
					int NumGuests = Integer.parseInt(Num_Guests_View.getText().toString());
					int mDuration = Integer.parseInt(Occupancy_View.getText().toString());
					String GuestNames =  GuestName_View.getText().toString();
					String ContactNos =  ContactNo_View.getText().toString();					
					parentactivity.CurrentReservation(NumTables,NumGuests,mDuration,GuestNames,ContactNos,mwaituid,madvuid);					
					dismiss();					
				}											
																
			}
		});

	}
	
}
