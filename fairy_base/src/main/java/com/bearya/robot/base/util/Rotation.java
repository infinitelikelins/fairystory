package com.bearya.robot.base.util;

public class Rotation extends BaseAction{

    private long timeMilli;

    private float begin;

    private float end;

    public Rotation(long timeMilli, float begin, float end) {
        this.timeMilli = timeMilli;
        this.begin = begin;
        this.end = end;
    }

    public long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(long timeMilli) {
        this.timeMilli = timeMilli;
    }

    public float getBegin() {
        return begin;
    }

    public void setBegin(float begin) {
        this.begin = begin;
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
    }
}
