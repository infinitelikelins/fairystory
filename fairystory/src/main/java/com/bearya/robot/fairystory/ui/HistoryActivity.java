package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.adapter.HistoryAdapter;
import com.bearya.robot.fairystory.ui.popup.impl.DeleteConfirmPopup;
import com.bearya.robot.fairystory.ui.res.HistoryRecord;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.Objects;

/**
 * 童话世界的地垫行走历史记录保存
 */
public class HistoryActivity extends BaseActivity implements BaseQuickAdapter.OnItemLongClickListener,
        BaseQuickAdapter.OnItemChildClickListener {

    private HistoryAdapter historyAdapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HistoryActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MusicUtil.stopBgMusic();
        MusicUtil.stopMusic();

        withClick(R.id.btnBack, view -> finish());

        RecyclerView historyRecyclerView = findViewById(R.id.history_list);
        historyRecyclerView.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        historyAdapter = new HistoryAdapter();
        historyAdapter.setOnItemLongClickListener(this);
        historyAdapter.setOnItemChildClickListener(this);
        historyAdapter.bindToRecyclerView(historyRecyclerView);
        historyAdapter.setEmptyView(R.layout.empty_history);
        historyAdapter.isUseEmpty(true);

        String[] keys = HistoryRecord.getInstance().allKey();
        if (keys != null && keys.length > 0) {
            int length = keys.length;
            for (int i = length - 1; i >= 0; i--) {
                historyAdapter.addData(HistoryRecord.getInstance().getItem(keys[i]));
            }
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        deleteHistoryWalk(position);
        return true;
    }

    /**
     * 删除历史的行走记录
     */
    private void deleteHistoryWalk(final int position) {
        DeleteConfirmPopup popup = new DeleteConfirmPopup(this);
        popup.applyShowTips(getString(R.string.delete_work));
        popup.withConfirm(v -> {
            HistoryRecord.getInstance().remove(String.valueOf(Objects.requireNonNull(historyAdapter.getItem(position)).getId()));
            historyAdapter.remove(position);
        }, null);
        popup.showPopupWindow();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, final View view, int position) {
        TellStoryActivity.start(this, historyAdapter.getItem(position));
    }

}