package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.walk.Direct;

public abstract class Action implements IAction {
    private Direct mDirect;
    private int mStep;
    private int moveHowStep;//走了几步
    private Additional additional;//附加道具

    public Action(Direct mDirect, int mStep) {
        this.mDirect = mDirect;
        this.mStep = mStep;
    }

    @Override
    public int getStep() {
        return mStep;
    }

    @Override
    public Direct getDirect() {
        return mDirect;
    }

    public void setStep(int step) {
        this.mStep = step;
    }

    public void append(Action action) {
        if (action == null || getClass() != action.getClass()) {
            return;
        }
        setStep(getStep() + action.getStep());
    }

    public void moveOneStep() {
        moveHowStep++;
    }

    public boolean isCompleted() {
        return moveHowStep >= mStep;
    }

    public void setAdditional(Additional additional) {
        this.additional = additional;
    }

    public Additional getAdditional() {
        return additional;
    }

}
