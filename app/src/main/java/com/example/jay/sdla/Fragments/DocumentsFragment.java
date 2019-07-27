package com.example.jay.sdla.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jay.sdla.Adapters.DocumentsFragmentTabsAdapter;
import com.example.jay.sdla.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class DocumentsFragment extends Fragment {

    private DocumentsFragmentTabsAdapter documentsFragmentTabsAdapter;
    private ViewPager documentsViewPager;
    private TabLayout documentsTabs;
    public DocumentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_documents, container, false);

        documentsFragmentTabsAdapter = new DocumentsFragmentTabsAdapter(getFragmentManager());
        documentsViewPager = view.findViewById(R.id.documents_pager);
        setUpViewPager(documentsViewPager);

        documentsViewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        if(DocumentsContents.mActionMode != null){
                            DocumentsContents.mActionMode.finish();
                        }

                        if(EncryptedDocuments.mActionMode != null){
                            EncryptedDocuments.mActionMode.finish();
                        }

                    }
                }
        );

        documentsTabs = view.findViewById(R.id.documents_tabs);
        documentsTabs.setupWithViewPager(documentsViewPager);
        setUpTabTitle();

        return view;
    }// end method onCreateView

    public void setUpViewPager(ViewPager viewPager){
        DocumentsFragmentTabsAdapter documentsFragmentTabsAdapter
                = new DocumentsFragmentTabsAdapter(getFragmentManager());
        documentsFragmentTabsAdapter.addFragments(new DocumentsContents(), "DOCUMENTS");
        documentsFragmentTabsAdapter.addFragments(new EncryptedDocuments(), "ENCRYPTED");
        viewPager.setAdapter(documentsFragmentTabsAdapter);
    }// end method setUpViewPager

    public void setUpTabTitle(){
        documentsTabs.getTabAt(0).setText(DocumentsFragmentTabsAdapter.tabsTitles.get(0));
        documentsTabs.getTabAt(1).setText(DocumentsFragmentTabsAdapter.tabsTitles.get(1));
    }// end method setUpTabTitle

}