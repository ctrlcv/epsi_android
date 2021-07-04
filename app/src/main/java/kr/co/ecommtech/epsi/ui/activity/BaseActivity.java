package kr.co.ecommtech.epsi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity  extends AppCompatActivity implements LocationListener {
    private final String TAG = "BaseActivity";

    private LocationManager mLocationManager;
    private Location mLocation = null;
    private OnGpsLocGetListener mLocGetListener;
    private float mMinAccuracy = 9999;
    private Timer mGpsTimer;

    public interface OnGpsLocGetListener {
        void onGpsLocGet(Location location);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager == null) {
            Log.e(TAG, "onCreate() can't create LocationManager!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (mMinAccuracy > location.getAccuracy()) {
            mLocation = location;
            mMinAccuracy = location.getAccuracy();
            mLocGetListener.onGpsLocGet(mLocation);
        }
    }

    @SuppressLint("MissingPermission")
    public void findMyLocation(OnGpsLocGetListener locListener) {
        if (mLocationManager == null) {
            Log.e(TAG, "findMyLocation() can't create LocationManager!");
            return;
        }

        mLocationManager.removeUpdates(this);
        mMinAccuracy = 9999;
        mLocGetListener = locListener;

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100, 1, this);

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.e(TAG, "locationProviderClient location is null");
                            return;
                        }

                        mMinAccuracy = location.getAccuracy();
                        mLocation = location;

                        Log.d(TAG, "get Location Accuracy:" + mMinAccuracy + ", lat:" + mLocation.getLatitude() + ", lon:" + mLocation.getLongitude());
                        mLocGetListener.onGpsLocGet(mLocation);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {}
                });

        if (mGpsTimer != null) {
            mGpsTimer.cancel();
            mGpsTimer = null;
        }

        mGpsTimer = new Timer();
        mGpsTimer.schedule(new CustomTimer(), 5000);
    }

    public void stopGpsSearch() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

        if (mGpsTimer != null) {
            mGpsTimer.cancel();
            mGpsTimer = null;
        }

        mMinAccuracy = 9999;
        mLocGetListener = null;
    }

    private class CustomTimer extends TimerTask {
        @Override
        public void run() {
            stopGpsSearch();
        }
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
