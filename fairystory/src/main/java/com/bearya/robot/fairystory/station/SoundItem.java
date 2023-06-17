package com.bearya.robot.fairystory.station;

import android.os.Parcel;
import android.os.Parcelable;

public class SoundItem implements Parcelable {
    private String name;
    private String mp3;
    private long during;
    private int icon;

    public SoundItem(String name, String mp3, long during, int icon) {
        this.name = name;
        this.mp3 = mp3;
        this.during = during;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public long getDuring() {
        return during;
    }

    public void setDuring(long during) {
        this.during = during;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.mp3);
        dest.writeLong(this.during);
        dest.writeInt(this.icon);
    }

    protected SoundItem(Parcel in) {
        this.name = in.readString();
        this.mp3 = in.readString();
        this.during = in.readLong();
        this.icon = in.readInt();
    }

    public static final Creator<SoundItem> CREATOR = new Creator<SoundItem>() {
        @Override
        public SoundItem createFromParcel(Parcel source) {
            return new SoundItem(source);
        }

        @Override
        public SoundItem[] newArray(int size) {
            return new SoundItem[size];
        }
    };
}
