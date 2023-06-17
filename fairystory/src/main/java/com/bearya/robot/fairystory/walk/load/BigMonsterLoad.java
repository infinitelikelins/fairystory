package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class BigMonsterLoad extends ObstacleLoad {

    public static final int START_OID = 43500; // 启动点码
    public static final String NAME = "巨怪地垫";

    public BigMonsterLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.PolyJuicePotion));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.monster_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/troll_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.monster_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/troll_n.mp3";
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.monsterFailPlayTime;
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.monsterSuccessPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryBigMonsterSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryBigMonsterFail;
    }

}