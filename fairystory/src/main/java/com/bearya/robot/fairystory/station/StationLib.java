package com.bearya.robot.fairystory.station;

import android.content.Context;

import com.bearya.robot.base.BaseApplication;
import com.bearya.robot.base.util.FileUtil;
import com.bearya.robot.base.util.ResourceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StationLib {
    Libs imageLibs;
    Libs soundLibs;
    Map<Long, Lib> libMap = new HashMap<>();

    public StationLib(Libs imageLibs, Libs soundLibs) {
        this.imageLibs = imageLibs;
        this.soundLibs = soundLibs;
        int uuidIndex = 1;
        for (Lib lib : imageLibs.libList) {
            lib.uuid = uuidIndex++;
            libMap.put(lib.uuid, lib);
        }
        for (Lib lib : soundLibs.libList) {
            lib.uuid = uuidIndex++;
            libMap.put(lib.uuid, lib);
        }
    }

    public static StationLib getLibsFromAssets(Context context) {
        try {
            String json = FileUtil.stringFromAssetsFile(context, BaseApplication.isEnglish ? "station/en/station_libs.json" : "station/zh/station_libs.json");
            JSONObject root = new JSONObject(json);
            JSONArray jsonImageLibs = root.getJSONArray("image_libs");
            Libs imageLibs = new Libs();

            for (int i = 0; i < jsonImageLibs.length(); i++) {
                JSONObject imageLibJson = jsonImageLibs.getJSONObject(i);
                imageLibs.addLib(parseImageLib(imageLibJson));
            }
            Libs soundLibs = new Libs();
            JSONArray jsonSoundLibs = root.getJSONArray("sound_libs");
            for (int i = 0; i < jsonSoundLibs.length(); i++) {
                JSONObject soundLibJson = jsonSoundLibs.getJSONObject(i);
                soundLibs.addLib(parseSoundLib(soundLibJson));
            }
            return new StationLib(imageLibs, soundLibs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Lib parseImageLib(JSONObject json) throws JSONException {
        Lib lib = new Lib();
        lib.name = json.optString("name");
        lib.icon = json.optString("icon");
        String imageType = json.optString("imageType");
        String imageName = json.optString("imageName");
        JSONArray itemArr = json.optJSONArray("items");
        for (int i = 0; i < itemArr.length(); i++) {
            JSONObject itemJson = itemArr.getJSONObject(i);
            LibItem imageItem = new LibItem();
            imageItem.name = itemJson.optString("name");
            imageItem.type = imageType;
            if (imageType.equals("res")) {
                imageItem.image = imageName + itemJson.optString("index");
            } else if ("lottie".equals(imageType)) {
                imageItem.image = itemJson.optString("image");
            }
            lib.items.add(imageItem);
        }
        return lib;
    }

    private static Lib parseSoundLib(JSONObject json) throws JSONException {
        Lib lib = new Lib();
        lib.name = json.optString("name");
        lib.icon = json.optString("icon");
        String soundName = json.optString("soundName");
        String imageType = json.optString("imageType");
        String imageName = json.optString("imageName");
        String soundType = json.optString("soundType");
        JSONArray itemArr = json.optJSONArray("items");
        for (int i = 0; i < itemArr.length(); i++) {
            JSONObject itemJson = itemArr.getJSONObject(i);
            LibItem soundItem = new LibItem();
            if (imageType.equals("res")) {
                soundItem.name = itemJson.optString("name");
                soundItem.image = String.valueOf(ResourceUtil.getMipmapId(imageName + itemJson.optString("index")));
            }
            if ("assets".equals(soundType)) {
                soundItem.mp3 = soundName + itemJson.optString("index") + ".mp3";
            }
            lib.items.add(soundItem);
        }
        return lib;
    }

    public Lib getByUUID(long uuid) {
        return libMap.get(uuid);
    }

    public LibItem getSoundItemByMp3(String sound) {
        for (Lib lib : soundLibs.libList) {
            for (LibItem libItem : lib.items) {
                if (libItem.mp3 != null && libItem.mp3.equals(sound)) {
                    return libItem;
                }
            }
        }
        return null;
    }
}
