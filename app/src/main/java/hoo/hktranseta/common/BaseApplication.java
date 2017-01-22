package hoo.hktranseta.common;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import hoo.hktranseta.common.worker.GreenDaoManager;
import hoo.hktranseta.common.worker.SharedPrefsManager;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initGreenDaoManager();
        initSharedPreferences();
        initStetho();
    }

    public static Context getContext() {
        return mInstance;
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    // Initialize
    private void initStetho() {
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        if (sharedPrefsManager.getAppMode() == Constants.AppMode.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }

    private void initGreenDaoManager() {
        GreenDaoManager.getInstance();
    }

    private void initSharedPreferences() {
        SharedPrefsManager.getInstance();
    }
}
