package hoo.hktranseta.common.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.wingsofts.byeburgernavigationview.ByeBurgerBottomBehavior;
import com.wingsofts.byeburgernavigationview.TranslateAnimateHelper;

public class BottomNavigationViewBehavior extends ByeBurgerBottomBehavior {

    private static final int dyIgnore = 1;

    public BottomNavigationViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target,
                                  int dx, int dy, int[] consumed) {
        if (Math.abs(dy) > dyIgnore) {
            if (dy < 0) {
                if (mAnimateHelper.getState() == TranslateAnimateHelper.STATE_HIDE) {
                    mAnimateHelper.show();
                }
            } else if (dy > 0) {
                if (mAnimateHelper.getState() == TranslateAnimateHelper.STATE_SHOW) {
                    mAnimateHelper.hide();
                }
            }
        }
    }
}
