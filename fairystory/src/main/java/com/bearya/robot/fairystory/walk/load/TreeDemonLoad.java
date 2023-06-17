package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.card.Additional;
import com.bearya.robot.base.protocol.EquipmentCard;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.AdditionalLock;

public class TreeDemonLoad extends ObstacleLoad {
    public static final int START_OID = 44400;//启动点码
    public static final String NAME = "千年树妖地垫";

    public TreeDemonLoad() {
        super(START_OID);
        lock = new AdditionalLock(new Additional(EquipmentCard.DancingFlute));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected int loadFaceSuccessPlay() {
        return R.array.tree_success;
    }

    @Override
    protected String loadPlaySuccessSound() {
        return "tts/zh/dryads_p.mp3";
    }

    @Override
    protected int loadFaceFailPlay() {
        return R.array.tree_fail;
    }

    @Override
    protected String loadPlayFailSound() {
        return "tts/zh/dryads_n.mp3";
    }

    @Override
    protected int loadFacePlayFailTime() {
        return IntroduceTime.treeFailPlayTime;
    }

    @Override
    protected int loadFacePlaySuccessTime() {
        return IntroduceTime.treeSuccessPlayTime;
    }

    @Override
    protected String loadSuccessCommand() {
        return Command.FairyStoryTreeDemonSuccess;
    }

    @Override
    protected String loadFailCommand() {
        return Command.FairyStoryTreeDemonFail;
    }
}
