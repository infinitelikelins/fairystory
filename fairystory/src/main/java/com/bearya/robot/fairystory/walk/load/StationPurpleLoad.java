package com.bearya.robot.fairystory.walk.load;

public class StationPurpleLoad extends StationLoad {

    public static final int START_OID = 55200;
    public static final String NAME = "紫色站点";
    public static final int STATION_INDEX = 4;

    public StationPurpleLoad() {
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