package com.example.mplayer.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mplayer.R;
import com.example.mplayer.adapter.PlaylistViewAdapter;
import com.example.mplayer.service.MusicService;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPlaylist extends Fragment {

    private String TAG = "FragmentPlaylist";
    RecyclerView playlistRecyclerView;
    PlaylistViewAdapter playlistViewAdapter;
    private ArrayList<String> playList = new ArrayList<>();
    private MusicService musicService;

    public FragmentPlaylist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        playlistRecyclerView = v.findViewById(R.id.rViewPlaylist);
        playlistViewAdapter = new PlaylistViewAdapter(getContext(), playList, (PlaylistViewAdapter.OnListClickListener) getContext());
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        playlistRecyclerView.setAdapter(playlistViewAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPlaylist();
    }

    private void initPlaylist() {
        Log.d(TAG, "initPlaylist: setting up playlist names");

        playList.add("Happy");
        playList.add("Sad");
        playList.add("Angry");
        playList.add("Neutral");
    }

//    @Override
//    public void onListClick(int position) {
//        Log.d(TAG, "onListClick: ".concat(String.valueOf(position)));
//
//        if (position == 0){
//            playList.add("Sangam");
//            playlistViewAdapter = new PlaylistViewAdapter(getContext(), playList, this);
//            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            playlistRecyclerView.setAdapter(playlistViewAdapter);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(),"onResume called at playlist", Toast.LENGTH_SHORT).show();

    }
}
