package com.bearya.robot.base.play;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.bearya.actionlib.utils.RobotActionManager;

public class PlayData {
    public static final int ONLY_SOUND = 1;
    public static final int ONLY_FRAME_ANIMATION = 2;
    public static final int ONLY_ACTION = 4;
    public static final int ONLY_IMAGE = 8;
    public String sound;

    public long soundLongTime;

    public FacePlay facePlay;
    public RobotActionManager.LightMode mode;
    public RobotActionManager.LightColor color;
    public ArrayMap<Integer, TimeAction> actions = null;

    private int completeCondition;
    private int alreadyCompleteCondition;
    public String playAction = null;

    public PlayData() {
    }

    public PlayData(String sound) {
        this.sound = sound;
    }

    public PlayData(FacePlay facePlay) {
        this.facePlay = facePlay;
    }

    public PlayData(String sound, FacePlay facePlay) {
        this.sound = sound;
        this.facePlay = facePlay;
    }

    public PlayData(PlayData playData) {
        sound = playData.sound;
        soundLongTime = playData.soundLongTime;
        facePlay = playData.facePlay;
        mode = playData.mode;
        color = playData.color;
        actions = playData.actions;
        playAction = playData.playAction;
        completeCondition = playData.completeCondition;
        alreadyCompleteCondition = playData.alreadyCompleteCondition;
    }

    public void countCompleteCondition() {
        completeCondition = 0;
        if (!TextUtils.isEmpty(sound)) {
            completeCondition += ONLY_SOUND;
        }
        if (facePlay != null) {
            if ((facePlay.getFaceType() == FaceType.FrameAnimate || facePlay.getFaceType() == FaceType.Arrays)) {
                completeCondition += ONLY_FRAME_ANIMATION;
            } else if (facePlay.getFaceType() == FaceType.Image || facePlay.getFaceType() == FaceType.Lottie) {
                completeCondition += ONLY_IMAGE;
            }

        }
        if (actions != null && actions.size() > 0) {
            completeCondition += ONLY_ACTION;
        }
    }

    public void complete(int condition) {
        alreadyCompleteCondition = alreadyCompleteCondition | condition;
    }

    public boolean isComplete() {
        return alreadyCompleteCondition >= completeCondition;
    }

    public String getPlayAction() {
        return playAction;
    }

    public void setPlayAction(String playAction) {
        this.playAction = playAction;
    }

    public String getSound() {
        return sound;
    }

    public FacePlay getFacePlay() {
        return facePlay;
    }

    public RobotActionManager.LightMode getMode() {
        return mode;
    }

    public RobotActionManager.LightColor getColor() {
        return color;
    }

    public TimeAction[] getRobotAction() {
        TimeAction[] acs = null;
        if (actions != null && actions.size() > 0) {
            acs = new TimeAction[actions.size()];
            int index = 0;
            for (int i = 0; i < 3; i++) {
                TimeAction value = actions.valueAt(i);
                if (value != null) {
                    acs[index++] = value;
                }
            }
        }
        return acs;
    }

    public boolean containFace() {
        return facePlay != null && !TextUtils.isEmpty(facePlay.getFace());
    }

    public boolean containMp3() {
        return !TextUtils.isEmpty(sound);
    }

    public boolean containAction() {
        return actions != null && actions.size() > 0;
    }

    public void removeAction(Integer tag) {
        if (actions != null && actions.size() > 0 && actions.indexOfKey(tag) >= 0) {
            actions.remove(tag);
        }
        if (actions != null && actions.size() <= 0) {
            actions = null;
        }
    }

    public boolean isEmpty() {
        return !containFace() && !containMp3() && !containAction();
    }

}