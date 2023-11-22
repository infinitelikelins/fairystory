package com.bearya.robot.fairystory.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bearya.robot.base.play.PlayData;
import com.bearya.robot.base.ui.BaseActivity;
import com.bearya.robot.base.util.MusicUtil;
import com.bearya.robot.base.util.ResourceUtil;
import com.bearya.robot.fairystory.R;
import com.bearya.robot.fairystory.ui.CardControllerActivity;
import com.bearya.robot.fairystory.ui.popup.impl.DeleteConfirmPopup;
import com.bearya.robot.qdreamer.QdreamerAudio;

public class StationsActivity extends BaseActivity implements View.OnClickListener {


    public static StationLib stationLib;
    private final ImageView[] stationViewArr = new ImageView[6];
    private boolean isClicked = false;

    public static void start(Context context) {
        context.startActivity(new Intent(context, StationsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_load);
        for (int i = 0; i < stationViewArr.length; i++) {
            ImageView view = findViewById(ResourceUtil.getId(getApplicationContext(), "btnStation" + (i + 1)));
            view.setTag(i + 1);
            view.setOnClickListener(this);
            stationViewArr[i] = view;
        }
        MusicUtil.playAssetsAudio("station/zh/station_init.mp3");
        stationLib = StationLib.getLibsFromAssets(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        if (!isClicked) {
            isClicked = true;
            int index = (Integer) view.getTag();
            startActivity(new Intent(getApplication(), StationConfigActivity.class).putExtra("index", index));
        }
    }

    public void onPerformClicked(View view) {
        CardControllerActivity.start(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        QdreamerAudio.getInstance().init(this);
        loadHistory();
    }

    private void loadHistory() {
        for (ImageView iv : stationViewArr) {
            Object tag = iv.getTag();
            if (tag instanceof Integer) {
                PlayData playData = StationConfigActivity.getLastConfigStation(getApplicationContext(), (Integer) tag);
                iv.setSelected(!playData.isEmpty());
            }
        }
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onClearClicked(View view) {
        deleteStationConfig();
    }

    /**
     * 清理站点的配置
     */
    private void deleteStationConfig() {
        DeleteConfirmPopup popup = new DeleteConfirmPopup(this);
        popup.applyShowTips(getString(R.string.clear_all_station_config));
        popup.applyShowAudio("card/zh/p_delete_station.mp3");
        popup.withConfirm(v -> {
            StationConfigActivity.clearStationCache(getApplicationContext());
            loadHistory();
            Toast.makeText(getApplicationContext(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
        }, null);
        popup.showPopupWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QdreamerAudio.getInstance().release();
        stationLib = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicUtil.stopMusic();
        isClicked = false;
    }
}
