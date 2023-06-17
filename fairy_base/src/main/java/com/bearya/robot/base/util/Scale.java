package com.bearya.robot.base.util;

public class Scale extends BaseAction{

    private long timeMilli;

    private float ratio;

    public Scale(long timeMilli, float ratio) {
        this.timeMilli = timeMilli;
        this.ratio = ratio;
    }

    public long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(long timeMilli) {
        this.timeMilli = timeMilli;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
