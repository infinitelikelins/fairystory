package com.bearya.actionlib.light;

/**
 * Created by yexifeng on 2018/2/28.
 */

public interface ILight {
    void onEmotionLight();//表情状态
    void onSleepLight();//休眠状态
    void onScreenOffLight();//熄屏状态
    void onWorkLight();//工作状态
    void onLowPowerLight();//低电量状态
    void onChargeingLight();//充电状态
}
