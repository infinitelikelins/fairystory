package com.bearya.actionlib.utils;

import android.os.Build;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static com.bearya.actionlib.utils.WriteLightUtil.writeEarLightByColor;
import static com.bearya.actionlib.utils.WriteLightUtil.writeUsbLightByColor;

public class AutoLightHandle {
    private int time = 3000;
    private boolean isSingleColor = false;
    private int color = 0;
    private boolean isBreathe;
    private Subscription mLeftSubscription;
    private Subscription mRightSubscription;
    private Subscription mUsbSubscription;

    private AutoLightHandle() {
    }

    private static class Holder {
        static AutoLightHandle handle = new AutoLightHandle();
    }

    public static AutoLightHandle getInstance() {
        return Holder.handle;
    }

    public void startAutoLightThread(int time) {
        startAutoLightThread(0, false, time, true);
    }

    public void startAutoLightThread(int color, boolean isSingleColor, int time, boolean isBreathe) {
        int c = getColor(color);
        startAutoLeftLight(c, isSingleColor, time, isBreathe);
        startAutoRightLight(c, isSingleColor, time, isBreathe);
        startAutoUsbLight(c, isSingleColor, time, isBreathe);
    }

    public void startAutoLeftLight(int color, boolean isSingleColor, int time, boolean isBreathe) {
        this.color = getColor(color);
        this.isSingleColor = isSingleColor;
        this.time = time;
        this.isBreathe = isBreathe;

        if (mLeftSubscription != null && !mLeftSubscription.isUnsubscribed()) {
            mLeftSubscription.unsubscribe();
        }
        mLeftSubscription = writeAutoLight(RobotActionManager.Ear.LEFT, time, isSingleColor, color, isBreathe);
    }

    public void startAutoRightLight(int color, boolean isSingleColor, int time, boolean isBreathe) {
        this.color = getColor(color);
        this.isSingleColor = isSingleColor;
        this.time = time;
        this.isBreathe = isBreathe;

        if (mRightSubscription != null && !mRightSubscription.isUnsubscribed()) {
            mRightSubscription.unsubscribe();
        }
        mRightSubscription = writeAutoLight(RobotActionManager.Ear.RIGHT, time, isSingleColor, color, isBreathe);
    }

    public void startAutoUsbLight(int color, boolean isSingleColor, int time, boolean isBreathe) {
        this.color = getColor(color);
        this.isSingleColor = isSingleColor;
        this.time = time;
        this.isBreathe = isBreathe;

        if (mUsbSubscription != null && !mUsbSubscription.isUnsubscribed()) {
            mUsbSubscription.unsubscribe();
        }
        mUsbSubscription = writeUsbAutoLight( time, isSingleColor, color, isBreathe);
    }

    private int getColor(int color) {
        if (color >= 7 || color < 0) {
            return new Random().nextInt(7);
        }
        return color;
    }

    public void startAutoLightThread() {
        startAutoLightThread(3000);
    }

    public void stopAutoLightThread() {
        System.out.println("stopAutoLightThread");
        RobotActionManager.closeEarLight();
        RobotActionManager.closeUsbLight();
        if (mLeftSubscription != null && !mLeftSubscription.isUnsubscribed()) {
            mLeftSubscription.unsubscribe();
        }
        if (mRightSubscription != null && !mRightSubscription.isUnsubscribed()) {
            mRightSubscription.unsubscribe();
        }
        if (mUsbSubscription != null && !mUsbSubscription.isUnsubscribed()) {
            mUsbSubscription.unsubscribe();
        }
    }

    public void stopAutoLeftLightThread() {
        if (mLeftSubscription != null && !mLeftSubscription.isUnsubscribed()) {
            mLeftSubscription.unsubscribe();
        }
    }

    public void stopAutoRightLightThread() {
        if (mRightSubscription != null && !mRightSubscription.isUnsubscribed()) {
            mRightSubscription.unsubscribe();
        }
    }

    private Subscription writeAutoLight(RobotActionManager.Ear ear, int time, final boolean isSingleColor, int color, final boolean isBreathe) {
        final int[] c = {color};

        final int aa = 0;
        final int bb = 0x01;
        final int[] cc = {0x10};
        final int dd = 0x02;
        final long[] input = {0};
        final int[] i = {0};
        final int v = ear.getValue();
        final int interval = isBreathe ? time / 100 : time / 2;
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (Build.BOARD.startsWith("rk")) {
                            if (!isBreathe) {//频闪
                                input[0] = input[0] == 0 ? 255 : 0;
                            }
                            if (input[0] < 0) {
                                input[0] = 0;
                            }
//                            System.out.println("brightness" + input[0]);
                            writeEarLightByColor(c[0], v, input[0]);
                            if (isBreathe) {
                                if (i[0] >= 0 && i[0] <= 50) {
                                    input[0] += 5;
                                } else if (i[0] >= 50 && i[0] <= 101) {
                                    input[0] -= 5;
                                } else {
                                    if (!isSingleColor) {
                                        c[0]++;
                                        if (c[0] == 7) {
                                            c[0] = 0;
                                        }
                                    }
                                    i[0] = -1;
                                    input[0] = 0;
                                }
                                i[0]++;
                            } else {
                                if (!isSingleColor) {
                                    c[0]++;
                                    if (c[0] == 7) {
                                        c[0] = 0;
                                    }
                                }
                            }
                        } else {
                            if (!isBreathe) {//频闪
                                input[0] = input[0] == 0 ? 1 : 0;
                            } else {
                                input[0] = (bb << 16) + (cc[0] << 8) + dd;
                            }
                            writeEarLightByColor(c[0], v, input[0]);
                            if (isBreathe) {
                                if (i[0] >= 0 && i[0] < 20) {
                                    cc[0] = cc[0] + 5;
                                } else if (i[0] >= 20 && i[0] < 40) {
                                    cc[0] = cc[0] + 4;
                                } else if (i[0] >= 40 && i[0] < 50) {
                                    cc[0] = cc[0] + 3;
                                } else if (i[0] >= 50 && i[0] < 60) {
                                    cc[0] = cc[0] - 3;
                                } else if (i[0] >= 60 && i[0] < 80) {
                                    cc[0] = cc[0] - 4;
                                } else if (i[0] >= 80 && i[0] < 100) {
                                    cc[0] = cc[0] - 5;
                                } else {
                                    if (!isSingleColor) {
                                        c[0]++;
                                        if (c[0] == 7) {
                                            c[0] = 0;
                                        }
                                    }
                                    i[0] = 0;
                                    cc[0] = 0x10;
                                }
                                i[0]++;
                            } else {
                                if (!isSingleColor) {
                                    c[0]++;
                                    if (c[0] == 7) {
                                        c[0] = 0;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private Subscription writeUsbAutoLight(int time, final boolean isSingleColor, int color, final boolean isBreathe) {
        final int[] c = {color};
        final long[] input = {0};
        final int[] i = {0};
        final int interval = isBreathe ? time / 100 : time / 2;
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                        if (!isBreathe) {//频闪
                            input[0] = input[0] == 0 ? 255 : 0;
                        }
                        if (input[0] < 0) {
                            input[0] = 0;
                        }
//                            System.out.println("brightness" + input[0]);
                        writeUsbLightByColor(c[0], input[0]);
                        if (isBreathe) {
                            if (i[0] >= 0 && i[0] <= 50) {
                                input[0] += 5;
                            } else if (i[0] >= 50 && i[0] <= 101) {
                                input[0] -= 5;
                            } else {
                                if (!isSingleColor) {
                                    c[0]++;
                                    if (c[0] == 7) {
                                        c[0] = 0;
                                    }
                                }
                                i[0] = -1;
                                input[0] = 0;
                            }
                            i[0]++;
                        } else {
                            if (!isSingleColor) {
                                c[0]++;
                                if (c[0] == 7) {
                                    c[0] = 0;
                                }
                            }
                        }
                    }
                });
    }
}