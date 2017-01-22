package hoo.hktranseta.main.kmb.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hoo.hktranseta.R;
import hoo.hktranseta.common.BaseApplication;
import hoo.hktranseta.main.kmb.KmbRouteFragment;
import hoo.hktranseta.main.kmb.model.db.KmbServiceType;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class KmbRouteFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "KmbRouteFPAdapter";
    private List<KmbServiceType> mKmbServiceTypeList;
    private Set<KmbRouteFragment> mKmbRouteFragmentSet = new HashSet<>();

    public KmbRouteFragmentPagerAdapter(FragmentManager fm, List<KmbServiceType> kmbServiceTypeList) {
        super(fm);
        mKmbServiceTypeList = kmbServiceTypeList;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a KmbRouteFragment (defined as a static inner class below).
        return KmbRouteFragment.newInstance(mKmbServiceTypeList.get(position).getRouteNo(), mKmbServiceTypeList.get(position).getBoundId());
    }

    @Override
    public int getCount() {
        return mKmbServiceTypeList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return BaseApplication.getContext().getResources().getString(R.string.to)
                + mKmbServiceTypeList.get(position).getLocationTo();
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    /**
     * Here we can finally safely save a reference to the created
     * Fragment, no matter where it came from (either getItem() or
     * FragmentManger). Simply save the returned Fragment from
     * super.instantiateItem() into an appropriate reference depending
     * on the ViewPager position.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        KmbRouteFragment createdFragment = (KmbRouteFragment) super.instantiateItem(container, position);
        mKmbRouteFragmentSet.add(createdFragment);
        return createdFragment;
    }

    public void updateAdapterData(int itemId, boolean forceUpdate, boolean updateEta){
        for (KmbRouteFragment kmbRouteFragment: mKmbRouteFragmentSet) {
            kmbRouteFragment.updateAdapterData(itemId, forceUpdate, updateEta);
        }
    }

    public void updateOnRefreshData() {
        for (KmbRouteFragment kmbRouteFragment: mKmbRouteFragmentSet) {
            kmbRouteFragment.updateOnRefreshData();
        }
    }
}
