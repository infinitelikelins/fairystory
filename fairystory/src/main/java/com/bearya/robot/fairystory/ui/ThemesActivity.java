package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.play.AnimationsContainer;
import com.bearya.robot.base.play.FacePlay;
import com.bearya.robot.base.play.FaceType;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.ui.view.FrameSurfaceView;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.station.StationsActivity;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.IntroduceAudio;
import com.bearya.robot.fairystory.ui.res.StageImage;
import com.bearya.robot.fairystory.ui.res.ThemeConfig;
import com.bearya.robot.fairystory.walk.car.LoadMgr;

import java.util.concurrent.TimeUnit;

/**
 * 主题乐园,在这里可以选择不同的关卡进行游戏
 */
public class ThemesActivity extends BaseActivity implements View.OnClickListener {

    private FrameSurfaceView frameSurfaceView;
    private rx.Subscription subscribe;
    private boolean singleClickLock = false;
    private ImageView stationEntryView;
    private AnimationsContainer.FramesSequenceAnimation animation;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ThemesActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        frameSurfaceView = findViewById(R.id.frame_surface);
        frameSurfaceView.setIsRepeat(true);
        frameSurfaceView.setGapTime(10);

        frameSurfaceView.setBitmapResoursID(StageImage.stageImages);

        for (int i = 1; i <= 3; i++) {
            withClick(ResourceUtil.getId(getApplicationContext(), "view" + i), this);
        }

        stationEntryView = findViewById(R.id.stationEntryView);

        withClick(stationEntryView, v -> startGame(ThemeConfig.THEME_CXTD));
        withClick(R.id.my_working, v -> HistoryActivity.start(getApplicationContext()));

    }

    @Override
    public void onClick(View v) {

        if (singleClickLock) {
            return;
        }
        singleClickLock = true;

        // 开场介绍资源类型
        final String type = (String) v.getTag();
        // 点击后播放点击的主题
        Pair<String, String> themeConfig =
                ThemeConfig.THEME_MHWH.equals(type) ? new Pair<>(IntroduceAudio.THEME_BALL_CLICK_AUDIO, Command.FairyStoryBallAudio) :
                        ThemeConfig.THEME_YXWH.equals(type) ? new Pair<>(IntroduceAudio.THEME_HERO_CLICK_AUDIO, Command.FairyStoryHeroAudio) :
                                ThemeConfig.THEME_QHXB.equals(type) ? new Pair<>(IntroduceAudio.THEME_TREASURE_CLICK_AUDIO, Command.FairyStoryTreasureAudio) :
                                        new Pair<>(IntroduceAudio.THEME_BALL_CLICK_AUDIO, Command.FairyStoryBallAudio);

        BaseApplication.sendAction(themeConfig.second);

        MusicUtil.playAssetsAudio(themeConfig.first, mediaPlayer -> startGame(type));

    }

    @Override
    protected void onResume() {
        super.onResume();

        BaseApplication.getInstance().getHandler().postDelayed(() -> MusicUtil.playAssetsAudio("music/zh/welcome.mp3", mediaPlayer -> {
            MusicUtil.playAssetsAudio("music/zh/delay.mp3");
            MusicUtil.playAssetsBgMusic("music/zh/theme.mp3");
        }), 500);
        frameSurfaceView.start();
        BaseApplication.sendAction(Command.FairyStoryTheme);
        startAnimation(new FacePlay(String.valueOf(R.array.station_entry), FaceType.Arrays), stationEntryView);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        singleClickLock = false;

        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
            subscribe = null;
        }

        subscribe = rx.Observable.interval(20, 20, TimeUnit.SECONDS)
                .subscribeOn(rx.schedulers.Schedulers.newThread())
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(aLong -> MusicUtil.playAssetsAudio("music/zh/delay.mp3"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        singleClickLock = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        frameSurfaceView.stop();
        animation.stop();

        MusicUtil.stopBgMusic();

        if (subscribe != null && !subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }

    }

    /**
     * 启动游戏介绍页面
     *
     * @param type 开场介绍资源类型
     * @see ThemeConfig
     */
    private void startGame(String type) {

        singleClickLock = false;

        ThemeConfig.CURRENT_THEME = type;
        LoadMgr.getInstance().setThemeEndLoad(type);
        if (ThemeConfig.THEME_CXTD.equals(ThemeConfig.CURRENT_THEME)) {
            StationsActivity.start(this);
        } else {
            ThemeIntroduceActivity.start(this, type);
        }

    }

    private void startAnimation(FacePlay facePlay, ImageView view) {
        int array = Integer.parseInt(facePlay.getFace());
        animation = AnimationsContainer.getInstance(array, 200).createProgressDialogAnim(view);
        animation.setRepair(true);
        animation.start();
        animation.setOnAnimStopListener(null);
    }

}