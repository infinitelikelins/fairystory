package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.ui.view.FrameSurfaceView;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceAudio;
import com.bearya.robot.fairystory.ui.res.IntroduceImageArrays;
import com.bearya.robot.fairystory.ui.res.IntroduceTime;
import com.bearya.robot.fairystory.ui.res.ThemeConfig;

/**
 * 场景动画 起始背景介绍
 */
public class ThemeIntroduceActivity extends BaseActivity {

    private FrameSurfaceView frameSurfaceView;
    private boolean isFrameFinishedFlag = false; // 帧动画播放是否结束
    private boolean isMusicFinishedFlag = false; // 背景音乐播放是否结束

    public static void start(Context context, String type) {
        context.startActivity(new Intent(context, ThemeIntroduceActivity.class).putExtra("type", type));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_introduce);

        // 获取播放主题的资源类型
        final String types = getIntent().getStringExtra("type");

        BaseApplication.sendAction(playFrameAction(types));

        frameSurfaceView = findViewById(R.id.frame_surface_view);

        // 设置基本需要播放图片资源内容
        frameSurfaceView.setBitmapResoursID(playStartImageArrays(types));
        // 重复播放 ：false
        frameSurfaceView.setIsRepeat(false);
        // 动画播放的间隔
        frameSurfaceView.setGapTime(playFrameAudioGapTime(types));
        // 动画播放开始和结束事件
        frameSurfaceView.setOnFrameFinishedListener(new FrameSurfaceView.OnFrameFinishedListener() {
            @Override
            public void onFrameStart() {
                MusicUtil.playAssetsAudio(playStartAudioRes(types), mediaPlayer -> {
                    isMusicFinishedFlag = true;
                    if (isFrameFinishedFlag) {
                        CardControllerActivity.start(ThemeIntroduceActivity.this, null);
                        finish();
                    }
                });
            }

            @Override
            public void onFrameFinish() {
                isFrameFinishedFlag = true;
                if (isMusicFinishedFlag) {
                    CardControllerActivity.start(ThemeIntroduceActivity.this, null);
                    finish();
                }
            }
        });

        // 跳过场景介绍动画
        withClick(frameSurfaceView, view -> {
            CardControllerActivity.start(ThemeIntroduceActivity.this, null);
            finish();
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        frameSurfaceView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        frameSurfaceView.stop();
        MusicUtil.stopMusic();
    }

    /**
     * 点击屏幕上的游戏关卡，选择播放的动画
     *
     * @param type 点击游戏关卡类型
     */
    private int[] playStartImageArrays(String type) {
        switch (type) {
            case ThemeConfig.THEME_YXWH: return IntroduceImageArrays.fairStartImages;
            case ThemeConfig.THEME_QHXB: return IntroduceImageArrays.seafloorStartImages;
            case ThemeConfig.THEME_MHWH: return IntroduceImageArrays.danceStartImages;
            default: return new int[]{};
        }
    }

    /**
     * 播放的背景配音
     *
     * @param type 点击游戏关卡类型
     */
    private String playStartAudioRes(String type) {
        switch (type) {
            case ThemeConfig.THEME_YXWH: return IntroduceAudio.hero;
            case ThemeConfig.THEME_QHXB: return IntroduceAudio.treasure;
            case ThemeConfig.THEME_MHWH: return IntroduceAudio.ball;
            default: return IntroduceAudio.def;
        }
    }

    /**
     * 播放的背景配音适配的时间
     *
     * @param type 点击游戏关卡类型
     */
    private int playFrameAudioGapTime(String type) {
        switch (type) {
            case ThemeConfig.THEME_YXWH: return IntroduceTime.heroStartTime;
            case ThemeConfig.THEME_QHXB: return IntroduceTime.treasureStartTime;
            case ThemeConfig.THEME_MHWH: return IntroduceTime.ballStartTime;
            default: return IntroduceTime.def;
        }
    }

    /**
     * @param type 点击游戏关卡类型
     */
    private String playFrameAction(String type) {
        switch (type) {
            case ThemeConfig.THEME_YXWH: return Command.FairyStoryHero;
            case ThemeConfig.THEME_QHXB: return Command.FairyStoryTreasure;
            case ThemeConfig.THEME_MHWH: return Command.FairyStoryBall;
            default: return Command.def;
        }
    }

}