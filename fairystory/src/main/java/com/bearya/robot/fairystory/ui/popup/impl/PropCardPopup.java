package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.CardPopup;
import com.bearya.robot.fairystory.ui.res.CardType;

public class PropCardPopup extends CardPopup {

    public PropCardPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_prop_card;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_edit.mp3");
    }

    @Override
    protected void onViewInflated() {
        withClick(R.id.prop_boat, v -> popupWithClick(CardType.ACTION_BOAT));
        withClick(R.id.prop_bullet, v -> popupWithClick(CardType.ACTION_BULLET));
        withClick(R.id.prop_flute, v -> popupWithClick(CardType.ACTION_FLUTE));
        withClick(R.id.prop_magic, v -> popupWithClick(CardType.ACTION_MAGIC));
        withClick(R.id.prop_needles, v -> popupWithClick(CardType.ACTION_NEEDLES));
        withClick(R.id.prop_water, v -> popupWithClick(CardType.ACTION_WATER));
        withClick(R.id.prop_soldier, v -> popupWithClick(CardType.ACTION_SOLDIER));
        withClick(R.id.prop_stick, v -> popupWithClick(CardType.ACTION_STICK));
        setWidth(1100);
    }

    @Override
    protected boolean allowOidRange(int oid) {
        return oid >= CardType.ACTION_BOAT && oid <= CardType.ACTION_STICK;
    }

}