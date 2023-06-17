package com.bearya.robot.fairystory.walk.car;

import android.os.SystemClock;

/**
 * 发送机
 */
public abstract class Engine implements Runnable {

    private boolean isRunning;

    public static final int F = 7;//每秒执行次数
    public static final int F_T = 1000 / F;//每次执行时间

    @Override
    public void run() {
        while (isRunning) {
            update();
            SystemClock.sleep(F_T);
        }
    }

    public void start() {
        isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public abstract void update();
}