package hoo.hktranseta.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import hoo.hktranseta.R;
import hoo.hktranseta.common.Constants;
import hoo.hktranseta.common.view.FabOnVisibilityChangedListener;
import hoo.hktranseta.common.worker.SharedPrefsManager;
import hoo.hktranseta.main.ferry.FerryActivity;
import hoo.hktranseta.main.followed.FollowedActivity;
import hoo.hktranseta.main.kmb.KmbActivity;
import hoo.hktranseta.main.mtr.MtrActivity;
import hoo.hktranseta.main.nwfb.NwfbActivity;
import hoo.hktranseta.main.tram.TramActivity;
import hoo.hktranseta.settings.SettingsActivity;
import hoo.hktranseta.settings.fragment.GeneralPreferenceFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected Class<?> mChildClass;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected AppBarLayout mAppBarLayout;
    protected FloatingActionButton mFab;
    protected MenuItem mSearchMenuItem;
    protected SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mAppBarLayout = (AppBarLayout)findViewById(R.id.appbar);

        // Set up Floating Action Button
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSearchMenuItem != null && mSearchView != null) {
                    //Snackbar.make(view, "Base Code", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    mAppBarLayout.setExpanded(true, true);
                    mSearchMenuItem.expandActionView();
                    mSearchView.requestFocus();
                }
            }
        });

        // Set up Navigation Drawer
        setupNavigationDrawer();

        // Remember the current Activity
        if (mChildClass != null) {
            SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
            sharedPrefsManager.setString(Constants.Prefs.START_ACTIVITY, mChildClass.getName());
        }
    }

    protected void setupNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                Window window = getWindow();
//                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                Window window = getWindow();
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);

        // Set up SearchMenuItem
        mSearchMenuItem = menu.findItem(R.id.action_search);

        if (mSearchMenuItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);

            mSearchMenuItem.setVisible(false);
            MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    if (mFab != null) {
                        mFab.hide(new FabOnVisibilityChangedListener());
                    }
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    if (mFab != null) {
                        mFab.show();
                    }
                    return true;
                }
            });
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_followed:
                intent = new Intent(this, FollowedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_kmb:
                intent = new Intent(this, KmbActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_nwfb:
                intent = new Intent(this, NwfbActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_tram:
                intent = new Intent(this, TramActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_ferry:
                intent = new Intent(this, FerryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_mtr:
                intent = new Intent(this, MtrActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
                if (sharedPrefsManager.getAppMode() != Constants.AppMode.DEBUG) {
                    intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, GeneralPreferenceFragment.class.getName());
                    intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                }
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
