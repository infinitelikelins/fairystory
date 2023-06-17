package com.bearya.robot.base.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

public class DeviceUtil {

    /**
     * 获取板子上的序列号
     */
    public static String getRKBroadProductCode(){
        String serialNum = Build.SERIAL;
        return !TextUtils.isEmpty(serialNum) ? serialNum : "0";
    }

    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0.0";
        }
    }

}