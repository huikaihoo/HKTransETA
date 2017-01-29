package hoo.hktranseta.main.kmb;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.activity.MapsActivity;
import hoo.hktranseta.main.StreetViewActivity;
import hoo.hktranseta.main.kmb.adapter.KmbServiceTypeSpinnerAdapter;
import hoo.hktranseta.main.kmb.model.db.KmbRouteStop;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;
import hoo.hktranseta.main.kmb.worker.KmbDataManager;

public class KmbMapsActivity extends MapsActivity {

    private static final String TAG = "KmbMapsActivity";

    private String mRouteNo;
    private int mSelectedServiceTypePosition = 0;
    private List<KmbServiceType> mKmbServiceTypeList = new ArrayList<>();
    private List<Marker> mMarkerList = new ArrayList<>();

    private GetServiceTypeListAsyncTask mGetServiceTypeListAsyncTask;

    private Spinner mSpinner;
    private KmbServiceTypeSpinnerAdapter mKmbServiceTypeSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Get Activity based data
        mRouteNo = getIntent().getStringExtra(Constants.Extra.KMB_ROUTE_NO);
        mSelectedServiceTypePosition = getIntent().getIntExtra(Constants.Extra.KMB_SELECTED_SERVICE_TYPE_POSITION, 0);
        if (savedInstanceState != null) {
            mSelectedServiceTypePosition = savedInstanceState.getInt(Constants.Extra.KMB_SELECTED_SERVICE_TYPE_POSITION);
        }

        // Setup ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRouteNo);
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_maps, menu);

        MenuItem item = menu.findItem(R.id.menu_spinner);
        mSpinner = (Spinner) MenuItemCompat.getActionView(item);

        //resetAsyncTask(new GetServiceTypeListAsyncTask(spinner));
        mKmbServiceTypeSpinnerAdapter = new KmbServiceTypeSpinnerAdapter(this, R.layout.item_kmb_service_type_small, mKmbServiceTypeList);
        mSpinner.setAdapter(mKmbServiceTypeSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int beforePosition = mSelectedServiceTypePosition;
                Log.d(TAG, "Before position=" + beforePosition);
                Log.d(TAG, "New position=" + position);
                if (beforePosition != position) {
                    mSelectedServiceTypePosition = position;
                    setStopsOnMaps();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Extra.KMB_SELECTED_SERVICE_TYPE_POSITION, mSelectedServiceTypePosition);
        resetAsyncTask(null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        // Set up OnInfoWindowClickListener
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                TextView textView = (TextView) mSpinner.getSelectedView().findViewById(R.id.title);
                String subtitle = mRouteNo + ((textView != null) ? " - " + textView.getText() : "");

                Intent intent = new Intent(KmbMapsActivity.this, StreetViewActivity.class);
                intent.putExtra(Constants.Extra.ACTIONBAR_TITLE, marker.getTitle());
                intent.putExtra(Constants.Extra.ACTIONBAR_SUBTITLE, subtitle);
                intent.putExtra(Constants.Extra.LATITUDE, marker.getPosition().latitude);
                intent.putExtra(Constants.Extra.LONGITUDE, marker.getPosition().longitude);
                startActivity(intent);
            }
        });

        resetAsyncTask(new GetServiceTypeListAsyncTask());
        AsyncTaskCompat.executeParallel(mGetServiceTypeListAsyncTask);
    }

    private void setStopsOnMaps() {
        if (mGoogleMap != null) {
            // Clear all markers on maps
            mGoogleMap.clear();
            mMarkerList.clear();

            // Set stops on map
            LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();
            List<KmbRouteStop> kmbRouteStopList = mKmbServiceTypeList.get(mSelectedServiceTypePosition).getKmbRouteStopList();

            for (int i=0; i<kmbRouteStopList.size(); i++) {
                KmbRouteStop kmbRouteStop = kmbRouteStopList.get(i);
                float markerColor = BitmapDescriptorFactory.HUE_RED;
                if (i == 0) {
                    markerColor = BitmapDescriptorFactory.HUE_GREEN;
                } else if (i + 1 == kmbRouteStopList.size()) {
                    markerColor = BitmapDescriptorFactory.HUE_AZURE;
                }

                mMarkerList.add(mGoogleMap.addMarker(kmbRouteStop.getMarkerOptions((i + 1) + ". ", markerColor)));
                latLngBoundsBuilder.include(mMarkerList.get(i).getPosition());

                Log.d(TAG, kmbRouteStop.getStopLat() + "," + kmbRouteStop.getStopLong());
            }

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), 50));

            // Check for location permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Zoom to nearest stop
            }
        }
    }

    private void resetAsyncTask(@Nullable AsyncTask newAsyncTask) {
        // Cancel all previous AsyncTask
        if (mGetServiceTypeListAsyncTask != null) {
            mGetServiceTypeListAsyncTask.cancel(true);
        }

        // Create new AsyncTask
        if (newAsyncTask instanceof GetServiceTypeListAsyncTask) {
            mGetServiceTypeListAsyncTask = (GetServiceTypeListAsyncTask) newAsyncTask;
        }
    }

    // AsyncTask to get GetServiceTypeList
    private class GetServiceTypeListAsyncTask extends AsyncTask<Void, Void, List<KmbServiceType>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected List<KmbServiceType> doInBackground(Void... params) {
            KmbDataManager kmbDataManager = KmbDataManager.getInstance();
            return kmbDataManager.getServiceTypeList(mRouteNo);
        }

        @Override
        protected void onPostExecute(List<KmbServiceType> result) {
            super.onPostExecute(result);
            mKmbServiceTypeList.clear();
            mKmbServiceTypeList.addAll(result);
            mKmbServiceTypeSpinnerAdapter.notifyDataSetChanged();

            Log.d(TAG, "mKmbServiceTypeList.size=[" + mKmbServiceTypeList.size() + "]");

            setStopsOnMaps();

            if (mSpinner != null) {
                mSpinner.setEnabled(mKmbServiceTypeList.size() > 1);
                mSpinner.setSelection(mSelectedServiceTypePosition);
            }

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }
}
