package com.bearya.robot.fairystory.ui.adapter;

import androidx.annotation.Nullable;

import com.bearya.robot.fairystory.R;

import com.bearya.robot.fairystory.ui.res.WalkHistory;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class HistoryAdapter extends BaseQuickAdapter<WalkHistory, BaseViewHolder> {

    public HistoryAdapter() {
        super(R.layout.item_history);
    }

    @Override
    protected void convert(BaseViewHolder helper, @Nullable WalkHistory item) {
        helper.setText(R.id.history_name, String.valueOf(item.getId()));
        helper.addOnClickListener(R.id.tell);
    }

}
