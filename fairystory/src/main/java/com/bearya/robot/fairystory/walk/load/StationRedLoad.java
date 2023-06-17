package com.bearya.robot.fairystory.walk.load;

public class StationRedLoad extends StationLoad {

    public static final int START_OID = 52500;
    public static final String NAME = "红色站点";
    public static final int STATION_INDEX = 5;

    public StationRedLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getStationIndex() {
        return STATION_INDEX;
    }

}