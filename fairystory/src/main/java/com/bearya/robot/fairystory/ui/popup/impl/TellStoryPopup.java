package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.view.View;

import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;

public class TellStoryPopup extends AbsBasePopup {

    public TellStoryPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_story_finish;
    }

    @Override
    protected void onViewInflated() {
        setWidth(850);
    }

    public final void withConfirm(View.OnClickListener showAgain, View.OnClickListener cancelListener) {
        withClick(R.id.cancel, cancelListener);
        withClick(R.id.show_again, showAgain);
    }

}