package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class PegasusLoad extends EquipmentLoad {
    public static final int START_OID = 48900;//启动点码
    public static final String NAME = "飞马地垫";

    public PegasusLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.pegasus;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/hero_horse.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.pegasusPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryPegasus;
    }

}