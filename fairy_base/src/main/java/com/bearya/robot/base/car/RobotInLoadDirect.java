package com.bearya.robot.base.car;

import com.bearya.robot.base.walk.Direct;
import com.bearya.robot.base.load.BaseLoad;

public class RobotInLoadDirect {

    private Direct faceDirect;

    public void update(BaseLoad load, int headOid, int tailOid) {

        int headDisLeftEntrance = load.distance(headOid, load.getLeftEntranceOid());
        int headDisRightEntrance = load.distance(headOid, load.getRightEntranceOid());
        int headDisTopEntrance = load.distance(headOid, load.getTopEntranceOid());
        int headDisBottomEntrance = load.distance(headOid, load.getBottomEntranceOid());
        int tailDisLeftEntrance = load.distance(tailOid, load.getLeftEntranceOid());
        int tailDisRightEntrance = load.distance(tailOid, load.getRightEntranceOid());
        int tailDisTopEntrance = load.distance(tailOid, load.getTopEntranceOid());
        int tailDisBottomEntrance = load.distance(tailOid, load.getBottomEntranceOid());

        int hltr = headDisLeftEntrance + tailDisRightEntrance;
        int hrtl = headDisRightEntrance + tailDisLeftEntrance;
        int httb = headDisTopEntrance + tailDisBottomEntrance;
        int hbtt = headDisBottomEntrance + tailDisTopEntrance;


        int min1 = Math.min(hltr, hrtl);
        int min2 = Math.min(httb, hbtt);
        int min = Math.min(min1, min2);
        if (min == hltr) {
            faceDirect = Direct.Left;
        } else if (min == hrtl) {
            faceDirect = Direct.Right;
        } else if (min == httb) {
            faceDirect = Direct.Forward;
        } else {
            faceDirect = Direct.Backward;
        }
    }

    public Direct getFaceDirect() {
        return faceDirect;
    }
}
