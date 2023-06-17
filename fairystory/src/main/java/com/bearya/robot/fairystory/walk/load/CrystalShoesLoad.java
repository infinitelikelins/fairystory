package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class CrystalShoesLoad extends EquipmentLoad {
    public static final int START_OID = 47100;//启动点码
    public static final String NAME = "水晶鞋地垫";

    public CrystalShoesLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.shoes;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/ball_shoes.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.crystalShoesPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryCrystalShoes;
    }
}
