package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class CrocodileLakeLoad extends ObstacleLoad {
    public static final String NAME = "鳄鱼湖地垫";
    public static final int START_OID = 30000;//启动点码

    public CrocodileLakeLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.Boat));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.crocodile_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/river_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.crocodile_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/river_n.mp3";
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.crocodileSuccessPlayTime;
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.crocodileFailPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryCrocodileLakeSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryCrocodileLakeFail;
    }
}
