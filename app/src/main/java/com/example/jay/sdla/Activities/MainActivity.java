package com.example.jay.sdla.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;


import com.example.jay.sdla.Fragments.AudioFragment;
import com.example.jay.sdla.Fragments.DocumentsFragment;
import com.example.jay.sdla.Fragments.ImagesFragment;
import com.example.jay.sdla.Fragments.VideoFragment;
import com.example.jay.sdla.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, 0, 0, 0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            setFragment(new AudioFragment());
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_audio) {
            setFragment(new AudioFragment());
        } else if (id == R.id.nav_video) {
            setFragment(new VideoFragment());
        } else if (id == R.id.nav_images) {
            setFragment(new ImagesFragment());
        } else if (id == R.id.nav_documents) {
            setFragment(new DocumentsFragment());
        } else if(id == R.id.nav_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        } else if(id == R.id.nav_about){
            startActivity(new Intent(this, AboutActivity.class));
        }


        /*
        if(DocumentsContents.mActionMode != null){
            DocumentsContents.mActionMode.finish();
        }

        if(EncryptedDocuments.mActionMode != null){
            EncryptedDocuments.mActionMode.finish();
        }
        */

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        /*
        if(DocumentsContents.mActionMode != null){
            DocumentsContents.mActionMode.finish();
        }

        if(EncryptedDocuments.mActionMode != null){
            EncryptedDocuments.mActionMode.finish();
        }
        */

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                }
        );
        return super.onCreateOptionsMenu(menu);
    }

    public void setFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(
                            R.id.containerView,
                            fragment,
                            fragment.getTag()
                    ).commit();
        }
    }
}
