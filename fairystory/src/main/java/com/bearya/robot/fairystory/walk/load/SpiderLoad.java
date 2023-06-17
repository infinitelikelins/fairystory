package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class SpiderLoad extends ObstacleLoad {

    public static final int START_OID = 36300;//启动点码
    public static final String NAME = "蜘蛛地垫";

    public SpiderLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.SweaterNeedle));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.spider_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/spider_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.spider_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/spider_n.mp3";
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.spiderSuccessPlayTime;
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.spiderFailPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStorySpiderSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStorySpiderFail;
    }
}