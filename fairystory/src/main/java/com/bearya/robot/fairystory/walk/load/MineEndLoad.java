package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.BaseApplication;

public class MineEndLoad extends EndLoad {
    public static final int START_OID = 10900;//启动点码
    public static final String NAME = BaseApplication.isEnglish ? "TreasureIsland" : "宝藏岛";

    public MineEndLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String[] getEquipmentLoads() {
        return new String[]{
                TreasureMapLoad.NAME, // 藏宝图
                CompassLoad.NAME, // 指南针
                KeyLoad.NAME // 钥匙
        };
    }
}