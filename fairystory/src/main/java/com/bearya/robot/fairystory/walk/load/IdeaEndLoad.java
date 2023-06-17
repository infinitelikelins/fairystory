package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.BaseApplication;

public class IdeaEndLoad extends EndLoad {

    public static final int START_OID = 1000;// 启动点码 (先于跳跳镇的终点码值重合)
    public static final String NAME = BaseApplication.isEnglish ? "IdeaWorld" : "兰科世界";  // 来源于 动画 虚幻勇士中的 计算机创造出来的虚拟世界

    public IdeaEndLoad() {
        super(START_OID);
    }

    @Override
    public String[] getEquipmentLoads() {
        return new String[0];
    }

    @Override
    public String getName() {
        return NAME;
    }

}