package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.bearya.robot.fairystory.ui.res.CardType;
import com.bearya.robot.fairystory.ui.res.Command;
import com.bearya.robot.fairystory.ui.res.HistoryRecord;
import com.bearya.robot.fairystory.walk.car.LoadMgr;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultSuccessPopup extends AbsBasePopup {

    public ResultSuccessPopup(Context context, @NonNull List<CardParentAction> data) {
        super(context);

        // 路径指令的总数
        AtomicInteger pathCount = new AtomicInteger(0);
        // 道具指令的总数
        AtomicInteger propCount = new AtomicInteger(0);
        // 函数指令的总数
        AtomicInteger functionCount = new AtomicInteger(0);

        if (data.size() > 0) {
            for (CardParentAction parentAction : data) {
                if (parentAction != null && parentAction.parentActionId == CardType.ACTION_FUNCTION_CALL &&
                        parentAction.childAction != null) {
                    functionCount.incrementAndGet();
                }

                if (parentAction != null && parentAction.parentActionId != CardType.ACTION_FUNCTION_CALL
                        && parentAction.parentActionId != CardType.ACTION_DEFAULT) {
                    pathCount.incrementAndGet();
                }

                // 当道具指令不为空 并且 道具指令不为默认空卡片, 总数+1
                if (parentAction != null && parentAction.parentActionId == CardType.ACTION_FORWARD &&
                        parentAction.childAction != null && parentAction.childAction.childActionId != CardType.ACTION_DEFAULT) {
                    propCount.incrementAndGet();
                }
            }
        }

        int path = pathCount.get();
        int prop = propCount.get();
        int function = functionCount.get();
        int station = LoadMgr.getInstance().getStationLoadNumber();

        // 道具指令
        ((TextView) findViewById(R.id.prop_count)).setText(String.valueOf(prop));

        // 路径指令
        ((TextView) findViewById(R.id.path_count)).setText(String.valueOf(path));

        // 函数指令
        ((TextView) findViewById(R.id.function_count)).setText(String.valueOf(function));

        // 创想模块
        ((TextView) findViewById(R.id.station_count)).setText(String.valueOf(station));

        // 投屏告知终点结果的数据
        BaseApplication.sendAction(Command.FairyStoryResult(prop, path));

        // 本地记录下道具指令和路径指令数据
        HistoryRecord.getInstance().writeValue(path, prop, station);

    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_result_success_view;
    }

    @Override
    protected void onViewInflated() {

    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("tts/zh/gold_effect.mp3", mediaPlayer -> {
            showResultData();
            setPerformMode();
        });
    }

    @Override
    protected int onPopupShowDuration() {
        return 100;
    }

    @Override
    protected int onPopupDismissDuration() {
        return 100;
    }

    private void setPerformMode() {
        findViewById(R.id.exit_game).setVisibility(View.VISIBLE);
        findViewById(R.id.update_controller).setVisibility(View.VISIBLE);
        findViewById(R.id.save_game).setVisibility(View.VISIBLE);
    }

    private void showResultData() {
        // 动画作用的Root布局
        ConstraintLayout constraintLayout = findViewById(R.id.popup_result_success_view);

        // 动画集合,提取Root布局的约束信息，放到ConstraintSet中
        ConstraintSet set1 = new ConstraintSet();
        set1.clone(constraintLayout);

        // 提供结束的时候动画的参数
        ConstraintSet set2 = new ConstraintSet();
        set2.clone(getContext(), R.layout.popup_result_success_view_set);

        // 动画动作
        TransitionSet transitionSet = new TransitionSet();
        // 淡入动画效果
        Fade fadeIn = new Fade(Fade.IN);
        fadeIn.setDuration(1000);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setStartDelay(1000);
        // 添加到动画的集合中
        transitionSet.addTransition(fadeIn);

        // 位移动作
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.addTarget(R.id.ic_light);
        changeBounds.setDuration(1000);
        changeBounds.setInterpolator(new LinearInterpolator());
        // 添加到动画集合中
        transitionSet.addTransition(changeBounds);
        // 动画执行的顺序
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        // 动画的时间插值器
        transitionSet.setInterpolator(new LinearInterpolator());
        // 动画应用
        TransitionManager.beginDelayedTransition(constraintLayout, transitionSet);

        // 最终的动画结果推送到布局中
        set2.applyTo(constraintLayout);
    }

    public void withEvent(final View.OnClickListener exitGame, final View.OnClickListener updateController, final View.OnClickListener saveGame) {
        // 退出游戏
        withClick(R.id.exit_game, exitGame);
        // 优化指令
        withClick(R.id.update_controller, updateController);
        // 保存创作
        withClick(R.id.save_game, saveGame, false);
    }

}