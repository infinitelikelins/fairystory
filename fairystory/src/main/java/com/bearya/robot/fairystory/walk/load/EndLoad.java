package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.play.Director;
import com.bearya.robot.base.play.LoadPlay;
import com.bearya.robot.fairystory.walk.load.lock.DirectorPlayLock;

public abstract class EndLoad extends XLoad {

    public EndLoad(int startOid) {
        super(startOid);
        lock = new DirectorPlayLock();
    }

    @Override
    public void registerPlay() {
        LoadPlay unlockSuccessPlay = new LoadPlay();
        Director.getInstance().register(ON_NEW_LOAD, unlockSuccessPlay);
    }

    /**
     * 到达终点所需要的装备
     */
    public abstract String[] getEquipmentLoads();

}