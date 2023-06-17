package com.bearya.robot.fairystory.ui.res;

import android.os.Environment;

import com.bearya.actionlib.utils.SharedPreferencesUtil;
import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.play.PlayData;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class HistoryRecord {

    private static HistoryRecord historyRecord;

    public static HistoryRecord getInstance() {
        if (historyRecord == null) {
            historyRecord = new HistoryRecord();
        }
        return historyRecord;
    }

    private final SharedPreferencesUtil instance;
    private final MMKV mmkv;
    private final ArrayList<PlayData> playDataList;

    private HistoryRecord() {
        String relativePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.bearya.robot.programme/mmkv";
        mmkv = MMKV.mmkvWithID("play-data-story-config", MMKV.MULTI_PROCESS_MODE, "PLAY-DATA-STORY-CONFIG", relativePath);
        instance = SharedPreferencesUtil.getInstance(BaseApplication.getInstance().getApplicationContext());
        playDataList = new ArrayList<>();
    }

    public void writeValue(int pathCount, int propCount, int station) {
        instance.put("pathCount", pathCount).put("propCount", propCount).put("stationCount", station);
    }

    public int readPathCountValue() {
        return instance.getInt("pathCount", 0);
    }

    public int readPropCountValue() {
        return instance.getInt("propCount", 0);
    }

    public int readStationCountValue() {
        return instance.getInt("stationCount", 0);
    }

    public void put(PlayData data) {
        if (!data.isEmpty()) playDataList.add(new PlayData((data)));
    }

    public long save() {
        int id = instance.getInt("PLAY_COUNT", 0);
        WalkHistory history = new WalkHistory(id + 1, playDataList);
        mmkv.encode(String.valueOf(history.getId()), history);
        instance.put("PLAY_COUNT", history.getId());
        playDataList.clear();
        return history.getId();
    }

    public String[] allKey() {
        String[] strings = mmkv.allKeys();
        if (strings != null && strings.length > 0) {
            Arrays.sort(strings, (o1, o2) -> Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2)));
        }
        return strings;
    }

    public WalkHistory getItem(String idKey) {
        return mmkv.decodeParcelable(idKey, WalkHistory.class);
    }

    public void remove(String key) {
        if (mmkv.contains(key)) mmkv.remove(key);
    }

    public void clearSavePlayDataMemory() {
        playDataList.clear();
    }

    public void release(){
        mmkv.clearMemoryCache();
        mmkv.close();
        playDataList.clear();
    }

}