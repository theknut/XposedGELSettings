package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentHomescreen extends FragmentBase {
	
	public FragmentHomescreen() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.homescreen_fragment);
        
        this.findPreference("hideiconhomescreen").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("changegridsizehome").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("iconsettingsswitchhome").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("homescreeniconlabelshadow").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("noallappsbutton").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        this.findPreference("hideappdock").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                
                if (!toastShown) {                 
                	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                	toastShown = true;
                }
                
                if ((Boolean) newValue) {
        			SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.remove("gestureswipeupappdock");
            		editor.remove("gestureswipeupappdrawer");
            		editor.apply();
            		editor.putBoolean("gestureswipeupappdock", true);
            		editor.putBoolean("gestureswipeupappdrawer", false);
            		editor.apply();
                }
                else {
                	SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.remove("gestureswipeupappdock");
            		editor.remove("gestureswipeupappdrawer");
            		editor.apply();
            		editor.putBoolean("gestureswipeupappdock", false);
            		editor.putBoolean("gestureswipeupappdrawer", true);
            		editor.apply();
                }
                
                return true;
            }});
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}