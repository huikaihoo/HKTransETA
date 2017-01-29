package hoo.hktranseta.settings.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import hoo.hktranseta.BuildConfig;
import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.fragment.BasePreferenceFragment;
import hoo.hktranseta.common.worker.SharedPrefsManager;
import hoo.hktranseta.main.kmb.worker.KmbDataManager;

import static junit.framework.Assert.assertNotNull;

/**
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends BasePreferenceFragment {

    PreferenceCategory mTestingPreferenceCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(Constants.Prefs.DATABASE_UPDATE_FREQUENCY));
        bindPreferenceSummaryToValue(findPreference(Constants.Prefs.ETA_UPDATE_FREQUENCY));
        bindPreferenceSummaryToValue(findPreference(Constants.Prefs.PARAMETERS));

        // Clear Route Data Preference
        Preference clearRouteDataPreference = findPreference(Constants.Prefs.CLEAR_ROUTE_DATA);

        clearRouteDataPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: Add conform dialog
                KmbDataManager kmbDataManager = KmbDataManager.getInstance();
                kmbDataManager.clearAllDbData();
                return true;
            }
        });

        // Testing PreferenceCategory
        mTestingPreferenceCategory = (PreferenceCategory) findPreference(Constants.Prefs.TESTING);
        assertNotNull(mTestingPreferenceCategory);

        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        if (sharedPrefsManager.getAppMode() == Constants.AppMode.RELEASE){
            getPreferenceScreen().removePreference(mTestingPreferenceCategory);
        }

        // App Version Preference
        Preference appVersionPreference = findPreference(Constants.Prefs.APP_VERSION);

        appVersionPreference.setSummary(BuildConfig.VERSION_NAME);
        appVersionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            int mCounter = 0;

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mCounter == 6) {
                    getPreferenceScreen().addPreference(mTestingPreferenceCategory);
                } else {
                    mCounter++;
                }
                return true;
            }
        });
    }
}