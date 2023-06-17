package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class VolcanicLoad extends NoEntryLoad {

    public static final int START_OID = 31800;//启动点码
    public static final String NAME = "火山地垫";

    public VolcanicLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.volcano;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/volcano_fail.mp3";
    }

    @Override
    protected int loadPlayFaceTime() {
        return IntroduceTime.volcanicPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryVolcanic;
    }

}