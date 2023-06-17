package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.BaseApplication;

public class CastleEndLoad extends EndLoad {

    public static final int START_OID = 57900;//启动点码

    public static final String NAME = BaseApplication.isEnglish ? "Fantasy Castle" : "梦幻城堡";

    public CastleEndLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String[] getEquipmentLoads() {
        return new String[]{
                CrystalShoesLoad.NAME, // 水晶鞋
                DanceSkirtLoad.NAME, // 跳舞裙
                FatTonnyLoad.NAME // 南瓜车
        };
    }

}