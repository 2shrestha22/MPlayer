package com.example.mplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable{

    private long id;
    private String title;
    private String artist;

    //Constructor
    public Song(long id, String title, String artist) {

        this.id = id;
        this.title = title;
        this.artist = artist;
    }


    protected Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    //Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }


    //Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
    }

}
