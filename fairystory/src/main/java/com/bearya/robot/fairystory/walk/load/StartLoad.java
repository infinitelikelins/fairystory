package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.car.TravelPath;
import com.bearya.robot.base.walk.Travel;
import com.bearya.robot.base.walk.TravelCrossOver;
import com.bearya.robot.base.walk.TravelFace;

public class StartLoad extends XLoad {
    public static final int START_OID = 26476;//启动点码
    public static final String NAME = "起点";

    public StartLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void registerPlay() {

    }

    public TravelPath<Travel> getInitTravelPath(int strategy) {
        TravelPath<Travel> travelPath = new TravelPath<>();
        if (strategy == XLoad.TRAVEL_PATH_STRATEGY_MOVE_ONLY) {
            travelPath.addTravel(new TravelCrossOver(27121));
        } else {
            travelPath.addTravel(new TravelFace(26701));
        }
        travelPath.addTravel(new TravelCrossOver(26701));
        return travelPath;
    }

    public int getFaceOid() {
        return 26491;
    }

}