package com.bearya.robot.fairystory.walk.car.travel;

public interface WheelControllerListener {
    int getHeadOid();
    int getTailOid();
    void onCompleteTravelPath();
}
