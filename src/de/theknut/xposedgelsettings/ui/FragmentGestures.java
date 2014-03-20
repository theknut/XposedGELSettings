package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;

public class FragmentGestures extends FragmentBase {
	
	public FragmentGestures() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.gestures_fragment);
        
        this.findPreference("gestureswipedownleft").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("gestureswipedownright").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("gestureswipedowncloseappdrawer").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        final Preference swipeupappdock = this.findPreference("gestureswipeupappdock");
        final Preference swipeupappdrawer = this.findPreference("gestureswipeupappdrawer");
        
        OnPreferenceChangeListener onChangeListenerSwitchSwipeUp = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                
                if (!toastShown) {                 
                	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                	toastShown = true;
                }
                
                if ((Boolean) newValue) {                		
            		if (preference.getKey().equals(swipeupappdock.getKey())) {
            			((CustomSwitchPreference) swipeupappdrawer).setChecked(false);
            		}
            		else if (preference.getKey().equals(swipeupappdrawer.getKey())) {
            			((CustomSwitchPreference) swipeupappdock).setChecked(false);
            		}
                }
                
                return true;
            }
        };
        
        swipeupappdock.setOnPreferenceChangeListener(onChangeListenerSwitchSwipeUp);
        swipeupappdrawer.setOnPreferenceChangeListener(onChangeListenerSwitchSwipeUp);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}