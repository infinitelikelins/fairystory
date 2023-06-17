package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.walk.Direct;

public class LeftAction extends DirectAction{
    public LeftAction(int step) {
        super( Direct.Left, step);
    }

    @Override
    public String getName() {
        return "左转";
    }
}
