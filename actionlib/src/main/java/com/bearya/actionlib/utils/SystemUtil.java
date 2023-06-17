package com.bearya.actionlib.utils;

import android.content.Context;
import android.content.Intent;

public class SystemUtil {

    /**
     * 关机
     */
    public static void shutDown(Context context) {
        context.sendBroadcast(new Intent("com.machine.shutdown"));
    }

    /**
     * 重启
     */
    public static void reboot(Context context) {
        context.sendBroadcast(new Intent("com.machine.reboot"));
    }

    /**
     * 发送亮屏广播
     */
    public static void screenOn(Context context) {
        context.sendBroadcast(new Intent("com.screen.light"));
    }
}
