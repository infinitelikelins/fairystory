package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class ErrorPropCardPopup extends AbsBasePopup {
    public ErrorPropCardPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_error_prop_card;
    }

    @Override
    protected void onViewInflated() {
        setWidth(650);
        withClick(R.id.error_root_prop, null);
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_warning2.mp3");
    }

    @Override
    protected void onPopupDismiss() {
        super.onPopupDismiss();
        MusicUtil.stopMusic();
    }
}
