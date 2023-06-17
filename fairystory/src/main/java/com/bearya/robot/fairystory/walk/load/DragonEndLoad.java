package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.BaseApplication;

public class DragonEndLoad extends EndLoad {
    public static final int START_OID = 57000;//启动点码
    public static final String NAME = BaseApplication.isEnglish ? "Valley of Evil Dragon" : "恶龙谷";

    public DragonEndLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String[] getEquipmentLoads() {
        return new String[]{
                ArmorLoad.NAME, // 铠甲
                PegasusLoad.NAME, // 飞马
                SwordLoad.NAME // 宝剑
        };
    }

}