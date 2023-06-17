package com.bearya.robot.base.musicplayer;

import android.media.MediaPlayer;

/**
 * Created by lianweidong on 2017/11/18.
 */

public interface IPlayer {
    void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);
    void setOnErrorListener(MediaPlayer.OnErrorListener listener);
    void setOnPrepared(MediaPlayer.OnPreparedListener listener);
    void play(String dataSource) throws Exception;
    void stop();
    void release();
}
