package com.example.mplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mplayer.R;


public class FragmentNowPlaying extends Fragment {

    public static final String TAG = "FragmentNowPlaying";
    private TextView textView;
    private SeekBar seekBar;
    private Button btnPlayPause;
    private String mtitle = "Title";
    private TextView textTotalDuration, textCurrentDuration;

    private boolean isPlaying = false;

    public FragmentNowPlaying() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBar = view.findViewById(R.id.seekBar2);
        textView = view.findViewById(R.id.tvTitle);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        Button btnNext = view.findViewById(R.id.btnNext);
        Button btnPrev = view.findViewById(R.id.btnPrev);

        textCurrentDuration = view.findViewById(R.id.textCurrentDuration);
        textTotalDuration = view.findViewById(R.id.textTotalDuration);

        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
                intent.putExtra("signal", "NEXT");
                localBroadcastManager.sendBroadcast(intent);
                Log.d(TAG, "NEXT broadcast sent");
            }
        });

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
                intent.putExtra("signal", "PLAY_PAUSE");
                localBroadcastManager.sendBroadcast(intent);
                if (isPlaying) {
                    btnPlayPause.setBackgroundResource(R.drawable.play);
                    isPlaying = false;
                } else {
                    btnPlayPause.setBackgroundResource(R.drawable.pause);
                    isPlaying = true;
                }
                Log.d(TAG, "PLAY broadcast sent");
            }
        });


        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
                intent.putExtra("signal", "PREV");
                localBroadcastManager.sendBroadcast(intent);
                Log.d(TAG, "PREV broadcast sent");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
                    intent.putExtra("signal", "SEEK");
                    intent.putExtra("seekTo", progress);
                    localBroadcastManager.sendBroadcast(intent);
                    Log.d(TAG, "SEEK broadcast sent");
                }
            }
        });

    }

    private void sendControlSignal(String signal) {
        //this is local broadcast sender that sends control signal to music service
    }


    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "onReceive: Called");

            //get music title
            String title = intent.getStringExtra("track");
            //Log.d(TAG, "onReceive: "+title);
            //get duration
            int duration = intent.getIntExtra("duration", 100);
            //Log.d(TAG, "onReceive: "+duration);
            //get seekBar position
            int seekPos = intent.getIntExtra("position", 100);
            //Log.d(TAG, "onReceive: "+seekPos);

//            Toast toast = Toast.makeText(getContext(),title, Toast.LENGTH_SHORT);
//            toast.show();

            //calling respective function to update UI
            if (!mtitle.equals(title)) { //checks if title is updated or not then updates UI
                initUI(duration, title);
                mtitle = title;

                btnPlayPause.setBackgroundResource(R.drawable.pause);
                isPlaying = true;

                Log.d(TAG, "onReceive: title= ".concat(title).concat("mtitle= ").concat(mtitle));
            }

            updateSeekBar(seekPos);
        }
    };

    //initialize title and duration
    private void initUI(int duration, String title) {

        //music title in now playing screen
        textView.setText(title);
        //auto scrolling
        textView.setHorizontallyScrolling(true);
        textView.setMovementMethod(new ScrollingMovementMethod());
        //auto scrolling, if Text is set via XML
        //textView.setSelected(true);

        //seekbar
        seekBar.setMax(duration);

        //music duration
        textTotalDuration.setText(timeString(duration));
    }

    //update seekbar position
    void updateSeekBar (int position) {
        //sekbar
        seekBar.setProgress(position);
        //music current duration
        textCurrentDuration.setText(timeString(position));
    }

    // testing
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(),"onResume called", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onResume: called");
        //calling broadcast receiver
        songInfoReceiver();
    }

    private void songInfoReceiver () {

        Log.d(TAG, "songInfoReceiver: Called");
        //start broadcast receiver
        final LocalBroadcastManager mlocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction("com.example.mplayer.intent.SONG_INFO");
        mlocalBroadcastManager.registerReceiver(localBroadcastReceiver, localFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: callled");
        //stopping broadcast receiver
        final LocalBroadcastManager mlocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //unregister
        mlocalBroadcastManager.unregisterReceiver(localBroadcastReceiver);
        //reset title to Title or it will not update after resume
        mtitle = "Title";
    }

    public String timeString (int time) {
        int min = time/1000/60;
        int sec = time/1000%60;

        return String.format("%02d:%02d", min, sec);
    }

}
