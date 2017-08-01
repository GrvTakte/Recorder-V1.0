package com.gaurav.recorder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gaurav.recorder.Fragments.AudioRecordingFragment;
import com.gaurav.recorder.Fragments.VideoRecordingFragment;

/**
 * Created by gaurav on 31/07/17.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AudioRecordingFragment();

            case 1:
                return new VideoRecordingFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
