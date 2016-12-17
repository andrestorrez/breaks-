package co.icoms.breaks;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;


import co.icoms.breaks.adapters.AdminPagerAdapter;
import co.icoms.breaks.adapters.UserPagerAdapter;
import co.icoms.breaks.models.User;

public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private FragmentPagerAdapter mSectionsPagerAdapter;



    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Check user signed in
        current_user = User.current_user(this);

        if (current_user == null){
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
        }else {


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            if (current_user.isAdmin())
                mSectionsPagerAdapter = new AdminPagerAdapter(getSupportFragmentManager());
            else
                mSectionsPagerAdapter = new UserPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }
/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.acction_log_out){
            User.logOut(this);
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public User getCurrent_user() {
        return current_user;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //super.onBackPressed();
    }
}
