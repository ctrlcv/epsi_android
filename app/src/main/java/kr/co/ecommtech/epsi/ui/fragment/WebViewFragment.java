package kr.co.ecommtech.epsi.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.naver.maps.map.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.co.ecommtech.epsi.R;
import kr.co.ecommtech.epsi.ui.activity.DefaultMainActivity;

public class WebViewFragment extends Fragment implements DefaultMainActivity.OnBackPressedListener {
    private static final String TAG = "WebViewFragment";

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.web_view)
    WebView mWebView;

    private WebSettings mWebSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity() != null) {
            ((DefaultMainActivity)getActivity()).setTitle("사용자 매뉴얼");
            ((DefaultMainActivity)getActivity()).setHomeBtnVisible(true);
        }

        mWebView.loadUrl("http://139.150.83.28/manual/Manual.html");
        mWebView.setWebViewClient(new WebViewClient());

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setDisplayZoomControls(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebSettings.setDefaultFixedFontSize(14);
        mWebView.zoomIn();

        return rootView;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        if (getActivity() != null) {
            ((DefaultMainActivity) getActivity()).setTitle("");
            ((DefaultMainActivity) getActivity()).setHomeBtnVisible(false);
        }
    }

    @Override
    public void onBack() {
        if (getActivity() != null) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.replace(R.id.fragmentHodler, new HomeFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((DefaultMainActivity)context).setOnBackPressedListener(this);
    }
}
