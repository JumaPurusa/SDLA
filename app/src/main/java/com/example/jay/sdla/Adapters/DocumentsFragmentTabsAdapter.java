package com.example.jay.sdla.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DocumentsFragmentTabsAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> listFragments = new ArrayList<>();
    public static final List<String> tabsTitles = new ArrayList<>();

    public DocumentsFragmentTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragments(Fragment fragment, String title){
        listFragments.add(fragment);
        tabsTitles.add(title);
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
