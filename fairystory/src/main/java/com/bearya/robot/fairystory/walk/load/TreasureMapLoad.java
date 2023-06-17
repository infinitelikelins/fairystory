package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class TreasureMapLoad extends EquipmentLoad {
    public static final int START_OID = 33600;//启动点码
    public static final String NAME = "藏宝图地垫";

    public TreasureMapLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.treasure;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/treasure_map.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.treasurePlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryTreasureMap;
    }

}