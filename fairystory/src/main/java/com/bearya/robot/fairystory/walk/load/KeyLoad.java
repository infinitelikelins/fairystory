package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;

public class KeyLoad extends EquipmentLoad {

    public static final int START_OID = 35400;//启动点码
    public static final String NAME = "钥匙地垫";

    public KeyLoad() {
        super(START_OID);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFacePlay() {
        return R.array.key;
    }

    @Override
    protected String loadPlaySound() {
        return "music/zh/treasure_key.mp3";
    }

    @Override
    protected int loadFacePlayTime() {
        return IntroduceTime.keyPlayTime;
    }

    @Override
    protected String loadCommand() {
        return Command.FairyStoryKey;
    }
}