package hoo.hktranseta.common.view;


import android.support.design.widget.FloatingActionButton;
import android.view.View;

/**
 * Source: http://stackoverflow.com/questions/41142711/25-1-0-android-support-lib-is-breaking-fab-behavior
 */
public class FabOnVisibilityChangedListener extends FloatingActionButton.OnVisibilityChangedListener {
    @Override
    public void onHidden(FloatingActionButton fab) {
        super.onHidden(fab);
        fab.setVisibility(View.INVISIBLE);
    }
}
