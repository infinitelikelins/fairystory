package com.bearya.robot.base.util;

import android.content.Context;

import com.bearya.robot.base.BaseApplication;

public class ResourceUtil {
    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRAWBLE = "drawable";
    private static final String RES_MIPMAP = "mipmap";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";
    private static final String RES_RAW = "raw";

    public static int getRawIdByName(String name) {
        return getResId(BaseApplication.getInstance(), name, RES_RAW);
    }

    public static int getMipmapId(String name) {
        return getResId(BaseApplication.getInstance(), name, RES_MIPMAP);
    }


    public static int getId(Context context, String resName) {
        return getResId(context, resName, RES_ID);
    }

    public static int getStringId(Context context, String resName) {
        return getResId(context, resName, RES_STRING);
    }


    public static int getDrableId(String resName) {
        return getResId(BaseApplication.getInstance(), resName, RES_DRAWBLE);
    }

    public static int getLayoutId(Context context, String resName) {
        return getResId(context, resName, RES_LAYOUT);
    }

    public static int getStyleId(Context context, String resName) {
        return getResId(context, resName, RES_STYLE);
    }

    public static int getColorId(Context context, String resName) {
        return getResId(context, resName, RES_COLOR);
    }

    public static int getDimenId(Context context, String resName) {
        return getResId(context, resName, RES_DIMEN);
    }

    public static int getAnimId(Context context, String resName) {
        return getResId(context, resName, RES_ANIM);
    }


    public static int getMenuId(Context context, String resName) {
        return getResId(context, resName, RES_MENU);
    }

    public static int getResId(Context context, String resName, String defType) {
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }

}