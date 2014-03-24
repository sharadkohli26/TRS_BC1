package com.example.trs_bc1;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Window;

public class EditNumDialog extends SherlockDialogFragment implements OnEditorActionListener{
	
	private EditText mEditText;
    private TextView mTextView;

    public interface EditNumDialogListener {
        void onFinishEditNumDialog(String TargetTitle,String TargetPrefName,String inputText);
    }
    
    static EditNumDialog newInstance(String Title,String TargetPrefName,int currvalue){
    	EditNumDialog new_end = new EditNumDialog();
    	Bundle args = new Bundle();    	
    	args.putString("Title",Title);
    	args.putString("TargetPrefName",TargetPrefName);
    	args.putInt("CurrValue",currvalue);
    	new_end.setArguments(args);
    	return new_end;    	
    }
 
    public EditNumDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {    	
        View view = inflater.inflate(R.layout.edit_num_dialog, container);
        mEditText = (EditText) view.findViewById(R.id.edit_num_input);
        mTextView = (TextView) view.findViewById(R.id.edit_num_Head);
                
        getDialog().getWindow().requestFeature((int) Window.FEATURE_NO_TITLE);        
        getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                
        // Show soft keyboard automatically
        String TextViewTitle = getArguments().getString("Title")+":"+Integer.toString(getArguments().getInt("CurrValue"));
        mTextView.setText(TextViewTitle);
        mTextView.setTextSize(20);
        //mTextView.setTextColor(Color.BLUE);
        mEditText.requestFocus();        
        mEditText.setOnEditorActionListener(this);       
        return view;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
        	if (mEditText.getText().toString().isEmpty()){
        		dismiss();
        		return false;        		
        	}        		
            EditNumDialogListener parentactivity = (EditNumDialogListener) getActivity();
            String TargetPrefName=getArguments().getString("TargetPrefName");
            String TargetTitle=getArguments().getString("Title");
            parentactivity.onFinishEditNumDialog(TargetTitle,TargetPrefName,mEditText.getText().toString());            
            dismiss();
            return true;
        }
        dismiss();
		return false;
	}
}