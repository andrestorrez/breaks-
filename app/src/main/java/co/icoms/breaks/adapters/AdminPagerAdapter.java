package co.icoms.breaks.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import co.icoms.breaks.HomeActivity;
import co.icoms.breaks.tabs.HolidaysFragment;
import co.icoms.breaks.tabs.PeopleFragment;
import co.icoms.breaks.tabs.RequestsFragment;

/**
 * Created by escolarea on 12/5/16.
 */

public class AdminPagerAdapter extends FragmentPagerAdapter {
    public AdminPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                PeopleFragment people = new PeopleFragment();
                return people;
            case 1:
                RequestsFragment requests = new RequestsFragment();
                return requests;
            case 2:
                HolidaysFragment holidays = new HolidaysFragment();
                return holidays;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "People";
            case 1:
                return "Requests";
            case 2:
                return "Holidays";
        }
        return null;
    }
}
