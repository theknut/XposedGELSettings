/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class MainActivity extends InAppPurchase {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mFragmentTitles;
    private Fragment mCurrFragment = null;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CommonUI.CONTEXT = (Context) this;
        
        mTitle = mDrawerTitle = getTitle();
        mFragmentTitles = getResources().getStringArray(R.array.fragmenttitles_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        mDrawerLayout.getRootView().setBackgroundColor(CommonUI.UIColor);
		getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mFragmentTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        CommonUI.AUTO_BLUR_IMAGE = CommonUI.CONTEXT.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getBoolean("autoblurimage", false);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// sending the colors to Tinted Status Bar
    	StatusBarTintApi.sendColorChangeIntent(CommonUI.UIColor, Color.WHITE, CommonUI.UIColor, Color.WHITE, this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        switch (item.getItemId()) {
		    // action with ID action_refresh was selected
		    case R.id.action_refresh:
		    	CommonUI.restartLauncherOrDevice();
		      break;
		    default:
		      break;
	    }

	    return true;
    }
    
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	boolean beforeIsDonate = isDonate;
    	
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (isDonate && !beforeIsDonate) {
    		selectItem(10);
    	}
    }

    private void selectItem(final int position) {
    	
    	// update the main content by replacing fragments
        FragmentManager fm = getFragmentManager();
        
        switch(position) {
	        case 0:
	        	mCurrFragment = new FragmentWelcome();
	        	break;
	        case 1:
	        	mCurrFragment = new FragmentGeneral();
	        	break;
	        case 2:
	        	mCurrFragment = new FragmentSearchbar();
	        	break;
	        case 3:
	        	mCurrFragment = new FragmentHomescreen();
	        	break;
	        case 4:
	        	mCurrFragment = new FragmentAppDrawer();
	        	break;
	        case 5:
	        	mCurrFragment = new FragmentGestures();
	        	break;
	        case 6:
	        	mCurrFragment = new FragmentNotificationBadges();
	        	break;
	        case 7:
	        	mCurrFragment = new FragmentSystemUI();
	        	break;
	        case 8:
	        	mCurrFragment = new FragmentBackupRestore();
	        	break;
	        case 9:
	        	mCurrFragment = new FragmentSettings();
	        	break;
	        // !!!! don't forget to change onActivityResult, too !!!!
	        case 10:
	        	mCurrFragment = new FragmentDonate();
	        	break;
        }
        
        fm.beginTransaction().replace(R.id.content_frame, mCurrFragment).commit();

        // update selected item and title
        mDrawerList.setItemChecked(position, true);
        setTitle(mFragmentTitles[position]);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    public static void closeDrawer() {
    	
    	new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
          	
        	  ((DrawerLayout) mActivity.findViewById(R.id.drawer_layout)).closeDrawer(Gravity.LEFT);
          }
        }, 350);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}