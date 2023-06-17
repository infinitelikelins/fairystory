package com.bearya.robot.base.play;


import java.util.ArrayList;
import java.util.List;

public class LoadPlay {
    private List<PlayData> playDataList = new ArrayList<>();

    public String playAction;

    public LoadPlay() {
    }

    public LoadPlay(List<PlayData> playDataList) {
        this.playDataList = playDataList;
    }

    public void addLoad(PlayData data){
        playDataList.add(data);
    }

    public PlayData getPlay() {
        if(!isComplete()) {
            return playDataList.remove(0);
        }else{
            return null;
        }
    }

    public boolean isComplete(){
        return playDataList.isEmpty();
    }

    public LoadPlay(LoadPlay data) {
        this.playDataList.addAll(data.playDataList);
        this.playAction = data.playAction;
    }

}
