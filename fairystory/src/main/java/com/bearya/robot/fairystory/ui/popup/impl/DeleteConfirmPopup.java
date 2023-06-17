package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class DeleteConfirmPopup extends AbsBasePopup {

    private String popupShowAudio;

    public DeleteConfirmPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_delete;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        if (!TextUtils.isEmpty(popupShowAudio)) {
            MusicUtil.playAssetsAudio(popupShowAudio);
        }
    }

    @Override
    protected void onViewInflated() {
        setWidth(800);
    }

    public final void applyShowAudio(String popupShowAudio) {
        if (popupShowAudio != null) {
            this.popupShowAudio = popupShowAudio;
        }
    }

    public final void applyShowTips(String tips) {
        ((TextView) findViewById(R.id.del_tips)).setText(tips);
    }

    public final void withConfirm(final View.OnClickListener deleteListener, View.OnClickListener cancelListener) {
        withClick(R.id.cancel, cancelListener);
        withClick(R.id.delete, v -> {
            if (deleteListener != null) {
                deleteListener.onClick(v);
                MusicUtil.playAssetsAudio("card/zh/p_delete.mp3");
            }
        });
    }

}