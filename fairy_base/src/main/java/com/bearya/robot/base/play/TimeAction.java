package com.bearya.robot.base.play;

public class TimeAction {
    private int time;
    private int action;

    public TimeAction(int action, int time) {
        this.time = time;
        this.action = action;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getTime() {
        return time;
    }

    public int getAction() {
        return action;
    }
}
