package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class ResultFailPopup extends AbsBasePopup {

    private final String errorMp3;

    public ResultFailPopup(Context context, String errorMessage, String errorMp3) {
        super(context);
        // 错误信息提示
        ((TextView) findViewById(R.id.error_text)).setText(errorMessage);
        this.errorMp3 = errorMp3;
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_result_view;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        if (!TextUtils.isEmpty(errorMp3) && errorMp3.endsWith(".mp3")) {
            MusicUtil.playAssetsAudio(errorMp3);
        }
    }

    public void withEvent(final View.OnClickListener updateCardController, final View.OnClickListener backHome) {
        withClick(R.id.update_card_controller, updateCardController);
        withClick(R.id.back_home, backHome);
    }

}