package ro.ase.musapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.bson.types.ObjectId;

public class Song  {
    private String name;
    private String directory;
    private ObjectId songObjectID;

    public Song(String name) {
        this.name = name;
        this.directory = "/data/data/ro.ase.musapp/files/songs/"+name;
        this.songObjectID=null;
    }

    public Song(String name,ObjectId songObjectID) {
        this.name = name;
        this.directory = "/data/data/ro.ase.musapp/files/songs/"+name;
        this.songObjectID=songObjectID;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ObjectId getSongObjectID() {
        return songObjectID;
    }

    public void setSongObjectID(ObjectId songObjectID) {
        this.songObjectID = songObjectID;
    }

    @Override
    public String toString() {
        return "Song "+ name +
                " is at the directory: " + directory;
    }


}
