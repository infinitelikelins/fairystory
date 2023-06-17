package com.bearya.robot.fairystory.walk.load;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.play.Director;
import com.bearya.robot.base.play.FacePlay;
import com.bearya.robot.base.play.FaceType;
import com.bearya.robot.base.play.LoadPlay;
import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.fairystory.station.StationConfigActivity;
import com.bearya.robot.fairystory.ui.res.HistoryRecord;
import com.bearya.robot.fairystory.walk.load.lock.DirectorPlayLock;

public abstract class StationLoad extends XLoad {

    public StationLoad(int startOid) {
        super(startOid);
        lock = new DirectorPlayLock();
    }

    @Override
    public void registerPlay() {
        LoadPlay newLoadPlay = new LoadPlay();
        PlayData playData = StationConfigActivity.getLastConfigStation(BaseApplication.getInstance(), getStationIndex());
        if (!playData.isEmpty()) {
            if (playData.facePlay == null) {
                playData.facePlay = new FacePlay("hg", FaceType.Lottie);
            }
            newLoadPlay.addLoad(playData);
            HistoryRecord.getInstance().put(playData);
        }
        Director.getInstance().register(ON_NEW_LOAD, newLoadPlay);
    }

    public abstract int getStationIndex();

}