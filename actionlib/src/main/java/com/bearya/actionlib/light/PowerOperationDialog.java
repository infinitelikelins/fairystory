package com.bearya.actionlib.light;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.bearya.actionlib.R;
import com.bearya.actionlib.constants.RobotConstants;
import com.bearya.actionlib.ui.BaseDialog;
import com.bearya.actionlib.utils.RobotActionManager;
import com.bearya.actionlib.utils.SystemUtil;

/**
 * 电量过低dialog弹出框
 * created by lianweidog on 2017-6-14
 */
public class PowerOperationDialog extends BaseDialog {
    private Context mContext;

    public PowerOperationDialog(Context context) {
        super(context, R.layout.dialog_power_operation, true);
        mContext = context;
    }

    @Override
    protected void initSubView() {
        LinearLayout close = (LinearLayout) findViewById(R.id.ll_power_operation_close);
        LinearLayout reboot = (LinearLayout) findViewById(R.id.ll_power_operation_reboot);
        LinearLayout cancel = (LinearLayout) findViewById(R.id.ll_power_operation_cancel);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SystemUtil.shutDown(mContext);
                RobotActionManager.send(RobotConstants.Robot_Close);
            }
        });
        reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemUtil.reboot(mContext);
//                RobotActionManager.send(RobotConstants.Robot_Reboot);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}