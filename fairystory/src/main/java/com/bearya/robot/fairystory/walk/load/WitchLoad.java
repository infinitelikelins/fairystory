package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class WitchLoad extends ObstacleLoad {

    public static final int START_OID = 30900;//启动点码
    public static final String NAME = "女巫地垫";

    public WitchLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.MagicWand));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.witch_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/witch_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.witch_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/witch_n.mp3";
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.witchSuccessPlayTime;
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.witchFailPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryWitchSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryWitchFail;
    }

}