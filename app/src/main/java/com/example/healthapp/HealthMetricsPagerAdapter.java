package com.example.healthapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HealthMetricsPagerAdapter extends FragmentPagerAdapter {

    public HealthMetricsPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new StepsFragment();
            case 1: return new BpmFragment();
            case 2: return new SleepFragment();
            default: return new StepsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}