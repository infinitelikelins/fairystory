package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class NineTailedCatLoad extends ObstacleLoad {

    public static final int START_OID = 42600;//启动点码
    public static final String NAME = "九尾猫地垫";

    public NineTailedCatLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.PlayingCatSticks));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.cat_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/cat_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.cat_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/cat_n.mp3";
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.catFailPlayTime;
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.catSuccessPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryNineTailedCatSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryNineTailedCatFail;
    }

}