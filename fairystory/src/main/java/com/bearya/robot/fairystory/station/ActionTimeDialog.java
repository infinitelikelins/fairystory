package com.bearya.robot.fairystory.station;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;

import java.util.Locale;

public class ActionTimeDialog extends Dialog implements View.OnClickListener {

    private ActionTimeListener listener;

    public ActionTimeDialog(@NonNull Activity activity, int second) {
        super(activity, R.style.FullScreenDialog);
        setContentView(R.layout.dialog_station_times);
        setCanceledOnTouchOutside(true);

        for (int i = 1; i <= 10; i++) {
            TextView view = findViewById(ResourceUtil.getId(getContext(), "tv" + i));
            String tag = (String) view.getTag();
            if (tag.equals(String.valueOf(second))) {
                view.setSelected(true);
            }
            view.setOnClickListener(this);
        }
        MusicUtil.playAssetsAudio("station/zh/station_action_select_time.mp3");

    }

    @Override
    public void onClick(View view) {
        String section = (String) view.getTag();
        MusicUtil.playAssetsAudio(String.format(Locale.CHINA,"station/zh/station_action_time_%s.mp3", section));
        if (listener != null) {
            listener.onActionTime(Integer.parseInt(section));
        }
        dismiss();
    }

    public void setListener(ActionTimeListener listener) {
        this.listener = listener;
    }

    public interface ActionTimeListener {
        void onActionTime(int second);

    }
}
