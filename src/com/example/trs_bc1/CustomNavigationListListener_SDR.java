package com.example.trs_bc1;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CustomNavigationListListener_SDR implements OnNavigationListener {
	// this is for live reservations..make another listener for custom

	public interface CustomNavigationListReturns_SDR {
		void CustomNavListRet(int NavListItemCode);
	}

	SherlockFragmentActivity m_activity;
	String[] m_titlearray;// title array to be displayed

	CustomNavigationListListener_SDR(SherlockFragmentActivity activity_passed,
			String[] titlearray_passed) {
		m_activity = activity_passed;
		m_titlearray = titlearray_passed;
		// m_titlearray=titlearray_passed;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// the above is only for LiveReservations...
		CustomNavigationListReturns_SDR parentactivity = (CustomNavigationListReturns_SDR) m_activity;
		if (itemPosition == 0) {
			parentactivity
					.CustomNavListRet(LiveReservations.CustNavListItem_SAMEDATE);
		} else if (itemPosition == 1) {
			//m_activity.getSupportActionBar().setSelectedNavigationItem(0);
			parentactivity
					.CustomNavListRet(LiveReservations.CustNavListItem_TODAYVIEWDATE);
		} else {
			m_activity.getSupportActionBar().setSelectedNavigationItem(0);
			parentactivity
					.CustomNavListRet(LiveReservations.CustNavListItem_CHANGEDATE);
		}
		return true;
		/*
		 * else if (m_content.equals(m_titlearray[itemPosition])){ return true;
		 * }
		 */
	}
}