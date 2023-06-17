package com.bearya.robot.fairystory.station;


import android.os.Parcel;
import android.os.Parcelable;

import com.bearya.robot.base.play.FaceType;

public class LibItem implements Parcelable {
    String name;
    String type;
    String image;
    String mp3;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.image);
        dest.writeString(this.mp3);
    }

    public LibItem() {
    }

    protected LibItem(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.image = in.readString();
        this.mp3 = in.readString();
    }

    public static final Creator<LibItem> CREATOR = new Creator<LibItem>() {
        @Override
        public LibItem createFromParcel(Parcel source) {
            return new LibItem(source);
        }

        @Override
        public LibItem[] newArray(int size) {
            return new LibItem[size];
        }
    };

    public FaceType getFaceType() {
        if("lottie".equals(type)){
            return FaceType.Lottie;
        }
        return FaceType.Image;
    }
}
