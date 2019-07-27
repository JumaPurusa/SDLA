package com.example.jay.sdla.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.sdla.Adapters.ImagesFragmentTabsAdapter;
import com.example.jay.sdla.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class ImagesFragment extends Fragment {

    private ViewPager imagesViewPager;
    private TabLayout imageTabs;
    private ImagesFragmentTabsAdapter imagesFragmentTabsAdapter;

    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        imagesFragmentTabsAdapter = new ImagesFragmentTabsAdapter(getFragmentManager());
        imagesViewPager = view.findViewById(R.id.images_pager);
        setUpWithViewPager(imagesViewPager);

        imagesViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if(ImagesContent.mActionMode != null){
                            ImagesContent.mActionMode.finish();
                        }
                    }
                }
        );


        imageTabs = view.findViewById(R.id.images_tabs);
        imageTabs.setupWithViewPager(imagesViewPager);
        setUpTabTitles();

        return view;
    }

    public void setUpWithViewPager(ViewPager viewPager){
        ImagesFragmentTabsAdapter imagesFragmentTabsAdapter = new ImagesFragmentTabsAdapter(getFragmentManager());
        imagesFragmentTabsAdapter.addFragments(new ImagesContent(), "IMAGES");
        imagesFragmentTabsAdapter.addFragments(new EncryptedImages(), "ENCRYPTED");
        viewPager.setAdapter(imagesFragmentTabsAdapter);
    }

    public void setUpTabTitles(){
        imageTabs.getTabAt(0).setText(ImagesFragmentTabsAdapter.fragmentTitles.get(0));
        imageTabs.getTabAt(1).setText(ImagesFragmentTabsAdapter.fragmentTitles.get(1));
    }


}