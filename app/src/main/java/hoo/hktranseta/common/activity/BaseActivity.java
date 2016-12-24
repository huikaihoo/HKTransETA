package hoo.hktranseta.common.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import hoo.hktranseta.BuildConfig;
import hoo.hktranseta.common.Constants;

public class BaseActivity extends AppCompatActivity
        implements Constants.AppPreferences {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getAppMode(){
        if (BuildConfig.DEBUG) {
            //return Constants.AppMode.DEBUG;
        }
        return Constants.AppMode.getAppMode(getStringFromDefaultSharePreferences(Constants.Prefs.PARAMETERS, ""));
    }

    public int getIntFromDefaultSharePreferences(String key, int defaultValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setIntFromDefaultSharePreferences(String key, int value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public String getStringFromDefaultSharePreferences(String key, String defaultValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setStringFromDefaultSharePreferences(String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(key, value).apply();
    }
}
