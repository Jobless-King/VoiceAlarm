package com.galaxy.voicealarm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016-10-01.
 */
public class AudioFile implements Parcelable {
    private final String FileName;
    private final String FilePath;

    public AudioFile(String FileName, String FilePath){
        this.FileName = FileName;
        this.FilePath = FilePath;
    }

    public AudioFile(Parcel parcel){
        this.FileName = parcel.readString();
        this.FilePath = parcel.readString();
    }

    public String getFileName(){return this.FileName;}
    public String getFilePath(){return this.FilePath;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.FileName);
        dest.writeString(this.FilePath);
    }

    public static final Creator<AudioFile> CREATOR = new Creator<AudioFile>() {
        @Override
        public AudioFile createFromParcel(Parcel source) {
            return new AudioFile(source);
        }

        @Override
        public AudioFile[] newArray(int size) {
            return new AudioFile[size];
        }
    };
}
