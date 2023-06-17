package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.CardPopup;
import com.bearya.robot.fairystory.ui.res.CardType;

public class DefineFunctionCardPopup extends CardPopup {

    public DefineFunctionCardPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_define_function_card;
    }

    @Override
    protected void onViewInflated() {
        withClick(R.id.upward, view -> popupWithClick(CardType.ACTION_FORWARD));
        withClick(R.id.downward, view -> popupWithClick(CardType.ACTION_BACKWARD));
        withClick(R.id.left, view -> popupWithClick(CardType.ACTION_LEFT));
        withClick(R.id.right, view -> popupWithClick(CardType.ACTION_RIGHT));
        setWidth(1024);
    }

    @Override
    protected boolean allowOidRange(int oid) {
        return oid == CardType.ACTION_FORWARD || oid == CardType.ACTION_BACKWARD ||
                oid == CardType.ACTION_LEFT || oid == CardType.ACTION_RIGHT;
    }

}