package com.bearya.robot.base.musicplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.bearya.robot.base.util.ActionDefine;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by lianweidong on 2017/11/18.
 */

public class LocalMusicPlayer implements IPlayer, MediaPlayer.OnPreparedListener {

    private Context mContext;
    protected MediaPlayer mp = null;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private boolean isLopping;

    public LocalMusicPlayer(Context context) {
        this(context,false);
    }
    public LocalMusicPlayer(Context context,boolean isLopping) {
        this.mContext = context;
        init(isLopping);
    }

    private void init(boolean isLopping){
        mp = new MediaPlayer();
        setLoop(isLopping);
    }


    public void setLoop(boolean loop) {
		this.isLopping = loop;
        if (mp != null) {
            mp.setLooping(loop);
        }
    }


    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        if(mp!=null) {
            mp.setOnCompletionListener(listener);
        }
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        if(mp!=null) {
            mp.setOnErrorListener(listener);
        }
    }

    @Override
    public void setOnPrepared(MediaPlayer.OnPreparedListener listener) {
        this.mOnPreparedListener = listener;
    }

    public void setDataSource(String dataSource) throws Exception {
        ActionDefine.DATA_SOURCE_TYPE dataSourceType = ActionDefine.DATA_SOURCE_TYPE.SDCARD;
        String assetsBegin = "android_asset/";
        if (dataSource.startsWith(assetsBegin)) {
            dataSourceType = ActionDefine.DATA_SOURCE_TYPE.ASSETS;
            dataSource = dataSource.substring(assetsBegin.length());
        }else if(dataSource.startsWith("http")){
            dataSourceType = ActionDefine.DATA_SOURCE_TYPE.NET;
        }else{
            if ( !(dataSource.contains(ActionDefine.SDCARD1) || dataSource.contains(ActionDefine.SDCARD) ) ) {
                dataSource = ActionDefine.wrapRootDir(dataSource);
            }
        }
        switch (dataSourceType) {
            case SDCARD:
                File file = new File(dataSource);
                FileInputStream fis = new FileInputStream(file);
                mp.setDataSource(fis.getFD());
                break;
            case ASSETS:
                AssetManager assetMg = mContext.getAssets();
                AssetFileDescriptor fileDescriptor = assetMg.openFd(dataSource);
                mp.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                break;
            case NET:
                mp.setDataSource(dataSource);
                break;
        }
    }

    @Override
    public void play(String ds) throws Exception {
        if(mp!=null) {
            stop();
            setDataSource(ds);
            mp.prepareAsync();
            mp.setOnPreparedListener(this);
        }
    }

    public void stop() {
        if (mp != null) {
            mp.stop();
            mp.reset();
            mp.setLooping(isLopping);
        }
    }

    public void release() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }
//
    public void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }
//
    public void resume() {
        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }
//


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if(mOnPreparedListener!=null){
            mOnPreparedListener.onPrepared(mp);
        }
    }
}
