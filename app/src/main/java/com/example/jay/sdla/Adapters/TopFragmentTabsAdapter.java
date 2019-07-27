package com.example.jay.sdla.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TopFragmentTabsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    public static final List<String> fragmentTitle = new ArrayList<>();



    public TopFragmentTabsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitle.add(title);
    }


    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        if(fragmentList != null){
            return fragmentList.size();
        }else{
            return 0;
        }

    }
}
