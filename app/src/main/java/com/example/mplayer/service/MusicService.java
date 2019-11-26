package com.example.mplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mplayer.MainActivity;
import com.example.mplayer.R;
import com.example.mplayer.model.Song;
import com.example.mplayer.model.SongList;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    //recent
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;
    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;
//recent


    private static final String TAG = "debug_MusicService";
    //for notification
    private static final int NOTIFY_ID = 1;

    //media player
    private MediaPlayer mediaPlayer;

    //song list
    private ArrayList<Song> songs;

    //song position
    private int songPos;

    private Song playSong;

    private final IBinder musicBind = new MusicBinder();




    public MusicService() {
    }

    @Override
    public void onCreate() {
        //create the service
        super.onCreate();

        //initializing position
        songPos = 0;

        //create mediaPlayer object
        mediaPlayer = new MediaPlayer();

        initMusicPlayer();
        //set song for first time when service starts
        songs = new SongList().getAllList(this, "All");
        setMusicList(songs);
        //first need to set music list
        setSong(0);
        //broadcast receiver
        controlReceiver();

    }

    //initializing media player
    public void initMusicPlayer () {
        //set player properties
        //continue playback even when screen is locked
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

    }

    public void setMusicList (ArrayList<Song> songs) {
        this.songs = songs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //stopForeground(true);
        //mediaPlayer.stop();
        //mediaPlayer.release();
        return false;
    }

    public void playSong() {
        //play a song
        //reset player first
        mediaPlayer.reset();
        //get song
        playSong = songs.get(songPos);
        //get id
        long currSong = playSong.getId();
        //set URI
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e(TAG, "Error setting data source", e);
        }
        mediaPlayer.prepareAsync();

        //notification
        notification();

    }

    public void setSong(int songIndex){
        songPos=songIndex;
    }

    public String getTrackName() {
        return playSong.getTitle();
    }


    public int getPosn(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posn){
        mediaPlayer.seekTo(posn);
    }

    public void go(){
        mediaPlayer.start();
    }

    public void playPause() {
        if (isPng())
            pausePlayer();
        else
            go();
        Log.d(TAG, "PLAY Called");

    }

    public void playPrev(){
        songPos--;
        if(songPos<0)
            songPos=songs.size()-1;
        playSong();
        Log.d(TAG, "PREV Called");
    }

    //skip to next
    public void playNext(){
        songPos++;
        if(songPos>=songs.size())
            songPos=0;
        playSong();
        Log.d(TAG, "NEXT Called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBind;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        if (mp.getCurrentPosition()>0) {
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();

        sendSongInfo();

    }


    public void notification () {
        //notification
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.headphone)
                .setTicker(getTrackName())
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(getTrackName());
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void sendSongInfo () {

        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }

        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {

                    //broadcast title of playing song and seekbar position
                    //local broadcast receiver
                    final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                    Intent intent = new Intent("com.example.mplayer.intent.SONG_INFO");
                    intent.putExtra("track", getTrackName());
                    intent.putExtra("duration", getDur());
                    intent.putExtra("position", getPosn());
                    localBroadcastManager.sendBroadcast(intent);                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    //broadcast receiver for input control

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //calling respective function

            //next, prev, play, pause
            String signal = intent.getStringExtra("signal");

            //songList and song position
            ArrayList<Song> songList = intent.getParcelableArrayListExtra("songList");
            int listPos = intent.getIntExtra("listPos", 0);

            //song seek
            int seekTo = intent.getIntExtra("seekTo", 0);

            //Toast.makeText(getApplicationContext(),signal, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "BroadCast Received");

            switch (signal) {
                case "PLAY_PAUSE":
                    playPause();
                    break;
                case "NEXT":
                    playNext();
                    break;
                case "PREV":
                    playPrev();
                    break;
                case "PLAY_THIS":
                    //init playlist and play given song
                    songs = songList;
                    songPos = listPos;
                    playSong();
                    break;
                case "SEEK":
                    mediaPlayer.seekTo(seekTo);
                    break;
            }

        }
    };

    private void controlReceiver () {

        //start broadcast receiver
        final LocalBroadcastManager localReceiver = LocalBroadcastManager.getInstance(this);
        final IntentFilter localFilter = new IntentFilter();
        localFilter.addAction("com.example.mplayer.intent.CONTROL_SIGNAL");
        localReceiver.registerReceiver(localBroadcastReceiver, localFilter);

    }

}
