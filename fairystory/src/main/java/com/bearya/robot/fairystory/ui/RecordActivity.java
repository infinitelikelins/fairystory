package com.bearya.robot.fairystory.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.res.HistoryRecord;

public class RecordActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, RecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newest_record);

        int pathCountValue = HistoryRecord.getInstance().readPathCountValue();
        int propCountValue = HistoryRecord.getInstance().readPropCountValue();
        int stationCountValue = HistoryRecord.getInstance().readStationCountValue();

        ((AppCompatTextView) findViewById(R.id.path_count)).setText(String.valueOf(pathCountValue));
        ((AppCompatTextView) findViewById(R.id.prop_count)).setText(String.valueOf(propCountValue));
        ((AppCompatTextView) findViewById(R.id.station_count)).setText(String.valueOf(stationCountValue));

        findViewById(R.id.exit_game).setOnClickListener(view -> finish());

        MusicUtil.stopMusic();

    }

}