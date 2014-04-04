package de.theknut.xposedgelsettings.ui;

import de.theknut.xposedgelsettings.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentBase extends PreferenceFragment {
	
	public static Context mContext;	
	public static boolean toastShown = false;
	public static String TAG = "XGELS";
	
	OnPreferenceChangeListener onChangeListenerLauncherReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
            if (!toastShown) {                 
            	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
            	toastShown = true;
            }
            
            return true;
        }
    };
    
    OnPreferenceChangeListener onChangeListenerFullReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
            if (!toastShown) {                 
            	Toast.makeText(mContext, R.string.toast_full_reboot, Toast.LENGTH_LONG).show();
            	toastShown = true;
            }
            
            CommonUI.needFullReboot = true;
            return true;
        }
    };
    
    public FragmentBase() { }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
    	return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	mContext = activity;
    }
}
