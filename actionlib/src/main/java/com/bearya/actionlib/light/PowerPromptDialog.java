package com.bearya.actionlib.light;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.bearya.actionlib.R;
import com.bearya.actionlib.ui.BaseDialog;

public class PowerPromptDialog extends BaseDialog {

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isShowing())
                dismiss();//关闭dialog
        }
    };

    public PowerPromptDialog(Context context) {
        super(context, R.layout.dialog_power_prompt, false);
    }

    @Override
    protected void initSubView() {
        findViewById(R.id.rl_power_prompt_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing())
                    dismiss();//关闭dialog
            }
        });
    }

    @Override
    public void show() {
        super.show();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 7000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
