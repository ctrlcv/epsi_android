package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;
import kr.co.ecommtech.epsi.ui.activity.InfoActivity;
import kr.co.ecommtech.epsi.ui.activity.MapActivity;

public class HomeFragment extends Fragment implements DefaultMainActivity.OnBackPressedListener {
    private static String TAG = "HomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.readinfo_image, R.id.readinfo_btn, R.id.viewmap_image, R.id.viewmap_btn})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readinfo_image:
            case R.id.readinfo_btn:
                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHodler, new NfcFragment());
                    fragmentTransaction.commit();
                }
                break;

            case R.id.viewmap_image:
            case R.id.viewmap_btn:
                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentHodler, new MapFragment());
                    fragmentTransaction.commit();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onBack() {
        if (getActivity() != null) {
            ((DefaultMainActivity) getActivity()).finish();
        }
    }
}