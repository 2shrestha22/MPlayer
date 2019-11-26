package com.example.mplayer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mplayer.R;
import com.example.mplayer.adapter.RecyclerViewAdapter;
import com.example.mplayer.model.Song;
import com.example.mplayer.model.SongList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSongs extends Fragment implements RecyclerViewAdapter.OnSongClickListener{

    private ArrayList<Song> songArrayList = new ArrayList<>();
    private RecyclerView songRecyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String TAG = "DebugFragmentSongs";

    //binder
//    private MusicService musicService;
//    private Intent playIntent;
//    private boolean isBound = false;

    public FragmentSongs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);
        songRecyclerView = (RecyclerView) v.findViewById(R.id.rViewAllSongs);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), songArrayList, this);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        songRecyclerView.setAdapter(recyclerViewAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get songs list
        //getSongList();
        songArrayList = new SongList().getAllList(getContext(), "All");

        Collections.sort(songArrayList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


        //doBindService();

    }

    //getting all songs and adding into list
//    public void getSongList(){
//
//        //retrieve songs info
////        ContentResolver musicResolver = MainActivity.getContextOfApplication().getContentResolver(); //this when below gives null pointer exception
//        ContentResolver musicResolver = getActivity().getContentResolver();
//
//        //debug
//        if (musicResolver != null) {
//            Log.d(TAG, "getSongList: ContentResolver is not null");
//        }
//
//        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//
//        if (musicCursor!=null && musicCursor.moveToFirst()) {
//            //get columns
//            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//
//            //debug
//            Log.d(TAG, "getSongList: idColumns are set successfully");
//
//            //add song to list
//            do {
//                long thisId = musicCursor.getLong(idColumn);
//                String thisTitle = musicCursor.getString(titleColumn);
//                String thisArtist = musicCursor.getString(artistColumn);
//
//                songArrayList.add(new Song(thisId, thisTitle, thisArtist));
//            } while (musicCursor.moveToNext());
//        }
//    }

//    private ServiceConnection musicConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
//            //get service
//            musicService=binder.getService();
//            //pass music list
//            musicService.setMusicList(songArrayList);
//            isBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            isBound = false;
//        }
//    };

//    void doBindService () {
//
//        if (playIntent == null) {
//            playIntent = new Intent(getActivity(), MusicService.class);
//            getActivity().getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
//            //getActivity().startService(playIntent);
//        }
//        //getActivity().bindService(new Intent(getActivity(), MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);
//        isBound = true;
//    }
//
//    void doUnbindService() {
//        if (isBound) {
//            // Detach our existing connection.
//            getActivity().getApplicationContext().unbindService(musicConnection);
//            isBound = false;
//        }
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        doUnbindService();
//    }

    @Override
    public void onSongClick(int position) {
        //do what happens when song item is clicked
//        musicService.setSong(position);
//        musicService.playSong();
        //call broadcast sender
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
        intent.putExtra("signal", "PLAY_THIS");
        intent.putParcelableArrayListExtra("songList", songArrayList);
        intent.putExtra("listPos", position);
        localBroadcastManager.sendBroadcast(intent);
        Log.d(TAG, "All-playlist and position sent");

        //OnClick is working well
        //Toast toast = Toast.makeText(getContext(),Integer.toString(position), Toast.LENGTH_SHORT);
        //toast.show();
    }

}
