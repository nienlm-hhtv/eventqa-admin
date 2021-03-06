package com.hhtv.eventqa_admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.fragments.EventHighestVoteFragment3;
import com.hhtv.eventqa_admin.fragments.EventQuestionListFragment3;
import com.hhtv.eventqa_admin.models.event.Result;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nienb on 18/3/16.
 */
public class EventDetailActivity extends AppCompatActivity {
    @Bind(R.id.eventdetail_toolbar)
    Toolbar toolbar;
    @Bind(R.id.eventdetail_container)
    ViewPager mPager;
    @Bind(R.id.eventdetail_tabs)
    TabLayout mTabs;

    private static final int[] tabIcons = {
            R.drawable.ic_trending_up_white,
            R.drawable.ic_comment_white
    };

    private static final int[] toolbarTitle = {
            R.string.tab_most_voted,
            R.string.tab_question
    };

    public void setupTabIcons() {
        for (int i = 0; i < mTabs.getTabCount(); i++) {
            mTabs.getTabAt(i).setIcon(tabIcons[i]);

        }
    }


    public Result mModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mModel = Parcels.unwrap(getIntent().getParcelableExtra("event"));
        if (mModel == null){
            Toast.makeText(EventDetailActivity.this, getResources().getString(R.string.error_occur_pls_signin), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_event_detail_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setupViewPager(mPager);
        mTabs.setupWithViewPager(mPager);
        setupTabIcons();
        setTitle(toolbarTitle[0]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    Timer mTimer;
    final int TIMER_DELAY = 5 * 1000;

    public void initTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TIMER", "timer excecute at: " + DateTime.now().toString("hh:mm:ss"));
                        reloadContent(false);
                    }
                });
            }
        }, TIMER_DELAY, TIMER_DELAY);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    public void reloadContent(boolean loading){
        EventHighestVoteFragment3 hf = (EventHighestVoteFragment3) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(0);
        hf.updateQuestionList(loading);
        EventQuestionListFragment3 f = (EventQuestionListFragment3) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(1);
        f.updateQuestion(loading);
    }

    public void reloadAllContent(){
        EventHighestVoteFragment3 hf = (EventHighestVoteFragment3) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(0);
        hf.loadQuestionList();
        EventQuestionListFragment3 f = (EventQuestionListFragment3) ((ViewPagerAdapter) mPager.getAdapter())
                .getItem(1);
        f.loadQuestionList();
    }
    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(EventHighestVoteFragment3.newInstance(Integer.parseInt(mModel.getId())), "ONE");
        adapter.addFragment(EventQuestionListFragment3.newInstance(Integer.parseInt(mModel.getId())), "TWO");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(getResources().getString(toolbarTitle[position]));

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        new MaterialDialog
                .Builder(this)
                .title(R.string.exit)
                .content(R.string.do_you_want_to_exit)
                .negativeText(R.string.dismiss)
                .theme(Theme.LIGHT)
                .positiveText(R.string.exit)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        EventDetailActivity.this.finish();
                        //overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                    }
                }).show();
    }
}
