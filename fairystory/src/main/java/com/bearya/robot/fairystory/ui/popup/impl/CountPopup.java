package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.view.View;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class CountPopup extends AbsBasePopup {

    private PopupCountClickListener popupCountClickListener;

    public CountPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_count;
    }

    @Override
    protected void onViewInflated() {
        withClick(R.id.count1, v -> countClick(1));
        withClick(R.id.count2, v -> countClick(2));
        withClick(R.id.count3, v -> countClick(3));
        withClick(R.id.count4, v -> countClick(4));
        withClick(R.id.count5, v -> countClick(5));
        withClick(R.id.count6, v -> countClick(6));
        withClick(R.id.count7, v -> countClick(7));
        withClick(R.id.count8, v -> countClick(8));
        withClick(R.id.count9, v -> countClick(9));
        withClick(R.id.count10, v -> countClick(10));
        setWidth(1024);
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_edit.mp3");
    }

    protected final void countClick(int count) {
        if (popupCountClickListener != null) {
            popupCountClickListener.onCountClick(count);
            MusicUtil.playAssetsAudio("card/zh/" + count + ".mp3");
        }
    }

    public void setPopupCountClickListener(PopupCountClickListener popupCountClickListener) {
        this.popupCountClickListener = popupCountClickListener;
    }

    public interface PopupCountClickListener {
        void onCountClick(int count);
    }

}
