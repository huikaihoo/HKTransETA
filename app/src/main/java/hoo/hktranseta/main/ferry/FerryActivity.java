package hoo.hktranseta.main.ferry;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import hoo.hktranseta.R;
import hoo.hktranseta.common.activity.MainActivity;

public class FerryActivity extends MainActivity {

    public FerryActivity() {
        super();
        mChildClass = getClass();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        MenuItem item = navigationView.getMenu().findItem(R.id.nav_ferry);
        item.setChecked(true);
    }
}
