package com.bearya.robot.fairystory.ui.res;

import android.os.Parcel;
import android.os.Parcelable;

import com.bearya.robot.base.play.PlayData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class WalkHistory implements Parcelable {

    private final long id;
    private final String playData;
    private final Gson gson = new Gson();

    public WalkHistory(long idKey, List<PlayData> playDataArrayList) {
        id = idKey;
        playData = gson.toJson(playDataArrayList);
    }

    protected WalkHistory(Parcel in) {
        id = in.readLong();
        playData = in.readString();
    }

    public long getId() {
        return id;
    }

    public List<PlayData> getPlayDataArrayList() {
        return gson.fromJson(playData, new TypeToken<List<PlayData>>() {}.getType());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(playData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WalkHistory> CREATOR = new Creator<WalkHistory>() {
        @Override
        public WalkHistory createFromParcel(Parcel in) {
            return new WalkHistory(in);
        }

        @Override
        public WalkHistory[] newArray(int size) {
            return new WalkHistory[size];
        }
    };

}