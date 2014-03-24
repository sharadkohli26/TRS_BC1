package com.example.trs_bc1;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CustomNavigationListListener implements OnNavigationListener {
	// this is for live reservations..make another listener for custom
	// basically whats happening is that if the item selected is the item
	// displayed then we return same date message ...else we return diff date..

	public interface CustomNavigationListReturns {
		void CustomNavListRet(int NavListItemCode);
	}

	SherlockFragmentActivity m_activity;
	// title array not needed....we have no tused it
	String[] m_titlearray;// title array to be displayed

	// to explore whether i need to pass the activity here or context...
	CustomNavigationListListener(SherlockFragmentActivity activity_passed,
			String[] titlearray_passed) {
		m_activity = activity_passed;
		m_titlearray = titlearray_passed;
		// m_titlearray=titlearray_passed;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// the above is only for LiveReservations...
		CustomNavigationListReturns parentactivity = (CustomNavigationListReturns) m_activity;
		if (itemPosition == 0) {
			parentactivity
					.CustomNavListRet(LiveReservations.CustNavListItem_SAMEDATE);
		} else {
/*			 basically when you click on the second displayed option...it gets
			 displayed...so we change that ...and display only the first
			 option..*/
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
