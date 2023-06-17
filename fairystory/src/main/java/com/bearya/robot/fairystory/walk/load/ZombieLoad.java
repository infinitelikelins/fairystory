package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class ZombieLoad extends ObstacleLoad {

    public static final int START_OID = 32700;//启动点码
    public static final String NAME = "僵尸地垫";

    public ZombieLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.SunflowerWarrior));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.zombie_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/zombie_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.zombie_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/zombie_n.mp3";
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.zombieFailPlayTime;
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.zombieSuccessPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryZombieSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryZombieFail;
    }

}