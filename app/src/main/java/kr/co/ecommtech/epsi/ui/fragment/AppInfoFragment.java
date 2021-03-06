package kr.co.ecommtech.epsi.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;

public class AppInfoFragment extends Fragment implements DefaultMainActivity.OnBackPressedListener {
    private static String TAG = "AppInfoFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appinfo, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(false);
            ((DefaultMainActivity)getActivity()).setOnBackPressedListener(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("앱정보");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(true);
            ((DefaultMainActivity)getActivity()).setOnBackPressedListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBack() {
        if (getActivity() != null) {
            Log.d(TAG, "onBack()");
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        ((DefaultMainActivity)context).setOnBackPressedListener(this);
    }
}
