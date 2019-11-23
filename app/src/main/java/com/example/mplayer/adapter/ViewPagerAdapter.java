package com.example.mplayer.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mplayer.fragment.FragmentCamera;
import com.example.mplayer.fragment.FragmentNowPlaying;
import com.example.mplayer.fragment.FragmentSongs;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    String data[] = {"Now Playing", "All Songs", "Camera"};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return new FragmentNowPlaying();
        if (position == 1)
            return new FragmentSongs();
        if (position == 2)
            return  new FragmentCamera();
        return null;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return data[position];
    }
}

