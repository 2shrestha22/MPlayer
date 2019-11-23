package com.example.mplayer.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SongList {
    ArrayList<Song> songArrayList = new ArrayList<>();

    private static final String TAG = "SongList";

    /*
    * happy
    * cool
    * amazed
    * peaceful
    * */

    /*
    * all       - 111
    * sad       - 001
    * happy     - 010
    * neutral   - 000
    * angry     -
    * */

//    private ArrayList<Song> songArrayList;


//    public ArrayList<Song> getSongArrayList() {
//        return songArrayList;
//    }

//    public void setSongArrayList(ArrayList<Song> songArrayList) {
//        this.songArrayList = songArrayList;
//    }

    public ArrayList<Song> getAllList(Context context, String eLabel) {
        //retrieve songs info
        //ContentResolver musicResolver = this.getContentResolver(); //this when below gives null pointer exception
        ContentResolver musicResolver = context.getContentResolver();

        //debug
        if (musicResolver != null) {
            Log.d(TAG, "getSongList: ContentResolver is not null");
        }

        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        //Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        //convert emotion label 'res.getLabel()' to int 'mList'
        /* Angry
         * Disgust
         * Fear
         * Happy
         * Sad
         * Surprise
         * Neutral*/

        /*
         * 4 categories of songs + 1 default all songs
         * All = 0
         * Amazed = 1
         * Cool = 2
         * Happy = 3
         * Peaceful = 4
         * */

        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        Log.d(TAG, "getAllList: "+eLabel);
        switch (eLabel) {
            case "Angry":
                musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " LIKE '%/Musics/Cool/%'", null, null);
                break;
            case "Disgust":
            case "Fear":
            case "Sad":
                Log.d(TAG, "getAllList: Disgust, fear, sad called");
                musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " LIKE '%/Musics/Peaceful/%'", null, null);
                break;
            case "Happy":
                musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " LIKE '%/Musics/Happy/%'", null, null);
                break;
            case "Neutral":
                musicCursor = musicResolver.query(musicUri, null, null, null, null);                break;
            case "Surprise":
                musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.DATA + " LIKE '%/Musics/Amazed/%'", null, null);
                break;
        }

        if (musicCursor!=null && musicCursor.moveToFirst()) {
            //get columns
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            //debug
            Log.d(TAG, "getSongList: idColumns are set successfully");

            //add song to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);

                songArrayList.add(new Song(thisId, thisTitle, thisArtist));
            } while (musicCursor.moveToNext());
        }
        return songArrayList;
    }
}
