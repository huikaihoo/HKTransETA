package hoo.hktranseta.main.kmb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.main.kmb.adapter.KmbRouteFragmentPagerAdapter;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;
import hoo.hktranseta.main.kmb.worker.KmbDataManager;

public class KmbRouteActivity extends AppCompatActivity {

    private static final String TAG = "KmbRouteActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private KmbRouteFragmentPagerAdapter mKmbRouteFragmentPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String mKmbRouteNo;
    private Boolean mIsCircular = true;
    private List<KmbServiceType> mKmbServiceTypeList = new ArrayList<>();

    private BottomNavigationViewEx mBottomNavigationViewEx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmb_route);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get Activity based data
        mKmbRouteNo = getIntent().getStringExtra(Constants.Extra.KMB_ROUTE_NO);
        if (savedInstanceState != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(savedInstanceState.getString(Constants.Extra.ACTIONBAR_SUBTITLE));
            }
        }
        AsyncTaskCompat.executeParallel(new GetDefaultServiceTypeList(), mKmbRouteNo);

        // Set up FragmentPagerAdapter
        mKmbRouteFragmentPagerAdapter = new KmbRouteFragmentPagerAdapter(
                getSupportFragmentManager(), mKmbServiceTypeList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mKmbRouteFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mKmbRouteNo);
        }

        // Set up BottomNavigationViewEx
        mBottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav);

        mBottomNavigationViewEx.enableAnimation(true);
        mBottomNavigationViewEx.enableShiftingMode(false);
        mBottomNavigationViewEx.enableItemShiftingMode(false);

        if (savedInstanceState != null) {
            mBottomNavigationViewEx.setCurrentItem(savedInstanceState.getInt(Constants.Extra.KMB_SELECTED_ITEM_POSITION));
        } else {
            mBottomNavigationViewEx.setCurrentItem(1);
        }

        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (mKmbRouteFragmentPagerAdapter != null)
                    mKmbRouteFragmentPagerAdapter.updateAdapterData(item.getItemId(), false, false);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.Extra.KMB_ROUTE_NO, mKmbRouteNo);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            CharSequence str = actionBar.getSubtitle();
            if (str != null)
                outState.putString(Constants.Extra.ACTIONBAR_SUBTITLE, str.toString());
        }
        if (mBottomNavigationViewEx != null) {
            outState.putInt(Constants.Extra.KMB_SELECTED_ITEM_POSITION, mBottomNavigationViewEx.getCurrentItem());
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottom_nav);
//        bottomNavigationViewEx.setCurrentItem(1);
//        if (savedInstanceState != null) {
//            mKmbRouteNo = savedInstanceState.getString(Constants.Extra.KMB_ROUTE_NO);
//            if (getS)
//            getActionBar()savedInstanceState.getString(Constants.Extra.ACTIONBAR_SUBTITLE);
//        }
//    }

    public void setCircular(boolean isCircular) {
        mIsCircular = isCircular;
        setActionBarSubtitle();
    }

    private void setActionBarSubtitle() {
        Log.d(TAG, "setActionBarSubtitle");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null
                && mKmbServiceTypeList.size() > 0
                && mIsCircular != null) {
            String directionArrow;

            if (mKmbServiceTypeList.size() >= 2) {
                directionArrow = getString(R.string.arrow_two_ways);
            }
            else if (mIsCircular) {
                directionArrow = getString(R.string.arrow_circular);
            }
            else {
                directionArrow = getString(R.string.arrow_one_way);
            }

            actionBar.setSubtitle(mKmbServiceTypeList.get(0).getLocationFrom()
                    + directionArrow + mKmbServiceTypeList.get(0).getLocationTo());
        }
    }

    public void updateOnRefreshData() {
        if (mKmbRouteFragmentPagerAdapter != null)
            mKmbRouteFragmentPagerAdapter.updateOnRefreshData();
    }

    // AsyncTask to get DefaultServiceTypeList
    private class GetDefaultServiceTypeList extends AsyncTask<String, Void, List<KmbServiceType>> {
        @Override
        protected List<KmbServiceType> doInBackground(String... routeId) {
            KmbDataManager kmbDataManager = KmbDataManager.getInstance();
            return kmbDataManager.getDefaultServiceTypeList(routeId[0]);
        }

        @Override
        protected void onPostExecute(List<KmbServiceType> result) {
            super.onPostExecute(result);
            mKmbServiceTypeList.clear();
            mKmbServiceTypeList.addAll(result);

            setActionBarSubtitle();

            Log.d(TAG, "mKmbServiceTypeList.size=[" + mKmbServiceTypeList.size() + "]");
            mKmbRouteFragmentPagerAdapter.notifyDataSetChanged();
        }
    }
}
