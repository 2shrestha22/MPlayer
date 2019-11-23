package com.example.mplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mplayer.adapter.PlaylistViewAdapter;
import com.example.mplayer.adapter.ViewPagerAdapter;
import com.example.mplayer.model.Song;
import com.example.mplayer.service.MusicService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlaylistViewAdapter.OnListClickListener{

    TabLayout tabLayout;
    ViewPager viewPager;
    public static Context contextOfApplication;
    String TAG = "MainActivity";
    ArrayList<Song> songArrayList = new ArrayList<>();

    //binder
    private MusicService musicService;
    private Intent playIntent;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //binding service
        doBindService();
        contextOfApplication = getApplicationContext();
    }

    //uncommented from here

    private ServiceConnection musicConnection = new ServiceConnection () {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicService = binder.getService();

            //below commented is transferred to service to init only once but not every time app opens
//            songArrayList = new SongList().getAllList(getContextOfApplication());
//            musicService.setMusicList(songArrayList);
//            //first need to set music list
//            musicService.setSong(0);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            musicService = null;
        }
    };

    void doBindService() {
        if (playIntent == null) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        //bindService(new Intent(MainActivity.this, MusicService.class), musicConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    void doUnBindService() {
        if (isBound) {
            // Detach our existing connection.
            unbindService(musicConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy MainActivity: Called");
        super.onDestroy();
        doUnBindService();
    }

    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    @Override
    public void onListClick(int position) {
        //this is OnClickListener for playlist which is not ready yet
        Log.d(TAG, "onListCLick: listened with interface");
        Toast.makeText(this,"onListClick: listened with interface", Toast.LENGTH_SHORT).show();
    }


    //uncommented upto here

    //inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.exit_menu:
                stopService(playIntent);
                Toast.makeText(getContextOfApplication(), "MPlayer Closed",Toast.LENGTH_SHORT).show();
                System.exit(1);
        }
        return true;
    }
}
