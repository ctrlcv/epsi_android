package kr.co.ecommtech.epsi.ui.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.ecommtech.epsi.ui.data.RestError;
import kr.co.ecommtech.epsi.ui.network.HttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class Utils {
    private final static String TAG = "Utils";

    public static void showToast(Context context, String message) {
        ((Activity)context).runOnUiThread(() -> {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0,0);
            toast.show();
        });
    }

    public static boolean isValidEMail(String emailAddress) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static RestError parseError(Response<?> response) {
        Converter<ResponseBody, RestError> converter =
                HttpClient.getRetrofit()
                        .responseBodyConverter(RestError.class, new Annotation[0]);

        RestError error;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new RestError(false, "알 수 없는 오류가 발생하였습니다.\n관리자에게 문의하시기 바랍니다.");
        }

        return error;
    }

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int JArraySetToInt(JSONArray jArray, int beforevalue) throws JSONException {
        int result;

        Log.d(TAG, "JArraySetToInt:" + jArray.toString() + ", beforevalue:" + Integer.toUnsignedString(beforevalue));

        byte[] bytes = ByteBuffer.allocate(4).putInt(beforevalue).array();
        Log.d(TAG, "JArraySetToInt bytes:" + Arrays.toString(bytes) + ", size:" + bytes.length);

        for (int i = 0 ; i < jArray.length(); i++) {
            JSONObject ArrayObj = jArray.getJSONObject(i);
            int index = ArrayObj.getInt("index");
            int value = ArrayObj.getInt("char");

            Log.d(TAG, "JArraySetToInt - index:" + index + ", value:" + value);

            bytes[index] = (byte)value;
        }
        Log.d(TAG, "JArraySetToInt bytes:" + Arrays.toString(bytes));
        result = ((((int)bytes[0] & 0xff) << 24) |
                (((int)bytes[1] & 0xff) << 16) |
                (((int)bytes[2] & 0xff) << 8) |
                (((int)bytes[3] & 0xff)));

        Log.d(TAG, "JArraySetToInt result:" + Integer.toUnsignedString(result));
        return result;
    }

    public static List<Integer> JArraySetToIntList(JSONArray jArray, List<Integer> beforevalue) throws JSONException {
        Log.d(TAG, "JArraySetToIntList:" + jArray.toString() + ", beforevalue:" + beforevalue);

        for (int i = 0 ; i < jArray.length(); i++) {
            JSONObject ArrayObj = jArray.getJSONObject(i);
            int index = ArrayObj.getInt("index");
            int value = ArrayObj.getInt("char");

            Log.d(TAG, "JArraySetToIntList - index:" + index + ", value:" + value);
            beforevalue.set(index, value);
        }

        Log.d(TAG, "JArraySetToIntList result:" + beforevalue);
        return beforevalue;
    }

    public static int getDialogWidth(Context context) {
        // 전체 화면 사이즈에 좌우 margin 27dp
        return (int) (getDeviceWidth(context) - (dip2Pixel(context, 27) * 2));
    }

    /**
     * 단말기 높이 넓이<br/>
     * pixel 단위<br/>
     * FIXME landscape 단말의 경우 어떤 값이 나오는지 테스트 안됨.
     *
     * @param context
     * @return
     */
    public static int getDeviceWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * DP 단위를 Pixel 단위로 변환
     *
     * @param context
     * @param dpi
     * @return
     */
    public static float dip2Pixel(Context context, float dpi) {
        float density = context.getResources().getDisplayMetrics().densityDpi;
        float pixel = dpi * (density / (float) DisplayMetrics.DENSITY_DEFAULT);
        return pixel;
    }

    /**
     * 2019-09-19 hj
     * Activity 구동 중 여부 판단
     * @param context
     * @param activityName 대상 activity
     * @return true:지정한 activity가 task의 top 혹은 base에 존재함
     *         false:지정한 activity가 task의 top 혹은 base에 존재하지 않음
     */
    public static boolean getServiceTaskName(Context context, String activityName) {

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> info = am.getRunningTasks(30);
        Log.i(TAG, "myTaskName() = " + context.getPackageName());

        for (int i = 0; i < info.size(); i++) {
            Log.i(TAG, "[" + i + "] getServiceTaskName:" + info.get(i).topActivity.getPackageName() + " >> " + info.get(i).topActivity.getClassName());
        }

        for (Iterator iterator = info.iterator(); iterator.hasNext();) {
            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) iterator.next();
            Log.i(TAG, "getServiceTaskName().topActivity = "+runningTaskInfo.topActivity.getClassName());
            Log.i(TAG, "getServiceTaskName().baseActivity = "+runningTaskInfo.baseActivity.getClassName());
            Log.i(TAG, "getServiceTaskName().numRunning = "+runningTaskInfo.numRunning);

            if (runningTaskInfo.topActivity.getClassName().equals(activityName)) {
                Log.i(TAG, "getServiceTaskName() = true");
                return true;
            }

            if (runningTaskInfo.baseActivity.getClassName().equals(activityName)) {
                Log.i(TAG, "getServiceTaskName() = true");
                return true;
            }
        }
        return false;
    }

    public static boolean IsExcuteThisApp(Context context) {

        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> info = am.getRunningTasks(30);
        Log.i(TAG, "myTaskName() = " + context.getPackageName());

        for (int i = 0; i < info.size(); i++) {
            Log.i(TAG, "[" + i + "] getServiceTaskName:" + info.get(i).topActivity.getPackageName());
            if (context.getPackageName().equals(info.get(i).topActivity.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    public static String getVersion(Context context) {
        String version = null;

        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception on getVersion()");
            e.printStackTrace();
        }

        return version;
    }

    public static boolean checkPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        int hasPerm = pm.checkPermission(permission, context.getPackageName());
        return hasPerm == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasFeature(Context context, String feature) {
        boolean ret = false;
        try {
            ret = context.getPackageManager().hasSystemFeature(
                    feature);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
