package com.bearya.robot.fairystory.walk.load;

import androidx.annotation.ArrayRes;

import com.bearya.robot.base.play.Director;
import com.bearya.robot.base.play.FacePlay;
import com.bearya.robot.base.play.FaceType;
import com.bearya.robot.base.play.LoadPlay;
import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.walk.load.lock.DirectorPlayLock;

/**
 * 装备地垫
 */
public abstract class EquipmentLoad extends XLoad {

    public EquipmentLoad(int startOid) {
        super(startOid);
        lock = new DirectorPlayLock();
    }

    @Override
    public void registerPlay() {
        LoadPlay unlockSuccessPlay = new LoadPlay();
        PlayData playData = new PlayData();
        playData.facePlay = new FacePlay(String.valueOf(loadFacePlay()), FaceType.Arrays, loadFacePlayTime());
        playData.sound = loadPlaySound();
        playData.playAction = loadCommand();
        unlockSuccessPlay.addLoad(playData);
        Director.getInstance().register(ON_NEW_LOAD, unlockSuccessPlay);
    }

    /**
     * 获得装备播放的动画文件数组
     */
    @ArrayRes
    protected abstract int loadFacePlay();

    /**
     * 获得装备播放音频
     */
    protected abstract String loadPlaySound();

    /**
     * 道具卡匹配的动画单帧动画时间的间隔
     */
    protected int loadFacePlayTime() {
        return IntroduceTime.def;
    }

    /**
     * 发送的命令定义
     */
    protected String loadCommand() {
        return Command.def;
    }

}