package com.bearya.robot.base.walk;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.walk.action.MoveAndDirectAction;

public class LoadEntrance {
    private String loadName;
    private int entranceOid;
    private int exitOid;
    private MoveAndDirectAction action;

    public LoadEntrance(String loadName, int entranceOid, int exitOid) {
        this.loadName = loadName;
        this.entranceOid = entranceOid;
        this.exitOid = exitOid;
    }

    public LoadEntrance() {

    }

    public void setLoad(BaseLoad load) {
        this.loadName = load.getName();
    }

    public void setEntranceOid(int entranceOid) {
        this.entranceOid = entranceOid;
    }

    public BaseLoad getLoad() {
        return BaseApplication.getInstance().getLoadMgr().getLoad(loadName);
    }

    @Override
    public String toString() {
        return loadName + " 入口 " + entranceOid;
    }

    public void setAction(MoveAndDirectAction action) {
        this.action = action;
    }

    public TravelPath<Travel> computeExitPath(int strategy) {
        TravelPath<Travel> travelPath = getLoad().getExitTravelPath(entranceOid, action, strategy);
        action = null;
        return travelPath;
    }

    public LoadEntrance newInstance() {
        return new LoadEntrance(loadName, entranceOid, exitOid);
    }

    public String getLoadName() {
        return loadName;
    }

    public void reset() {
        loadName = null;
        entranceOid = 0;
        exitOid = 0;
        action = null;
    }
}
