package com.bearya.robot.base.ui;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;

import com.bearya.robot.base.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by yexifeng on 17/3/12.
 */

public class DialogVoice extends BaseDialog {
    private DiscreteSeekBar discreteSeekBar;
    private AudioManager audioManager;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (DialogVoice.this.isShowing())
                DialogVoice.this.dismiss();//关闭dialog
        }
    };
    public DialogVoice(Context context) {
        super(context, R.layout.activity_voice, true);
    }

    @Override
    protected void initSubView() {
        discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.seting_voice_seekbar);
        audioManager = (AudioManager) getContext().getSystemService(Service.AUDIO_SERVICE);

        discreteSeekBar.setMin(0);
        discreteSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        discreteSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        getCallVolume(seekBar.getProgress(),
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)), 0);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);//重新计时
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }
    public void refreshProgress(){
        discreteSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 3000);//重新计时
    }

    @Override
    public void show() {
        super.show();
        handler.postDelayed(runnable, 3000);//三秒后执行
    }


    public static int getCallVolume(int musicVolume, int maxMusicVolume, int maxCallVolume) {
        int callVolume = 1;
        if (musicVolume > 0 && maxMusicVolume > 0) {
            callVolume = musicVolume * maxCallVolume / maxMusicVolume;
        }
        return callVolume > 0 ? callVolume : 1;
    }
}
