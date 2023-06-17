package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class FatTonnyLoad extends EquipmentLoad {
    public static final int START_OID = 48000;//启动点码
    public static final String NAME = "南瓜车地垫";

    public FatTonnyLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.carriage;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/ball_carriage.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.fatTonnyPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryFatTonny;
    }
}
