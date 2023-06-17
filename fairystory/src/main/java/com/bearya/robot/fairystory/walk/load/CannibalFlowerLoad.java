package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class CannibalFlowerLoad extends ObstacleLoad {
    public static final int START_OID = 45300;//启动点码
    public static final String NAME = "食人花地垫";

    public CannibalFlowerLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.StickyBullet));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.flower_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/chomper_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.flower_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/chomper_n.mp3";
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.flowerSuccessPlayTime;
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.flowerFailPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryCannibalFlowerSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryCannibalFlowerFail;
    }
}
