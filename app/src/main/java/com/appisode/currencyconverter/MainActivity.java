package com.appisode.currencyconverter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import Data.Global_Data;
import es.dmoral.prefs.Prefs;

public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    String versionName;

    int intro_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Float abc=getAPIVerison();

        intro_value= Prefs.with(MainActivity.this).readInt("intro_key");

        if(intro_value==01)
        {

        }else
        {
            Intent intent = new Intent(MainActivity.this, Activity_Introactivity.class);
            startActivity(intent);
        }

        if (getIntent().getBooleanExtra("LOGOUT", false))
        {
            finish();
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;


        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();

             mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                 if (menuItem.getItemId() == R.id.rate_thisapp) {


                     try
                     {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getApplication().getPackageName())));
                     }
                     catch (android.content.ActivityNotFoundException anfe)
                     {
                         startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/details?id="+getApplication().getPackageName())));
                     }

                 }

                if (menuItem.getItemId() == R.id.more_apps) {


                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id="+getString(R.string.playstore_developer_name))));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id="+getString(R.string.playstore_developer_name))));
                    }




                }

                 if (menuItem.getItemId() == R.id.help_menu) {
                     Intent intent = new Intent(MainActivity.this, Activity_Introactivity.class);
                     startActivity(intent);

                 }

                 return false;
            }

        });


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    public static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "error retriving api version" + e.getMessage());
        }

        Global_Data.android_version=f.floatValue();

        return f.floatValue();
    }
}