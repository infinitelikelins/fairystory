package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.CardType;

import java.util.HashMap;
import java.util.Map;

public class FunctionActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, FunctionActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_function);

        withClick(R.id.btnBack, view -> finish());
        withClick(R.id.function1, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_1));
        withClick(R.id.function2, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_2));
        withClick(R.id.function3, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_3));
        withClick(R.id.function4, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_4));
        withClick(R.id.function5, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_5));
        withClick(R.id.function6, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_6));
        withClick(R.id.function7, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_7));
        withClick(R.id.function8, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_8));
        withClick(R.id.function9, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_9));
        withClick(R.id.function10, view -> DefineFunctionActivity.start(this, CardType.ACTION_FUNCTION_10));

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            HashMap<String, String> hashMap = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                String key = "DEFINE_FUNCTION_" + (CardType.ACTION_FUNCTION_1 + i);
                String string = SharedPreferencesUtil.getInstance(getApplicationContext()).getString(key);
                hashMap.put("function_selected_" + (i + 1), string);
            }
            for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                runOnUiThread(() -> {
                    View view = findViewById(ResourceUtil.getId(getApplicationContext(), entry.getKey()));
                    String value = entry.getValue();
                    boolean empty = TextUtils.isEmpty(value) || "[]".equals(value);
                    view.setVisibility(empty ? View.GONE : View.VISIBLE);
                });
            }
        }).start();
    }
}