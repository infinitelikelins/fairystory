package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class CompassLoad extends EquipmentLoad {

    public static final int START_OID = 34500;//启动点码

    public static final String NAME = "指南针地垫";

    public CompassLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.compass;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/treasure_compass.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.compassPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryCompass;
    }
}
