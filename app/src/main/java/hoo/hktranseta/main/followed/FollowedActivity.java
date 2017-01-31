package hoo.hktranseta.main.followed;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import hoo.hktranseta.R;
import hoo.hktranseta.common.activity.MainTabbedActivity;
import hoo.hktranseta.settings.SettingsActivity;
import hoo.hktranseta.settings.fragment.GeneralPreferenceFragment;

public class FollowedActivity extends MainTabbedActivity {

    public FollowedActivity() {
        super();
        mChildClass = getClass();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_followed);
        item.setChecked(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_followed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_edit:
                return true;
            case R.id.menu_add_shortcut:
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, GeneralPreferenceFragment.class.getName());
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
