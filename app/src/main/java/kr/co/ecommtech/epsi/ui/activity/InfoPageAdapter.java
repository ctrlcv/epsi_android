package kr.co.ecommtech.epsi.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import kr.co.ecommtech.epsi.ui.fragment.CrossSectionFragment;
import kr.co.ecommtech.epsi.ui.fragment.FloorPlanFragment;
import kr.co.ecommtech.epsi.ui.fragment.InfoFragment;

public class InfoPageAdapter extends FragmentStateAdapter {
    private InfoFragment mInfoFragment = null;
    private FloorPlanFragment mFloorPlanFragment = null;
    private CrossSectionFragment mCrossSectionFragment = null;

    public InfoPageAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 :
                if (mInfoFragment == null) {
                    mInfoFragment = new InfoFragment();
                }
                return mInfoFragment;

            case 1 :
                if (mFloorPlanFragment == null) {
                    mFloorPlanFragment = new FloorPlanFragment();
                }
                return mFloorPlanFragment;

            case 2 :
                if (mCrossSectionFragment == null) {
                    mCrossSectionFragment = new CrossSectionFragment();
                }
                return mCrossSectionFragment;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
