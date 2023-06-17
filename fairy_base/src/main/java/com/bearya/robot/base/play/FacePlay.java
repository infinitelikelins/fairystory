package com.bearya.robot.base.play;

public class FacePlay {
    private String face;
    private FaceType faceType;
    private int time;

    public FacePlay(String face, FaceType faceType) {
        this.face = face;
        this.faceType = faceType;
        this.time = 50;
    }

    public FacePlay(String face, FaceType faceType, int time) {
        this.face = face;
        this.faceType = faceType;
        this.time = time;
    }

    public String getFace() {
        return face;
    }

    public FaceType getFaceType() {
        return faceType;
    }

    public int getTime() {
        return time;
    }
}
