package com.bearya.robot.base.walk;

/**
 * Created by yexifeng on 17/11/18.
 */

import com.bearya.robot.base.util.DebugUtil;

/**
 * 前后OID与目标点的距离
 */
public class DistanceInfo {
    public int headToTarget;
    public int tailToTarget;

    public DistanceInfo(int headOidDis, int tailOidDis) {
        this.headToTarget = headOidDis;
        this.tailToTarget = tailOidDis;
    }

    public boolean isArrive() {
        DebugUtil.debug("前读头距离目标点:%d",headToTarget);
        return isArrive(3);
    }
    public boolean isArrive(int dis) {
        DebugUtil.debug("前读头距离目标点:%d",headToTarget);
        return headToTarget<=dis;
    }
}