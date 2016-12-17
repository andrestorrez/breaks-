package co.icoms.breaks.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.icoms.breaks.HomeActivity;
import co.icoms.breaks.tabs.HolidaysFragment;
import co.icoms.breaks.tabs.MyBreaksFragment;
import co.icoms.breaks.tabs.PeopleFragment;
import co.icoms.breaks.tabs.RequestsFragment;

/**
 * Created by escolarea on 12/5/16.
 */

public class UserPagerAdapter extends FragmentPagerAdapter{
    public UserPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                MyBreaksFragment myBreaks = new MyBreaksFragment();
                return myBreaks;
            case 1:
                HolidaysFragment holidays = new HolidaysFragment();
                return holidays;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Breaks";
            case 1:
                return "Holidays";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
