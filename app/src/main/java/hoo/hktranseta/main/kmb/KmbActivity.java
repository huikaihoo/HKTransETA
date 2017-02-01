package hoo.hktranseta.main.kmb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.activity.MainActivity;
import hoo.hktranseta.main.gov.adapter.GovBusRoutesAdapter;
import hoo.hktranseta.main.gov.model.GovBusRoute;
import hoo.hktranseta.main.gov.worker.GovDataManager;

public class KmbActivity extends MainActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "KmbActivity";
    private static final String[] busRouteType = { Constants.BusRouteType.KMB, Constants.BusRouteType.LWB };

    private String mQueryString = "";
    private boolean mSearchViewHasFocus = false;

    private List<GovBusRoute> mGovBusRouteList = new ArrayList<>();

    private GetGovBusRouteListAsyncTask mGetGovBusRouteListAsyncTask;

    private RecyclerView mRecyclerView;
    private GovBusRoutesAdapter mGovBusRoutesAdapter;

    public KmbActivity() {
        super();
        mChildClass = getClass();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_kmb);
        item.setChecked(true);

        // Set up SwipeRefreshLayout
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorKmb);
        }

        // Restore Query String if any
        if (savedInstanceState != null) {
            mQueryString = savedInstanceState.getString(Constants.Extra.QUERY_STRING);
            mSearchViewHasFocus = savedInstanceState.getBoolean(Constants.Extra.SEARCH_VIEW_HAS_FOCUS, false);
        }

        // Set up recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                ((LinearLayoutManager)mRecyclerView.getLayoutManager()).getOrientation()));

        // Get recyclerView adapter data
        mGovBusRoutesAdapter = new GovBusRoutesAdapter(mGovBusRouteList, this);
        mRecyclerView.setAdapter(mGovBusRoutesAdapter);

        if(savedInstanceState != null) {
            resetAsyncTask(new GetGovBusRouteListAsyncTask(savedInstanceState.getParcelable(Constants.Extra.RECYCLER_LAYOUT)));
        }else{
            resetAsyncTask(new GetGovBusRouteListAsyncTask(null));
        }
        AsyncTaskCompat.executeParallel(mGetGovBusRouteListAsyncTask);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean rc = super.onCreateOptionsMenu(menu);

        mSearchMenuItem = menu.findItem(R.id.action_search);
        if (mSearchMenuItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);

            mSearchView.setOnQueryTextListener(this);

            if (!mQueryString.isEmpty() || mSearchViewHasFocus) {
                mSearchMenuItem.expandActionView();
                mSearchView.setQuery(mQueryString, true);
                if (!mSearchViewHasFocus){
                    mSearchView.clearFocus();
                }
            }
//            mQueryString = "";
//            mSearchViewHasFocus = false;
        }

        return rc;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSearchView != null) {
            outState.putString(Constants.Extra.QUERY_STRING, mSearchView.getQuery().toString());
            outState.putBoolean(Constants.Extra.SEARCH_VIEW_HAS_FOCUS, mSearchView.hasFocus());
        }
        if (mRecyclerView != null){
            outState.putParcelable(Constants.Extra.RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        resetAsyncTask(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit>" + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mGovBusRoutesAdapter.getFilter().filter(newText);
        Log.d(TAG, "onQueryTextChange>" + newText);
        return false;
    }

    public void startRouteActivity(String routeNo) {
        Intent intent = new Intent(this, KmbRouteActivity.class);
        intent.putExtra(Constants.Extra.KMB_ROUTE_NO, routeNo);
        startActivity(intent);
    }

    private void resetAsyncTask(@Nullable AsyncTask newAsyncTask) {
        // Cancel all previous AsyncTask
        if (mGetGovBusRouteListAsyncTask != null) {
            mGetGovBusRouteListAsyncTask.cancel(true);
        }

        // Create new AsyncTask
        if (newAsyncTask instanceof GetGovBusRouteListAsyncTask) {
            mGetGovBusRouteListAsyncTask = (GetGovBusRouteListAsyncTask) newAsyncTask;
        }
    }

    // AsyncTask to get GovBusRouteList
    private class GetGovBusRouteListAsyncTask extends AsyncTask<Void, Void, List<GovBusRoute>> {
        private Parcelable layoutManagerState = null;

        public GetGovBusRouteListAsyncTask (@Nullable Parcelable layoutManagerState) {
            this.layoutManagerState = layoutManagerState;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected List<GovBusRoute> doInBackground(Void... params) {
            GovDataManager govDataManager = GovDataManager.getInstance();
            return govDataManager.getGovBusRouteList(busRouteType);
        }

        @Override
        protected void onPostExecute(List<GovBusRoute> result) {
            super.onPostExecute(result);
            mGovBusRouteList.clear();
            mGovBusRouteList.addAll(result);

            Log.d(TAG, "mGovBusRouteList.size=[" + mGovBusRouteList.size() + "]");

            if (mSearchView != null && (!mQueryString.isEmpty() || mSearchViewHasFocus)) {
                mSearchView.setQuery(mQueryString, true);
                if (!mSearchViewHasFocus){
                    mSearchView.clearFocus();
                }
            } else {
                mGovBusRoutesAdapter.getFilter().filter("");
            }

            if (layoutManagerState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
            }

            mGovBusRoutesAdapter.notifyDataSetChanged();

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }
}
