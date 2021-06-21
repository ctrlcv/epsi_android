package kr.co.ecommtech.epsi.ui.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class NfcPageAdapter extends FragmentStateAdapter {
    private NfcReadFragment mNfcReadFragment = null;
    private NfcWriteFragment mNfcWriteFragment = null;

    public NfcPageAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public NfcPageAdapter(@NonNull @NotNull Fragment fragment) {
        super(fragment);
    }

    public NfcPageAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (mNfcReadFragment == null) {
                    mNfcReadFragment = new NfcReadFragment();
                }
                return mNfcReadFragment;

            case 1:
                if (mNfcWriteFragment == null) {
                    mNfcWriteFragment = new NfcWriteFragment();
                }
                return mNfcWriteFragment;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
