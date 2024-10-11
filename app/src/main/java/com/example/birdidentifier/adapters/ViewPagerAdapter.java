package com.example.birdidentifier.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.birdidentifier.Fragments.CameraFragment;
import com.example.birdidentifier.Fragments.MainFragment;
import com.example.birdidentifier.Fragments.microphoneFragment.MicrophoneFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CameraFragment();
            case 2:
                return new MicrophoneFragment();
            default:
                return new MainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
