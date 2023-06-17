package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class EmptyActionPopup extends AbsBasePopup {

    public EmptyActionPopup(Context context) {
        super(context);
    }

    @Override
    protected void onViewInflated() {
        setWidth(850);
        withClick(R.id.empty_action_root, null);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_empty_action;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_warning4.mp3");
    }

    @Override
    protected void onPopupDismiss() {
        super.onPopupDismiss();
        MusicUtil.stopMusic();
    }
}
