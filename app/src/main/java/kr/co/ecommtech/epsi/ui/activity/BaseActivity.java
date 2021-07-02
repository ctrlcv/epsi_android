package kr.co.ecommtech.epsi.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity  extends AppCompatActivity {
    private final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy()");
    }

    public void finishAndRemoveTaskCompat() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            // crbug.com/395772 : Fallback for Activity.finishAndRemoveTask() failing.
            new FinishAndRemoveTaskWithRetry(this).run();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    private static class FinishAndRemoveTaskWithRetry implements Runnable {
        private static final long RETRY_DELAY_MS = 500;
        private static final long MAX_TRY_COUNT = 3;
        private final BaseActivity mActivity;
        private int mTryCount;

        FinishAndRemoveTaskWithRetry(BaseActivity activity) {
            mActivity = activity;
        }

        @Override
        public void run() {
            mActivity.finishAndRemoveTaskCompat();
            mTryCount++;
            if (!mActivity.isFinishing()) {
                if (mTryCount < MAX_TRY_COUNT) {
//                    ThreadUtils.postOnUiThreadDelayed(this, RETRY_DELAY_MS);
                    Handler h = new Handler(Looper.getMainLooper());
                    h.postDelayed(this, RETRY_DELAY_MS);
                } else {
                    mActivity.finish();
                }
            }
        }
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
