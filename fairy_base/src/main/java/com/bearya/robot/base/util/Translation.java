package com.bearya.robot.base.util;

public class Translation extends BaseAction{

    private long timeMilli;
    private AnimationDirector director;
    private float distance;

    public Translation(long timeMilli, AnimationDirector director, float distance) {
        this.timeMilli = timeMilli;
        this.director = director;
        this.distance = distance;
    }

    public long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(long timeMilli) {
        this.timeMilli = timeMilli;
    }

    public AnimationDirector getDirector() {
        return director;
    }

    public void setDirector(AnimationDirector director) {
        this.director = director;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
