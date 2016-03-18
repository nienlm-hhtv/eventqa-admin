package com.hhtv.eventqa_admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.fragments.EditEventFragment;
import com.hhtv.eventqa_admin.fragments.EventListFragment;
import com.hhtv.eventqa_admin.fragments.TestFragment;
import com.hhtv.eventqa_admin.helpers.UserUtils;
import com.hhtv.eventqa_admin.models.event.Result;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GetUserResponse user = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (user == null){
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_name)).setText(user.getUsername());
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_email)).setText(user.getEmail());

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EventListFragment f = EventListFragment.newInstance(user);
        transaction.add(R.id.main_framelayout, f);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final FragmentManager f = getSupportFragmentManager();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(f.getBackStackEntryCount() != 0) {
            Fragment curFragment = f.findFragmentById(R.id.main_framelayout);
            if (curFragment instanceof EditEventFragment){
                 new MaterialDialog.Builder(this)
                        .title("Exit")
                        .content("Your edit will be lost")
                        .positiveText("Stay")
                        .negativeText("Exit")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                f.popBackStack();
                                dialog.dismiss();
                            }
                        }).show();
            }else{
                f.popBackStack();
            }

        } else {
            super.onBackPressed();
        }
    }

    public void removeCurrentFragment(){
        FragmentManager f = getSupportFragmentManager();
        if(f.getBackStackEntryCount() != 0)
            f.popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TestFragment f;
        if (id == R.id.nav_camera) {
            // Handle the camera action
            EventListFragment eFragment = EventListFragment.newInstance(user);
            transaction.replace(R.id.main_framelayout, eFragment);
            transaction.commit();
        } else if (id == R.id.nav_gallery) {
            EditEventFragment editEventFragment = EditEventFragment.newInstance(user, EditEventFragment.ActionType.CREATE, -1);
            transaction.replace(R.id.main_framelayout, editEventFragment).addToBackStack("EditEventFragment");
            transaction.commit();
        } else if (id == R.id.nav_share) {
            f = TestFragment.newInstance("share");
            transaction.replace(R.id.main_framelayout, f);
            transaction.commit();
        } else if (id == R.id.nav_send) {
            new MaterialDialog.Builder(this)
                    .title("Confirm log out")
                    .content("Do you want to log out ?")
                    .positiveText("Log out")
                    .negativeText("Dismiss")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    }).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    dialog.dismiss();
                    UserUtils.logout(MainActivity.this);
                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }).show();

        } else{
            f = TestFragment.newInstance(" ??? ");
            transaction.replace(R.id.main_framelayout, f);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToEventEditPage(int eventId){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        EditEventFragment editEventFragment = EditEventFragment.newInstance(user, EditEventFragment.ActionType.EDIT, eventId);
        transaction.replace(R.id.main_framelayout, editEventFragment).addToBackStack("EditEventFragment");
        transaction.commit();
    }

    public void goToEventViewPage(Result event){
        Intent i = new Intent(this, EventDetailActivity.class);
        i.putExtra("event", Parcels.wrap(event));
        startActivity(i);
    }

}
