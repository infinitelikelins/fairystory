package com.bearya.robot.base.walk.action;


import com.bearya.robot.base.walk.Direct;

public abstract class DirectAction extends Action {
    public DirectAction(Direct mDirect, int mStep) {
        super(mDirect, mStep);
    }
}
