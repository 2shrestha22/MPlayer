package com.example.mplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mplayer.R;

import java.util.ArrayList;

public class PlaylistViewAdapter extends RecyclerView.Adapter <PlaylistViewAdapter.MyPlaylistViewHolder>{

    private Context context;
    private ArrayList<String> playList;
    private String TAG = "PlaylistViewAdapter";
    OnListClickListener mOnListClickListener;


    public PlaylistViewAdapter(Context context, ArrayList<String> playList, OnListClickListener mOnListClickListener) {
        this.context = context;
        this.playList = playList;
        this.mOnListClickListener = mOnListClickListener;
    }

    @NonNull
    @Override
    public MyPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater playlistInf = LayoutInflater.from(context);
        View playlistView = playlistInf.inflate(R.layout.item_playlist, parent, false);
        PlaylistViewAdapter.MyPlaylistViewHolder viewHolder = new PlaylistViewAdapter.MyPlaylistViewHolder(playlistView, mOnListClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlaylistViewHolder holder, int position) {
        holder.playlistName.setText(playList.get(position));
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public class MyPlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView playlistName;
        OnListClickListener onListClickListener;

        public MyPlaylistViewHolder(@NonNull View itemView, OnListClickListener onListClickListener) {
            super(itemView);

            playlistName = itemView.findViewById(R.id.textViewItemPlaylist);

            //interface OnListClickListener to ViewHolder
            this.onListClickListener = onListClickListener;
            //attach onclick listener to entire ViewHolder
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListClickListener.onListClick(getAdapterPosition());
        }
    }

    //click listener interface
    public interface OnListClickListener {
        void onListClick(int position);
    }

}
