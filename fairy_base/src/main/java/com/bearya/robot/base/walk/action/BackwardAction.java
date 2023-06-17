package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.walk.Direct;

public class BackwardAction extends DirectAction {
    public BackwardAction(int step) {
        super( Direct.Backward, step);
    }

    @Override
    public String getName() {
        return "调头";
    }
}