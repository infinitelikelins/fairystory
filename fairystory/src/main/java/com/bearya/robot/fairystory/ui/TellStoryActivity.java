package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bearya.robot.base.play.LoadPlay;
import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.base.play.TellStory;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.impl.TellStoryPopup;
import com.bearya.robot.fairystory.ui.res.WalkHistory;

import java.util.List;

/**
 * 讲故事
 */
public class TellStoryActivity extends BaseActivity {

    private WalkHistory config; // 创想地垫的配置信息

    /**
     * 启动讲故事
     */
    public static void start(Context context, WalkHistory item) {
        context.startActivity(new Intent(context, TellStoryActivity.class).putExtra("item", item));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tell_story);

        config = getIntent().getParcelableExtra("item");

        MusicUtil.stopMusic();
        MusicUtil.stopBgMusic();

        final AppCompatImageView backBtn = findViewById(R.id.btnBack);
        withClick(backBtn, view -> finish());

        LottieAnimationView tellStoryLottie = findViewById(R.id.tell_story_lottie_view);
        tellStoryLottie.setOnClickListener(v -> backBtn.setVisibility(backBtn.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        TellStory.getInstance().setView(tellStoryLottie);

        playData();
    }

    /**
     * 讲故事
     */
    private void playData() {
        List<PlayData> playDataArrayList = config.getPlayDataArrayList();
        if (playDataArrayList != null && playDataArrayList.size() > 0) {
            LoadPlay newLoadPlay = new LoadPlay();
            for (PlayData playData : playDataArrayList) {
                if (!playData.isEmpty()) newLoadPlay.addLoad(playData);
            }
            String key = "TellStory";
            TellStory.getInstance().register(key, newLoadPlay);
            TellStory.getInstance().director(key, this::showStoryFinishPopup);
        } else {
            Toast.makeText(getApplicationContext(), "没有可以讲述的故事", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        TellStory.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TellStory.getInstance().release();
    }

    /**
     * 显示故事播放完成后的弹框
     */
    private void showStoryFinishPopup() {
        TellStoryPopup popup = new TellStoryPopup(this);
        popup.withConfirm(v -> playData(), v -> finish());
        popup.showPopupWindow();
    }

}