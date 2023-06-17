package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.walk.Direct;

public class RightAction extends DirectAction{
    public RightAction(int step) {
        super( Direct.Right, step);
    }

    @Override
    public String getName() {
        return "右转";
    }
}
