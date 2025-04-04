package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {
    private String name;
    private String uri;
    private String description;
    private String imageUrl;

    public Playlist(String name, String uri, String description, String imageUrl) {
        this.name = name;
        this.uri = uri;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    protected Playlist(Parcel in) {
        name = in.readString();
        uri = in.readString();
        description = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(uri);
        dest.writeString(description);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
