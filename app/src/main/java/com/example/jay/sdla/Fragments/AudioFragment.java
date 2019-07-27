package com.example.jay.sdla.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.sdla.Adapters.TopFragmentTabsAdapter;
import com.example.jay.sdla.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment {

    private ViewPager audioViewPager;
    private TopFragmentTabsAdapter audioFragmentTabsAdapter;
    private TabLayout audiotabs;

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        audioFragmentTabsAdapter = new TopFragmentTabsAdapter(getFragmentManager());
        audioViewPager = view.findViewById(R.id.audio_pager);
        setUpViewPager(audioViewPager);


        audioViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if(AudioContentsFragment.mActionMode != null){
                            AudioContentsFragment.mActionMode.finish();
                        }
                    }
                }
        );

        audiotabs = view.findViewById(R.id.audio_tabs);
        audiotabs.setupWithViewPager(audioViewPager);
        setUpTabTitle();



        return view;
    }

    public void setUpViewPager(ViewPager viewPager){
        TopFragmentTabsAdapter audioFragmentTabsAdapter = new TopFragmentTabsAdapter(getFragmentManager());
        audioFragmentTabsAdapter.addFragment(new AudioContentsFragment(), "AUDIO");
        audioFragmentTabsAdapter.addFragment(new AudioEncrypted(), "ENCRYPTED");
        viewPager.setAdapter(audioFragmentTabsAdapter);
    }

    public void setUpTabTitle(){
        audiotabs.getTabAt(0).setText(TopFragmentTabsAdapter.fragmentTitle.get(0));
        audiotabs.getTabAt(1).setText(TopFragmentTabsAdapter.fragmentTitle.get(1));

    }
}
