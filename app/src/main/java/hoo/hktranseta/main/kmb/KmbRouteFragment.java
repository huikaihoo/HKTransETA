package hoo.hktranseta.main.kmb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedAdapter;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.fragment.BaseFragment;
import hoo.hktranseta.common.worker.SharedPrefsManager;
import hoo.hktranseta.main.kmb.adapter.KmbRouteStopsAdapter;
import hoo.hktranseta.main.kmb.adapter.KmbServiceTypeHeaderAdapter;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;
import hoo.hktranseta.main.kmb.worker.KmbDataManager;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A placeholder fragment containing a simple view.
 */
public class KmbRouteFragment extends BaseFragment {

    private static final String TAG = "KmbRouteFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    // View
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MaterialProgressBar mProgressBar;

    // Data
    private String mRouteNo;
    private int mBoundId;
    private int mSelectedItemId = 0;    // bottomNavigationViewEx selected item
    private int mSelectedServiceTypePosition = 0;
    private long mCountDownFinishTime = 0;
    private boolean mEnableTimer;
    private List<KmbServiceType> mKmbServiceTypeList = new ArrayList<>();

    private CountDownTimer mCountDownTimer;
    private GetServiceTypeListAsyncTask mGetServiceTypeListAsyncTask;
    private GetEtaListAsyncTask mGetEtaListAsyncTask;

    private RecyclerView mRecyclerView;
    private ComposedAdapter mComposedAdapter;

    public KmbRouteFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static KmbRouteFragment newInstance(String routeNo, int boundId) {
        KmbRouteFragment fragment = new KmbRouteFragment();
        Bundle args = new Bundle();
        args.putString(Constants.Extra.KMB_ROUTE_NO, routeNo);
        args.putInt(Constants.Extra.KMB_BOUND_ID, boundId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kmb_route, container, false);
        Log.d(TAG, "onCreateView");

        // Set up SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorKmb);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSelectedItemId == R.id.menu_route) {
                    ((KmbRouteActivity)getActivity()).updateOnRefreshData();
                } else {
                    updateOnRefreshData();
                }
            }
        });

        // Get Fragment based data
        mRouteNo = getArguments().getString(Constants.Extra.KMB_ROUTE_NO);
        mBoundId = getArguments().getInt(Constants.Extra.KMB_BOUND_ID);
        if (savedInstanceState != null) {
            mSelectedItemId = savedInstanceState.getInt(Constants.Extra.KMB_SELECTED_ITEM_ID);
            mSelectedServiceTypePosition = savedInstanceState.getInt(Constants.Extra.KMB_SELECTED_SERVICE_TYPE_POSITION);
            mCountDownFinishTime = savedInstanceState.getLong(Constants.Extra.COUNTDOWN_FINISH_TIME);
        }
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        mEnableTimer = ((mBoundId == 1) && (sharedPrefsManager.getInt(Constants.Prefs.ETA_UPDATE_FREQUENCY, 30) > 0));

        // Set up ProgressBar
        mProgressBar = (MaterialProgressBar) getActivity().findViewById(R.id.progress_bar);

        if (mRouteNo != null && mRouteNo.length() > 0 && mBoundId > 0) {
            // Set up recyclerView
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            // Get recyclerView adapter data
            if (mSelectedItemId == 0)
                mSelectedItemId = R.id.menu_route;
            setProgressBarVisibility();
            updateAdapterData(mSelectedItemId, true, (savedInstanceState == null));

            // Resume Time if savedInstanceState is not null
            if (savedInstanceState != null) {
                startRefreshTimer();
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mCountDownTimer == null && mCountDownFinishTime > 0) {
            startRefreshTimer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putInt(Constants.Extra.KMB_SELECTED_ITEM_ID, mSelectedItemId);
        outState.putInt(Constants.Extra.KMB_SELECTED_SERVICE_TYPE_POSITION, mSelectedServiceTypePosition);
        outState.putLong(Constants.Extra.COUNTDOWN_FINISH_TIME, mCountDownFinishTime);
        resetAsyncTask(null);
        cancelRefreshTimer();
    }

    private void setProgressBarVisibility() {
        if (mSelectedItemId == R.id.menu_route) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void startRefreshTimer() {
        if (mEnableTimer) {
            SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
            int maxTime = sharedPrefsManager.getInt(Constants.Prefs.ETA_UPDATE_FREQUENCY, 30) * 1000;
            long currentTime = System.currentTimeMillis();

            if (mCountDownFinishTime > 0 && mCountDownFinishTime < currentTime) {
                mCountDownFinishTime = 0;
                ((KmbRouteActivity)getActivity()).updateOnRefreshData();
            }
            if (mCountDownFinishTime == 0) {
                mCountDownFinishTime = currentTime + maxTime;
            }

            // Set ProgressBar
            mProgressBar.setMax(maxTime);

            // Set CountDownTimer
            cancelRefreshTimer();
            mCountDownTimer = new CountDownTimer(mCountDownFinishTime - currentTime, 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mProgressBar.setProgress((int)(mCountDownFinishTime - System.currentTimeMillis()));
                }

                @Override
                public void onFinish() {
                    mCountDownFinishTime = 0;
                    if (getActivity() != null && getActivity() instanceof  KmbRouteActivity) {
                        ((KmbRouteActivity)getActivity()).updateOnRefreshData();
                    }
                }
            };
            mCountDownTimer.start();
        }
    }

    private void cancelRefreshTimer() {
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mProgressBar.setProgress(0);
    }

    public int getSelectedServiceTypePosition() {
        return mSelectedServiceTypePosition;
    }

    // Call From Service Type Spinner
    public void setSelectedServiceTypePosition(int selectedServiceTypePosition) {
        Log.d(TAG, "setSelectedServiceTypePosition " + selectedServiceTypePosition + " " + mSelectedServiceTypePosition);
        if (mSelectedItemId == R.id.menu_route && selectedServiceTypePosition != mSelectedServiceTypePosition) {
            if (selectedServiceTypePosition < 0 || selectedServiceTypePosition >= mKmbServiceTypeList.size() ) {
                mSelectedServiceTypePosition = 0;
            } else {
                mSelectedServiceTypePosition = selectedServiceTypePosition;
            }
            updateComposedAdapter(mSelectedItemId, true);
            updateOnRefreshData();
        }
    }

    /**
     * Update ETA list of certain position
     */
    public void updateEtaList(int position) {
        List<KmbRouteStop> kmbRouteStopList = new ArrayList<>();
        kmbRouteStopList.add(mKmbServiceTypeList.get(mSelectedServiceTypePosition).getKmbRouteStopList().get(position));
        resetAsyncTask(new GetEtaListAsyncTask(false));
        AsyncTaskCompat.executeParallel(mGetEtaListAsyncTask, kmbRouteStopList);
    }

    public void updateOnRefreshData() {
        switch (mSelectedItemId) {
            case R.id.menu_route:
                resetAsyncTask(new GetEtaListAsyncTask(true));
                AsyncTaskCompat.executeParallel(mGetEtaListAsyncTask, mKmbServiceTypeList.get(mSelectedServiceTypePosition).getKmbRouteStopList());
                break;
            case R.id.menu_notice:
                break;
        }
    }

    /**
     * Update related data based on Navigation Item selected in bottomNavigationViewEx
     */
    public void updateAdapterData(int itemId, boolean forceUpdate, Boolean updateEta) {
        if (forceUpdate || mSelectedItemId != itemId) {
            //mSwipeRefreshLayout.setRefreshing(true);

            mComposedAdapter = new ComposedAdapter();
            switch (itemId) {
                case R.id.menu_timetable:
                    updateComposedAdapter(itemId, forceUpdate);
                    break;
                case R.id.menu_route:
                    resetAsyncTask(new GetServiceTypeListAsyncTask(updateEta));
                    AsyncTaskCompat.executeParallel(mGetServiceTypeListAsyncTask);
                    break;
                case R.id.menu_interchange:
                    updateComposedAdapter(itemId, forceUpdate);
                    break;
                case R.id.menu_notice:
                    updateComposedAdapter(itemId, forceUpdate);
                    break;
            }
        } else {
            updateComposedAdapter(itemId, forceUpdate);
        }
        mSelectedItemId = itemId;
        setProgressBarVisibility();
    }

    /**
     * Update mComposedAdapter based on Navigation Item selected in bottomNavigationViewEx
     */
    private void updateComposedAdapter(int itemId, boolean forceUpdate) {
        if (forceUpdate || mSelectedItemId != itemId ) {
            mComposedAdapter = new ComposedAdapter();
            switch (itemId) {
                case R.id.menu_timetable:
                    break;
                case R.id.menu_route:
                    mComposedAdapter.addAdapter(new KmbServiceTypeHeaderAdapter(mKmbServiceTypeList, this));
                    mComposedAdapter.addAdapter(new KmbRouteStopsAdapter(mKmbServiceTypeList.get(mSelectedServiceTypePosition), this));
                    break;
                case R.id.menu_interchange:
                    break;
                case R.id.menu_notice:
                    break;
            }
            mRecyclerView.setAdapter(mComposedAdapter);
        }
        mComposedAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void resetAsyncTask(@Nullable AsyncTask newAsyncTask){
        // Cancel all previous AsyncTask
        if (mGetServiceTypeListAsyncTask != null) {
            mGetServiceTypeListAsyncTask.cancel(true);
        }
        if (mGetEtaListAsyncTask != null) {
            mGetEtaListAsyncTask.cancel(true);
        }

        // Create new AsyncTask
        if (newAsyncTask instanceof GetServiceTypeListAsyncTask) {
            mGetServiceTypeListAsyncTask = (GetServiceTypeListAsyncTask) newAsyncTask;
        } else if (newAsyncTask instanceof GetEtaListAsyncTask) {
            mGetEtaListAsyncTask = (GetEtaListAsyncTask) newAsyncTask;
        }
    }

    /**
     * AsyncTask to get ServiceTypeList
     */
    private class GetServiceTypeListAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private boolean updateEta;

        public GetServiceTypeListAsyncTask(boolean updateEta) {
            this.updateEta = updateEta;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            KmbDataManager kmbDataManager = KmbDataManager.getInstance();
            List<KmbServiceType> kmbServiceTypeList = kmbDataManager.getServiceTypeList(mRouteNo, mBoundId);
            mKmbServiceTypeList.clear();
            mKmbServiceTypeList.addAll(kmbServiceTypeList);
            return mKmbServiceTypeList.size() > 0;
        }

        @Override
        protected void onPostExecute(Boolean rc) {
            super.onPostExecute(rc);

            Log.d(TAG, "mKmbServiceTypeList.size=[" + mKmbServiceTypeList.size() + "]");
            if (mBoundId == 1) {
                List<KmbRouteStop> routeStopList = mKmbServiceTypeList.get(0).getKmbRouteStopList();
                Log.d(TAG, "routeStopList.size=[" + routeStopList.size() + "]");

                if (routeStopList.size() > 0) {
                    KmbRouteStop firstStop = routeStopList.get(0);
                    KmbRouteStop lastStop = routeStopList.get(routeStopList.size() - 1);

                    if (getActivity() != null) {
                        ((KmbRouteActivity)getActivity()).setCircular(firstStop.getStopId().equals(lastStop.getStopId()));
                    }
                }
            }

            updateComposedAdapter(mSelectedItemId, true);
            if (updateEta || mCountDownFinishTime < System.currentTimeMillis()) {
                updateOnRefreshData();
            }
        }
    }

    /**
     * AsyncTask to get EtaList for RouteStopList
     */
    private class GetEtaListAsyncTask extends AsyncTask<List<KmbRouteStop>, Void, Boolean> {

        private boolean resetTimer;

        public GetEtaListAsyncTask (boolean resetTimer){
            this.resetTimer = resetTimer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "GetEtaListAsyncTask");
            mSwipeRefreshLayout.setRefreshing(true);
            if (resetTimer) {
                cancelRefreshTimer();
            }
        }

        @Override
        protected Boolean doInBackground(List<KmbRouteStop>... params) {
            for (KmbRouteStop kmbRouteStop : params[0]) {
                kmbRouteStop.etaStatus = Constants.Eta.LOADING;
            }

            try {
                mComposedAdapter.notifyDataSetChanged();
            } catch (Exception e){}     // Prevent Error when RecyclerView is not finish initialization

            KmbDataManager kmbDataManager = KmbDataManager.getInstance();
            return kmbDataManager.getEtaList(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean rc) {
            super.onPostExecute(rc);

            mComposedAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            if (resetTimer) {
                mCountDownFinishTime = 0;
                startRefreshTimer();
            }
        }
    }
}

