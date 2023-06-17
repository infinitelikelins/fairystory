package com.bearya.robot.base.walk.action;

import com.bearya.robot.base.walk.Direct;

public interface IAction {
    int getStep();
    Direct getDirect();
    String getName();
}
