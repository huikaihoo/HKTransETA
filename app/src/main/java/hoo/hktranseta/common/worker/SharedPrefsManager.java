package hoo.hktranseta.common.worker;

import android.content.SharedPreferences;

import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.common.Constants;

public class SharedPrefsManager {

    private static SharedPrefsManager mInstance = null;

    private SharedPreferences mSharedPreferences;

    private SharedPrefsManager() {
        mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(BaseApplication.getContext());
    }

    public static SharedPrefsManager getInstance() {
        if (mInstance == null){
            mInstance = new SharedPrefsManager();
        }
        return mInstance;
    }

    // Public Methods (SharedPreferences)
    public int getAppMode(){
        return Constants.AppMode.getAppMode(getString(Constants.Prefs.PARAMETERS, ""));
    }

    public int getInt(String key, int defaultValue){
        return Integer.valueOf(getString(key, String.valueOf(defaultValue)));
    }

    public void setInt(String key, int value){
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public String getString(String key, String defaultValue){
        return mSharedPreferences.getString(key, defaultValue);
    }

    public void setString(String key, String value){
        mSharedPreferences.edit().putString(key, value).apply();
    }
}
