package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.annotation.NonNull;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;

import java.util.Locale;

public class ActionSetDialog extends Dialog implements View.OnClickListener {

    public static final int ACTION_DOUBLE_HAND = 1;
    public static final int ACTION_LEFT_HAND = 2;
    public static final int ACTION_RIGHT_HAND = 3;
    public static final int ACTION_SHAKE_HEADER = 4;
    public static final int ACTION_SHAKE_HEADER_TO_LEFT = 5;
    public static final int ACTION_SHAKE_HEADER_TO_RIGHT = 6;

    private ActionSelectListener listener;
    private boolean isSelect;

    public ActionSetDialog(@NonNull Activity activity) {
        super(activity, R.style.FullScreenDialog);
        setContentView(R.layout.dialog_action_set);
        setCanceledOnTouchOutside(true);

        for (int i = 1; i <= 6; i++) {
            View view = findViewById(ResourceUtil.getId(getContext(), "cardView" + i));
            view.setTag(i);
            view.setOnClickListener(this);
        }
        MusicUtil.playAssetsAudio("station/zh/station_select_action.mp3");
        setOnDismissListener(dialog -> {
            if (!isSelect) {
                MusicUtil.stopMusic();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int action = (int) view.getTag();
        MusicUtil.playAssetsAudio(String.format(Locale.CHINA, "station/zh/station_action_%d.mp3", action));
        isSelect = true;
        if (listener != null) {
            listener.onActionSelected(action);
        }
        dismiss();
    }

    public void setListener(ActionSelectListener listener) {
        this.listener = listener;
    }

    public interface ActionSelectListener {
        void onActionSelected(int action);
    }

}