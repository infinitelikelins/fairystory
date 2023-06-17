package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class SwordLoad extends EquipmentLoad {
    public static final int START_OID = 50700;//启动点码
    public static final String NAME = "宝剑地垫";

    public SwordLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.sword;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/hero_sword.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.swordPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStorySword;
    }

}