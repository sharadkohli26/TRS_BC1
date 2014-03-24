package com.example.trs_bc1;

import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LR_TV {
	LinearLayout container;
	TextView header;
	TextView sub;
	
	public LR_TV(LiveReservations p_activity,int contid,int headid,int subid){
		container = (LinearLayout) p_activity.findViewById(contid);
		header = (TextView) p_activity.findViewById(headid);
		sub = (TextView) p_activity.findViewById(subid);
	}
	
	public void LR_TV_selected(LiveReservations p_activity){
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_selected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,LinearLayout.LayoutParams.MATCH_PARENT , 2f));
		header.setTextAppearance(p_activity, R.style.MainTextViewSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewSelected);	
	}
	public void LR_TV_notselected(LiveReservations p_activity){
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_notselected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,LinearLayout.LayoutParams.MATCH_PARENT , 1f));
		header.setTextAppearance(p_activity, R.style.MainTextViewNotSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewNotSelected);	
	}
	public void LR_TV_setSubText(String text){
		sub.setText(text);		
	}

}
