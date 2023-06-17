package com.bearya.actionlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.tencent.mmkv.MMKV;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesUtil {

    public static final String KEY_RF_ADDRESS = "rf_address";

    private static SharedPreferencesUtil instance = null;

    public static SharedPreferencesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesUtil(context);
        }
        return instance;
    }

    private final MMKV mmkv;

    private SharedPreferencesUtil(Context context) {
        SharedPreferences sp = context.getSharedPreferences("com.bearya.robot.sps", Context.MODE_PRIVATE);
        String packageName = context.getPackageName();

        String relativePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.bearya.robot.programme/mmkv";
        if (packageName.equals("com.bearya.robot.programme")){
            mmkv = MMKV.mmkvWithID("city", MMKV.MULTI_PROCESS_MODE, "CITY", relativePath);
        }else if (packageName.equals("com.bearya.robot.fairystory")){
            mmkv = MMKV.mmkvWithID("story", MMKV.MULTI_PROCESS_MODE, "STORY", relativePath);
        }else{
            mmkv = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE,"DEF");
        }
        if (mmkv != null) {
            mmkv.importFromSharedPreferences(sp);
        }
        sp.edit().clear().apply();
    }

    /**
     * 添加信息到SharedPreferences
     */
    public SharedPreferencesUtil put(Map<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            mmkv.encode(key, map.get(key));
        }
        return this;
    }

    public SharedPreferencesUtil put(String key, String value) {
        mmkv.encode(key, value);
        return this;
    }

    public SharedPreferencesUtil put(String key, boolean value) {
        mmkv.encode(key, value);
        return this;
    }

    public SharedPreferencesUtil put(String key, long value) {
        mmkv.encode(key, value);
        return this;
    }

    public SharedPreferencesUtil put(String key, int value) {
        mmkv.encode(key, value);
        return this;
    }
    /**
     * 删除一条信息
     */
    public void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    /**
     * 获取信息
     */
    public String getString(String key) {
        if (mmkv != null) {
            return mmkv.decodeString(key, "");
        }
        return "";
    }

    public int getInt(String key, int defaultValue) {
        if (mmkv != null) {
            return mmkv.decodeInt(key, defaultValue);
        }
        return 0;
    }
}