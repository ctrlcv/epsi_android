package kr.co.ecommtech.epsi.ui.utils;

import android.text.TextUtils;
import android.util.Log;

import kr.co.ecommtech.epsi.R;

public class CommUtils {
    private final static String TAG = "CommUtils";

    public static int getImageResourceId(String setPosition, String pipeType, String distanceDirection) {
        Log.d(TAG, "getImageResourceId() setPosition:'" + setPosition + "', pipeType:'" + pipeType + "', distanceDirection:'" + distanceDirection + "'");

        if (!TextUtils.isEmpty(setPosition) && setPosition.equals("경계석")) {
            if (!TextUtils.isEmpty(pipeType)) {
                switch (pipeType) {
                    case "직진형":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.d_type_0_center;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.d_type_0_center;
                        } else {
                            return R.drawable.d_type_0_center;
                        }

                    case "T분기형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_0_right;
                        } else {
                            return R.drawable.t_type_0_center;
                        }

                    case "T분기형(90°)":
                    default:
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_90_right;
                        } else {
                            return R.drawable.t_type_90_center;
                        }

                    case "T분기형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_180_right;
                        } else {
                            return R.drawable.t_type_180_center;
                        }

                    case "T분기형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.t_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.t_type_270_right;
                        } else {
                            return R.drawable.t_type_270_center;
                        }

                    case "엘보형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_0_right;
                        } else {
                            return R.drawable.l_type_0_center;
                        }

                    case "엘보형(90°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_90_right;
                        } else {
                            return R.drawable.l_type_90_center;
                        }

                    case "엘보형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_180_right;
                        } else {
                            return R.drawable.l_type_180_center;
                        }

                    case "엘보형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.l_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.l_type_270_right;
                        } else {
                            return R.drawable.l_type_270_center;
                        }

                    case "관말형(0°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_0_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_0_right;
                        } else {
                            return R.drawable.e_type_0_center;
                        }

                    case "관말형(90°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_90_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_90_right;
                        } else {
                            return R.drawable.e_type_90_center;
                        }

                    case "관말형(180°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_180_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_180_right;
                        } else {
                            return R.drawable.e_type_180_center;
                        }

                    case "관말형(270°)":
                        if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                            return R.drawable.e_type_270_left;
                        } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                            return R.drawable.e_type_270_right;
                        } else {
                            return R.drawable.e_type_270_center;
                        }
                }
            } else {
                if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("LEFT")) {
                    return R.drawable.l_type_0_left;
                } else if (!TextUtils.isEmpty(distanceDirection) && distanceDirection.equals("RIGHT")) {
                    return R.drawable.l_type_0_right;
                } else {
                    return R.drawable.l_type_0_center;
                }
            }
        } else {
            if (!TextUtils.isEmpty(pipeType)) {
                switch (pipeType) {
                    case "직진형":
                        return R.drawable.d_type_0;

                    case "T분기형(0°)":
                    default:
                        return R.drawable.t_type_0;

                    case "T분기형(90°)":
                        return R.drawable.t_type_90;

                    case "T분기형(180°)":
                        return R.drawable.t_type_180;

                    case "T분기형(270°)":
                        return R.drawable.t_type_270;

                    case "엘보형(0°)":
                        return R.drawable.l_type_0;

                    case "엘보형(90°)":
                        return R.drawable.l_type_90;

                    case "엘보형(180°)":
                        return R.drawable.l_type_180;

                    case "엘보형(270°)":
                        return R.drawable.l_type_270;

                    case "관말형(0°)":
                        return R.drawable.e_type_0;

                    case "관말형(90°)":
                        return R.drawable.e_type_90;

                    case "관말형(180°)":
                        return R.drawable.e_type_180;

                    case "관말형(270°)":
                        return R.drawable.e_type_270;
                }
            } else {
                return R.drawable.l_type_0;
            }
        }
    }

}
