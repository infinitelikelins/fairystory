package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class DanceSkirtLoad extends EquipmentLoad {
    public static final int START_OID = 46200;//启动点码
    public static final String NAME = "舞裙地垫";

    public DanceSkirtLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.skirt;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/ball_skirt.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.danceSkirtPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryDanceSkirt;
    }
}