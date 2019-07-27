package com.example.jay.sdla.Adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagesFragmentTabsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> listFragments = new ArrayList<>();
    public static final List<String> fragmentTitles = new ArrayList<>();

    public ImagesFragmentTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragments(Fragment fragment, String title){
        listFragments.add(fragment);
        fragmentTitles.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        return listFragments.get(position);
    }

    @Override
    public int getCount() {
        if(listFragments != null){
            return listFragments.size();
        }else{
            return 0;
        }
    }
}
