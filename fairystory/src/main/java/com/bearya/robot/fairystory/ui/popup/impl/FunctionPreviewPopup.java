package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.adapter.PreviewCardAdapter;
import com.bearya.robot.fairystory.ui.popup.AbsBasePopup;
import com.bearya.robot.fairystory.ui.res.CardParentAction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FunctionPreviewPopup extends AbsBasePopup {

    private final String functionKey;

    private PreviewCardAdapter adapter;

    public FunctionPreviewPopup(Context context, int defineFunctionType) {
        super(context);
        functionKey = "DEFINE_FUNCTION_" + defineFunctionType;
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_function_preview;
    }

    @Override
    protected void onViewInflated() {
        setWidth(1024);

        RecyclerView cardActionRecyclerView = findViewById(R.id.preview_action);
        cardActionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new PreviewCardAdapter();
        adapter.setFooterViewAsFlow(false);
        adapter.isFirstOnly(true);
        adapter.bindToRecyclerView(cardActionRecyclerView);
        adapter.isUseEmpty(true);
        adapter.setEmptyView(R.layout.popup_undefine);

    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        String functionData = SharedPreferencesUtil.getInstance(getContext()).getString(functionKey);
        if (!TextUtils.isEmpty(functionData)) {
            Type type = new TypeToken<List<CardParentAction>>() {
            }.getType();
            adapter.setNewData((new Gson().fromJson(functionData, type)));
        } else {
            adapter.setNewData(new ArrayList<>());
        }
    }

    public void withDefine(View.OnClickListener onDefineClickListener) {
        withClick(R.id.define_function, onDefineClickListener);
    }

}