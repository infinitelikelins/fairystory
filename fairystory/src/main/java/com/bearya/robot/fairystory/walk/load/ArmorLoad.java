package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class ArmorLoad extends EquipmentLoad {
    public static final int START_OID = 49800;//启动点码
    public static final String NAME = "铠甲地垫";

    public ArmorLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.armor;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/hero_armor.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.armorPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryArmor;
    }
}
