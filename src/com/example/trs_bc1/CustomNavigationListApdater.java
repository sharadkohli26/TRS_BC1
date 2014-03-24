package com.example.trs_bc1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Custom navigation list adapter.
 */
class CustomNavigationListAdapter extends BaseAdapter implements SpinnerAdapter {
	/**
	 * Members
	 */
	private LayoutInflater m_layoutInflater;
	private String[] m_titles;
	private String[] m_subtitles;
	private String m_currentselected;
	//private TypedArray m_titles;
	//private TypedArray m_subtitles;

	/**
	 * Constructor
	 */
	public CustomNavigationListAdapter(Context p_context, String[] p_titles, String[] p_subtitles,String CurrentSelected) {
		m_layoutInflater = LayoutInflater.from(p_context);		
		m_titles = p_titles;
		m_subtitles = p_subtitles;
		m_currentselected=CurrentSelected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return m_titles.length;
		//return m_titles.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int p_position) {
		return p_position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int p_position) {		
		//return m_titles.getResourceId(p_position, 0);
		return p_position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
		
	@Override
	public View getView(int p_position, View p_convertView, ViewGroup p_parent) {
		/*
		 * View...
		 */
		View view = p_convertView;
		if (view == null) {
			view = m_layoutInflater.inflate(R.layout.navigation_list_item,p_parent, false);
		}
		/*
		 * Display...
		 */

		TextView tv_title = (TextView) view.findViewById(R.id.nav_item_title);		// Title...
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.nav_item_subtitle));		// Subtitle...
		
		tv_title.setText(m_titles[p_position]);
		tv_subtitle.setText(m_subtitles[p_position]);
		tv_subtitle.setVisibility("".equals(tv_subtitle.getText()) ? View.GONE:View.VISIBLE);										
		/*
		 * Return...
		 */		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int p_position, View p_convertView,	ViewGroup p_parent) {
		/*
		 * View...
		 */

		View view = p_convertView;
		if (view == null) {
			view = m_layoutInflater.inflate(R.layout.navigation_list_dropdown_item, p_parent, false);
		}

		/*
		 * Display...
		 */

		// Icon...		

		// Title...
		TextView tv_title = (TextView) view.findViewById(R.id.nav_list_item_title);
		tv_title.setText(m_titles[p_position]);

		// Subtitle...
		
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.nav_list_item_subtitle));
		tv_subtitle.setText(m_subtitles[p_position]);		
		

		if(m_titles[p_position].equals(m_currentselected)){
			tv_title.setHeight(0);
			tv_subtitle.setHeight(0);
			//tv_title.setText("");
			//tv_subtitle.setText("");			
		}
		else{
			tv_title.setHeight(50);
			tv_subtitle.setHeight(50);
			if ("".equals(tv_subtitle.getText())){
				tv_subtitle.setVisibility("".equals(tv_subtitle.getText()) ? View.GONE : View.VISIBLE);
				tv_title.setHeight(100);
			}
			
			//tv_title.setHeight(LayoutParams.WRAP_CONTENT);
			//tv_subtitle.setHeight(LayoutParams.WRAP_CONTENT);
		}
			
		

		/*
		 * Return...
		 */
		return view;
	}	

}
