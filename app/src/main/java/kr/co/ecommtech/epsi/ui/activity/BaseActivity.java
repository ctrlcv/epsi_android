package kr.co.ecommtech.epsi.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity  extends AppCompatActivity {
    private final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    public final void showProgressDialog(final boolean show) {
        try {
//            if (findViewById(R.id.progressLayout) != null) {
//                runOnUiThread(() -> findViewById(R.id.progressLayout).setVisibility(show ? View.VISIBLE : View.GONE));
//            }
        } catch (Exception e) {
            Log.e(TAG, "Exception on showProgressDialog()" + e.getMessage());
        }
    }

    public final void showProgressDialog(final boolean show, final View view) {
        try {
            if (view != null) {
                runOnUiThread(() -> view.setVisibility(show ? View.VISIBLE : View.GONE));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception on showProgressDialog() 2" + e.getMessage());
        }
    }
}
