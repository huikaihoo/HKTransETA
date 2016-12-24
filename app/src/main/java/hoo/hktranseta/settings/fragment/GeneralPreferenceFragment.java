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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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
        bindPreferenceSummaryToValue(findPreference(Constants.Prefs.ETA_UPDATE_INTERVAL));
        bindPreferenceSummaryToValue(findPreference(Constants.Prefs.PARAMETERS));

        assertTrue(getActivity() instanceof Constants.AppPreferences);
        Constants.AppPreferences parentActivity = (Constants.AppPreferences) getActivity();

        // Testing PreferenceCategory
        mTestingPreferenceCategory = (PreferenceCategory) findPreference(Constants.Prefs.TESTING);
        assertNotNull(mTestingPreferenceCategory);

        if (parentActivity.getAppMode() == Constants.AppMode.RELEASE){
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