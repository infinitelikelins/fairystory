package com.bearya.robot.base.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.R;
import com.bearya.robot.base.ui.view.RippleImageView;
import com.bearya.robot.base.util.DeviceUtil;
import com.bearya.robot.base.util.MusicUtil;

public abstract class BaseLauncherActivity extends BaseActivity implements View.OnClickListener {

    private RippleImageView rippleImageView;
    private final Handler mHandler = new Handler();
    private ObjectAnimator translationXAnimator;
    private ObjectAnimator rotationAnimator;
    private ObjectAnimator scaleXAnimator;
    private ObjectAnimator scaleYAnimator;

    private final Runnable stopWaveAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            rippleImageView.stopWaveAnimation();
        }
    };

    private boolean flag;
    private LauncherData launcherData;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        AppCompatImageView bgView = findViewById(R.id.bgView);
        AppCompatImageView mFingerView = findViewById(R.id.fingerView);
        AppCompatTextView record = findViewById(R.id.record);
        AppCompatTextView casting = findViewById(R.id.casting);
        rippleImageView = findViewById(R.id.rippleImageView);

        launcherData = getLauncherData();

        bgView.setImageResource(launcherData.bg);
        bgView.setOnClickListener(this);

        if (isShowHistoryRecord()) {
            record.setVisibility(View.VISIBLE);
            record.setOnClickListener(this::onRecordClick);
        } else {
            record.setVisibility(View.GONE);
        }

        if (isShowBluetooth()) {
            casting.setVisibility(View.VISIBLE);
            casting.setOnClickListener(view -> startBluetoothCasting());
        } else {
            casting.setVisibility(View.GONE);
        }

        translationXAnimator = ObjectAnimator.ofFloat(mFingerView, "translationX", 0, -60);
        translationXAnimator.setDuration(1000);
        translationXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        translationXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        rotationAnimator = ObjectAnimator.ofFloat(mFingerView, "rotationX", 0, 30);
        rotationAnimator.setDuration(1000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        scaleXAnimator = ObjectAnimator.ofFloat(mFingerView, "scaleX", 1.0f, 0.8f);
        scaleXAnimator.setDuration(1000);
        scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleXAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        scaleYAnimator = ObjectAnimator.ofFloat(mFingerView, "scaleY", 1.0f, 0.8f);
        scaleYAnimator.setDuration(1000);
        scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleYAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        scaleXAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                flag = !flag;
                if (flag) {
                    rippleImageView.startWaveAnimation();
                    mHandler.postDelayed(stopWaveAnimationRunnable, 1000);
                }
            }


        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        translationXAnimator.start();
        rotationAnimator.start();
        scaleXAnimator.start();
        scaleYAnimator.start();

        BaseApplication.getInstance().getHandler().postDelayed(() ->
                MusicUtil.playAssetsAudio(launcherData.bgMp3,
                        mp -> MusicUtil.playAssetsBgMusic(launcherData.tipMp3)), 500);

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(String.format("V %s", DeviceUtil.getVersionName(getApplicationContext())));
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, launcherData.jumpToActivity));
        release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        translationXAnimator.pause();
        rotationAnimator.pause();
        scaleXAnimator.pause();
        scaleYAnimator.pause();
    }

    private void release() {
        translationXAnimator.cancel();
        rotationAnimator.cancel();
        scaleXAnimator.cancel();
        scaleYAnimator.cancel();
        stopWaveAnimationRunnable.run();
        mHandler.removeCallbacks(stopWaveAnimationRunnable);
        MusicUtil.stopBgMusic();
        MusicUtil.stopMusic();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void onBackClicked(View view) {
        BaseApplication.getInstance().release();
        release();
    }

    protected abstract LauncherData getLauncherData();

    /**
     * 历史记录点击
     */
    protected abstract void onRecordClick(View view);

    /**
     * 是否显示历史记录的按钮
     */
    protected abstract boolean isShowHistoryRecord();

    /**
     * 是否显示蓝牙投屏的按钮
     */
    protected abstract boolean isShowBluetooth();

    /**
     * 点击后需要连接蓝牙投屏功能
     */
    protected abstract void startBluetoothCasting();

}