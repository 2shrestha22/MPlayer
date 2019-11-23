package com.example.mplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mplayer.R;
import com.example.mplayer.model.Song;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Song> songs;
    private OnSongClickListener onSongClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<Song> songs, OnSongClickListener onSongClickListener) {
        this.context = context;
        this.songs = songs;
        this.onSongClickListener = onSongClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater songInf = LayoutInflater.from(context);
        View songView = songInf.inflate(R.layout.item_song, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(songView, onSongClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Song currSong = songs.get(position);
        holder.textViewTitle.setText(currSong.getTitle());
        holder.textViewArtist.setText(currSong.getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewTitle, textViewArtist;
        private ImageView imageViewArtWork;

        //click listener
        OnSongClickListener onSongClickListener;


        public MyViewHolder(@NonNull View itemView, OnSongClickListener onSongClickListener) {
            super(itemView);

            textViewTitle = (TextView)itemView.findViewById(R.id.textViewTitle);
            textViewArtist = (TextView)itemView.findViewById(R.id.textViewArtist);
            imageViewArtWork = (ImageView)itemView.findViewById(R.id.iv_ArtWork);


            //click listener
            this.onSongClickListener = onSongClickListener;

            //click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onSongClickListener.onSongClick(getAdapterPosition());

        }
    }

    //click listener interface
    public interface OnSongClickListener {
        void onSongClick(int position);
    }

}
