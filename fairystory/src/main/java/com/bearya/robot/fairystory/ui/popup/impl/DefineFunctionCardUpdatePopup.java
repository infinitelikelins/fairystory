package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.view.View;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.CardPopup;
import com.bearya.robot.fairystory.ui.res.CardType;

public class DefineFunctionCardUpdatePopup extends CardPopup {

    private boolean allowParallelCardOId = true;

    public DefineFunctionCardUpdatePopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_define_function_card_update;
    }

    @Override
    protected void onViewInflated() {
        withClick(R.id.forward, v -> popupWithClick(CardType.ACTION_FORWARD));
        withClick(R.id.right, v -> popupWithClick(CardType.ACTION_RIGHT));
        withClick(R.id.left, v -> popupWithClick(CardType.ACTION_LEFT));
        withClick(R.id.backward, v -> popupWithClick(CardType.ACTION_BACKWARD));
        withClick(R.id.left_add, v -> popupWithClick(CardType.ACTION_INSERT_LEFT));
        withClick(R.id.right_add, v -> popupWithClick(CardType.ACTION_INSERT_RIGHT));
        withClick(R.id.other, v -> popupWithClick(CardType.ACTION_PARALLEL));
        setWidth(1024);
    }

    public final void hideParallelCard(){
        allowParallelCardOId = false;
        findViewById(R.id.other).setVisibility(View.GONE);
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_edit.mp3");
    }

    @Override
    protected boolean allowOidRange(int oid) {
        return oid == CardType.ACTION_FORWARD || oid == CardType.ACTION_BACKWARD
                || oid == CardType.ACTION_LEFT || oid == CardType.ACTION_RIGHT
                || oid == CardType.ACTION_INSERT_LEFT || oid == CardType.ACTION_INSERT_RIGHT
                || (allowParallelCardOId && oid == CardType.ACTION_PARALLEL);
    }

}