package com.example.trs_bc1;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SDR_TV {
	LinearLayout container;
	TextView header;
	TextView sub;
	
	public SDR_TV(SpecificDateReservations p_activity,int contid,int headid,int subid){
		container = (LinearLayout) p_activity.findViewById(contid);
		header = (TextView) p_activity.findViewById(headid);
		sub = (TextView) p_activity.findViewById(subid);
	}
	
	public void SDR_TV_selected(SpecificDateReservations p_activity){
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_selected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,LinearLayout.LayoutParams.MATCH_PARENT , 2f));
		header.setTextAppearance(p_activity, R.style.MainTextViewSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewSelected);	
	}
	public void SDR_TV_notselected(SpecificDateReservations p_activity){
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, p_activity.getResources().getDisplayMetrics());
		container.setBackgroundResource(R.drawable.maintextview_notselected);
		container.setLayoutParams(new LinearLayout.LayoutParams(px,LinearLayout.LayoutParams.MATCH_PARENT , 1f));
		header.setTextAppearance(p_activity, R.style.MainTextViewNotSelected);
		sub.setTextAppearance(p_activity, R.style.SubTextViewNotSelected);	
	}
	public void SDR_TV_setSubText(String text){
		sub.setText(text);		
	}
	public void SDR_VisibilityGone(){
		container.setVisibility(View.GONE);
	}
	
	public void SDR_VisibilityVisible(){
		container.setVisibility(View.VISIBLE);
	}
}
