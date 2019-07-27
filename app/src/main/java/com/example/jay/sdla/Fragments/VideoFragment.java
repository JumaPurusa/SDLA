package com.example.jay.sdla.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.sdla.Adapters.VideoFragmentTabsAdapter;
import com.example.jay.sdla.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class VideoFragment extends Fragment {

    private VideoFragmentTabsAdapter videoFragmentTabsAdapter;
    private ViewPager videoPager;
    private TabLayout videoTabs;

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        videoFragmentTabsAdapter = new VideoFragmentTabsAdapter(getFragmentManager());
        videoPager = view.findViewById(R.id.video_pager);
        setUpViewPager(videoPager);

        videoPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if(VideoContents.mActionMode != null)
                            VideoContents.mActionMode.finish();

                    }
                }
        );

        videoTabs = view.findViewById(R.id.video_tabs);
        videoTabs.setupWithViewPager(videoPager);
        setUpTabTitle();
        return view;
    }

    public void setUpViewPager(ViewPager viewPager){
        VideoFragmentTabsAdapter videoFragmentTabsAdapter = new VideoFragmentTabsAdapter(getFragmentManager());
        videoFragmentTabsAdapter.addFragment(new VideoContents(), "VIDEO");
        videoFragmentTabsAdapter.addFragment(new EncryptedVideo(), "ENCRYPTED");
        viewPager.setAdapter(videoFragmentTabsAdapter);
    }

    public void setUpTabTitle(){
        videoTabs.getTabAt(0).setText(VideoFragmentTabsAdapter.fragmentTitles.get(0));
        videoTabs.getTabAt(1).setText(VideoFragmentTabsAdapter.fragmentTitles.get(1));
    }

}