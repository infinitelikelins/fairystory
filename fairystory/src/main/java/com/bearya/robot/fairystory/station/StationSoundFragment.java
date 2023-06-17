package com.bearya.robot.fairystory.station;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.musicplayer.IPlayer;
import com.bearya.robot.base.musicplayer.LocalMusicPlayer;
import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.qdreamer.QdreamerAudio;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class StationSoundFragment extends BaseFragment implements MediaPlayer.OnCompletionListener {
    private static final int REQUEST_CODE_SOUND = 1001;
    private static String FolderPath;

    private String path;
    private TextView tvTimerRecord;
    private TextView tvTimerPreview;
    private ImageView ivPreView;
    private int longTime;
    private long previewLongTime;
    private Handler handler = new Handler();

    private Runnable recordTimerRunable = new Runnable() {
        @Override
        public void run() {
            longTime++;
            previewLongTime = longTime;
            tvTimerRecord.setText(String.format("录音中\n%d S", longTime));
            handler.postDelayed(this, 1000);
        }
    };

    private Runnable previewTimerRunable = new Runnable() {
        @Override
        public void run() {
            previewLongTime--;
            tvTimerPreview.setText(String.format("播放中\n%d S", previewLongTime));
            if (previewLongTime > 0) {
                handler.postDelayed(this, 1000);
            } else {
                stopPlayRecourd();
            }

        }
    };
    private IPlayer mPlayer;
    private ImageView ivSound;
    private AppCompatTextView btnSoundLib;

    public static StationSoundFragment newInstance() {
        return new StationSoundFragment();
    }

    public static void initFolderPath(Context context) {
        File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (file == null) {
            throw new IllegalStateException("Failed to get external storage files directory");
        } else if (file.exists()) {
            if (!file.isDirectory()) {
                throw new IllegalStateException(file.getAbsolutePath() +
                        " already exists and is not a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: " +
                        file.getAbsolutePath());
            }
        }

        FolderPath = file.getAbsolutePath();
    }

    public static String genFilePath(Context context) {
        if (FolderPath == null) {
            initFolderPath(context);
        }
        return String.format("%s/%d.wav", FolderPath, System.currentTimeMillis());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFolderPath(getActivity().getApplicationContext());
        mPlayer = new LocalMusicPlayer(BaseApplication.getInstance());
        mPlayer.setOnCompletionListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_station_config_sound;
    }

    @Override
    protected void initView(View view) {
        ivSound = view.findViewById(R.id.ivSound);
        ivSound.setOnClickListener(view13 -> {
            view13.setSelected(!view13.isSelected());
            if (view13.isSelected()) {
                startRecord();
            } else {
                stopRecord();
                showLastRecord(path, longTime);
                PlayData station = getStation();
                if (station != null) {
                    station.sound = path;
                    station.soundLongTime = longTime;
                }
                longTime = 0;
            }
        });

        ivPreView = view.findViewById(R.id.ivPreView);
        tvTimerRecord = view.findViewById(R.id.tvSecondRecord);
        tvTimerPreview = view.findViewById(R.id.tvSecondPreview);
        ivPreView.setOnClickListener(view12 -> {
            view12.setSelected(!view12.isSelected());
            if (view12.isSelected()) {
                playRecord();
            } else {
                stopPlayRecourd();
            }
        });

        ivPreView.setOnLongClickListener(view1 -> {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            PlayData station = getStation();
            if (station != null) {
                deleteFile(station.sound);
                station.sound = "";
                station.soundLongTime = 0;
            }
            saveStation();
            ivPreView.setVisibility(View.GONE);
            tvTimerPreview.setText("");
            tvTimerRecord.setText("");
            return true;
        });
        btnSoundLib = view.findViewById(R.id.btnSoundLib);
        btnSoundLib.setOnClickListener(v -> {
            stopPlayRecord1();
            LibActivity.start(getActivity(), REQUEST_CODE_SOUND, false);
        });

    }

    private void showLastRecord(String filePath, long time) {
        if (TextUtils.isEmpty(path)) {
            path = filePath;
        }
        ivPreView.setVisibility(View.VISIBLE);
        tvTimerRecord.setText("");
    }

    @Override
    protected void loadLastStationConfig() {
        PlayData station = getStation();
        if (station != null && !TextUtils.isEmpty(station.sound)) {
            if (station.soundLongTime > 0) {
                previewLongTime = station.soundLongTime;
                showLastRecord(station.sound, station.soundLongTime);
            } else {
                LibItem item = StationsActivity.stationLib.getSoundItemByMp3(station.sound);
                if (item != null) {
                    loadSystemSoundConfig(item);
                }
            }
        }
    }

    private void loadSystemSoundConfig(final LibItem soundItem) {
        try {
            Glide.with(getActivity()).load(ResourceUtil.getMipmapId(soundItem.image)).centerCrop()
                    .bitmapTransform(new GlideCircleTransform(getActivity())).diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ivPreView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivPreView.setVisibility(View.VISIBLE);
        path = "android_asset/" + soundItem.mp3;
    }

    private void stopRecord() {
        QdreamerAudio.getInstance().stop();
        ivPreView.setImageResource(R.drawable.ic_listener_selector);
        handler.removeCallbacks(recordTimerRunable);
    }

    private void startRecord() {
        stopPlayRecourd();
        path = genFilePath(getContext().getApplicationContext());
        QdreamerAudio.getInstance().startRecord(path);
        handler.post(recordTimerRunable);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecord();
        stopPlayRecourd();
    }

    public void onUnselected() {
        stopRecord();
        stopPlayRecourd();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        ivPreView.setSelected(false);
        stopPlayRecord1();
    }

    ObjectAnimator animator;

    private void startRotate() {
        if (animator != null) {
            stopRotate();
        }
        animator = ObjectAnimator.ofFloat(ivPreView, "rotation", 0, 359);
        animator.setDuration(8000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void stopRotate() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        ivPreView.setRotation(0);
    }

    private void stopPlayRecord1() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        stopRotate();
    }

    /**
     * 播放MP3
     */
    private void playRecord() {
        if (mPlayer != null) {
            try {
                mPlayer.play(path);
                startRotate();
            } catch (Exception e) {
                ivPreView.setSelected(false);
                e.printStackTrace();
            }
        }
    }

    private void stopPlayRecourd() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        handler.removeCallbacks(previewTimerRunable);
        ivPreView.setSelected(false);
        stopRotate();
        loadLastStationConfig();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SOUND && resultCode == Activity.RESULT_OK) {
            final LibItem soundItem = data.getParcelableExtra("data");
            PlayData playData = getStation();
            if (playData != null) {
                playData.sound = soundItem.mp3;
                playData.soundLongTime = 0;
                saveStation();
                loadSystemSoundConfig(soundItem);
            }
        }
    }

    private void deleteFile(String sound) {
        if (sound.contains("storage/")) {
            File file = new File(sound);
            file.delete();
        }
    }
}
