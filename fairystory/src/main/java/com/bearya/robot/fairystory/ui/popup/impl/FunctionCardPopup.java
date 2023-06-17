package com.bearya.robot.fairystory.ui.popup.impl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.popup.CardPopup;
import com.bearya.robot.fairystory.ui.res.CardType;

import java.util.HashMap;
import java.util.Map;

public class FunctionCardPopup extends CardPopup {

    public FunctionCardPopup(Context context) {
        super(context);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.popup_function;
    }

    @Override
    protected void onViewInflated() {
        withClick(R.id.function1, v -> popupWithClick(CardType.ACTION_FUNCTION_1));
        withClick(R.id.function2, v -> popupWithClick(CardType.ACTION_FUNCTION_2));
        withClick(R.id.function3, v -> popupWithClick(CardType.ACTION_FUNCTION_3));
        withClick(R.id.function4, v -> popupWithClick(CardType.ACTION_FUNCTION_4));
        withClick(R.id.function5, v -> popupWithClick(CardType.ACTION_FUNCTION_5));
        withClick(R.id.function6, v -> popupWithClick(CardType.ACTION_FUNCTION_6));
        withClick(R.id.function7, v -> popupWithClick(CardType.ACTION_FUNCTION_7));
        withClick(R.id.function8, v -> popupWithClick(CardType.ACTION_FUNCTION_8));
        withClick(R.id.function9, v -> popupWithClick(CardType.ACTION_FUNCTION_9));
        withClick(R.id.function10, v -> popupWithClick(CardType.ACTION_FUNCTION_10));
        setWidth(1200);
    }

    @Override
    protected boolean allowOidRange(int oid) {
        return oid == CardType.ACTION_FUNCTION_1 || oid == CardType.ACTION_FUNCTION_2 ||
                oid == CardType.ACTION_FUNCTION_3 || oid == CardType.ACTION_FUNCTION_4 ||
                oid == CardType.ACTION_FUNCTION_5 || oid == CardType.ACTION_FUNCTION_6 ||
                oid == CardType.ACTION_FUNCTION_7 || oid == CardType.ACTION_FUNCTION_8 ||
                oid == CardType.ACTION_FUNCTION_9 || oid == CardType.ACTION_FUNCTION_10;
    }

    @Override
    protected void onPopupShow() {
        super.onPopupShow();
        MusicUtil.playAssetsAudio("card/zh/p_select_fun.mp3");
        new Thread(() -> {
            HashMap<String, String> hashMap = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                String key = "DEFINE_FUNCTION_" + (CardType.ACTION_FUNCTION_1 + i);
                String string = SharedPreferencesUtil.getInstance(getContext()).getString(key);
                hashMap.put("function_selected_" + (i + 1), string);
            }
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                getContext().runOnUiThread(() -> {
                    View view = findViewById(ResourceUtil.getId(getContext(), entry.getKey()));
                    String value = entry.getValue();
                    boolean empty = TextUtils.isEmpty(value) || "[]".equals(value);
                    view.setVisibility(empty ? View.GONE : View.VISIBLE);
                });
            }
        }).start();
    }
}