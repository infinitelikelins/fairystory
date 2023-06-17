package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class ErrorParallelCardPopup extends AbsBasePopup {

    public ErrorParallelCardPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewInflated() {
        setWidth(800);
        withClick(R.id.error_root_parallel, null);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_error_parallel;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_warning1.mp3");
    }

    @Override
    protected void onPopupDismiss() {
        super.onPopupDismiss();
        MusicUtil.stopMusic();
    }
}
