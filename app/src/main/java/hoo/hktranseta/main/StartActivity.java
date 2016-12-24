package hoo.hktranseta.main;

import android.content.Intent;
import android.os.Bundle;

import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.activity.BaseActivity;
import hoo.hktranseta.main.mtr.MtrActivity;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> activityClass;

        try {
            activityClass = Class.forName(getStringFromDefaultSharePreferences(
                    Constants.Prefs.START_ACTIVITY, MtrActivity.class.getName()));
        } catch(ClassNotFoundException ex) {
            activityClass = MtrActivity.class;
        }

        startActivity(new Intent(this, activityClass));
        finish();
    }
}
