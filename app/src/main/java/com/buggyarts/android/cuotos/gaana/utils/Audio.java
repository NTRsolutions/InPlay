package com.buggyarts.android.cuotos.gaana.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mayank on 11/15/17
 */

public class Audio implements Parcelable{

    public String data, title, album, album_id, artist, key;
    public int _id,min,sec, year;

    //Requested to assign information to a track
    public Audio(int id,String key,String data,String title,String album,String album_id,String artist,int min,int sec,int year){
        this._id = id;
        this.key = key;
        this.data = data;
        this.title = title;
        this.album = album;
        this.album_id = album_id;
        this.artist = artist;
        this.min = min;
        this.sec = sec;
        this.year = year;
    }

    public Audio(String data,String title,String artist,String album_id,int min,int sec){
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.album_id = album_id;
        this.min = min;
        this.sec = sec;
    }

    //Make array list of songs
    public Audio(String data,String title, String artist, String album_id){
        this.data = data;
        this.title = title;
        this.artist = artist;
        this.album_id = album_id;
    }

    //Make array list of albums
    public Audio(String data,String album,String album_id){
        this.data = data;
        this.album = album;
        this.album_id = album_id;
    }

    public Audio(String artist){
        this.artist = artist;
    }

    //load track list from album
    public Audio(String data,String title,String album,String album_id,String artist){
        this.data = data;
        this.title = title;
        this.album = album;
        this.album_id = album_id;
        this.artist = artist;
    }
    //load Artist Tracks from artist
    public Audio(String data,String title,String album,String album_id,String artist,int min,int sec){
        this.data = data;
        this.title = title;
        this.album = album;
        this.album_id = album_id;
        this.artist = artist;
        this.min = min;
        this.sec = sec;
    }

    /**
     * Getter and Setter Methods
     * @return
     */

    public int get_id() {
        return _id;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getArtist() {
        return artist;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public int getYear() {
        return year;
    }

    /**
     * Define Parcelable Methods
     * @param in
     */

    protected Audio(Parcel in) {
        data = in.readString();
        title = in.readString();
        album = in.readString();
        album_id = in.readString();
        artist = in.readString();
        key = in.readString();
        _id = in.readInt();
        min = in.readInt();
        sec = in.readInt();
        year = in.readInt();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data);
        parcel.writeString(title);
        parcel.writeString(album);
        parcel.writeString(album_id);
        parcel.writeString(artist);
        parcel.writeString(key);
        parcel.writeInt(_id);
        parcel.writeInt(min);
        parcel.writeInt(sec);
        parcel.writeInt(year);
    }
}
