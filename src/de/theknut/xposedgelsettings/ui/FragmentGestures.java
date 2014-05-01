package de.theknut.xposedgelsettings.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class FragmentGestures extends FragmentBase {
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.gestures_fragment);
        
        MyCheckboxPreference gesture_double_tap_only_on_wallpaper = (MyCheckboxPreference) this.findPreference("gesture_double_tap_only_on_wallpaper");
        MyListPreference gesture_double_tap = (MyListPreference) this.findPreference("gesture_double_tap");
        MyListPreference gesture_one_down_left = (MyListPreference) this.findPreference("gesture_one_down_left");
        MyListPreference gesture_one_down_middle = (MyListPreference) this.findPreference("gesture_one_down_middle");
        MyListPreference gesture_one_down_right = (MyListPreference) this.findPreference("gesture_one_down_right");
        final MyListPreference gesture_one_up_left = (MyListPreference) this.findPreference("gesture_one_up_left");
        MyListPreference gesture_one_up_middle = (MyListPreference) this.findPreference("gesture_one_up_middle");
        final MyListPreference gesture_one_up_right = (MyListPreference) this.findPreference("gesture_one_up_right");
        CustomSwitchPreference gesture_appdrawer =  (CustomSwitchPreference) this.findPreference("gesture_appdrawer");
        
        gesture_appdrawer.setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        List<MyListPreference> prefs = new ArrayList<MyListPreference>();
        prefs.add(gesture_double_tap);
        prefs.add(gesture_one_down_left);        
        prefs.add(gesture_one_down_middle);
        prefs.add(gesture_one_down_right);
        prefs.add(gesture_one_up_left);        
        prefs.add(gesture_one_up_middle);
        prefs.add(gesture_one_up_right);
        
        for (MyListPreference pref : prefs) {
        	
        	pref.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    
                    MyListPreference pref = (MyListPreference) preference;
                    
                    if (((String) newValue).equals("APP")) {
                    	
	                    Intent intent = new Intent(mContext, ChooseAppList.class);
	                    intent.putExtra("gesture", preference.getKey());
	                    startActivityForResult(intent, 0);
	                    
                    } else if (((String)newValue).equals("TOGGLE_DOCK")
                    		&& preference.getKey().equals("gesture_double_tap")) {
                    	
                    	Toast.makeText(mContext, "Double tap to toggle dock is currently not supported :(", Toast.LENGTH_LONG).show();
                    	return false;
                    	
                    } else {
                    	
                    	if (!toastShown) {                 
                        	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                        	toastShown = true;
                        }
                    }
                    
                    pref.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
                    
                    return true;
                }
            });
        	
        	if (pref.getValue().equals("APP")) {
        		pref.setSummary(pref.getEntry() + " - " + getAppName(pref.getKey()));
        	} else {
        		pref.setSummary(pref.getEntry());
        	}
        }
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        if (!InAppPurchase.isDonate) {  
        	
        	gesture_double_tap.setEnabled(false);      
            gesture_one_down_middle.setEnabled(false);
            gesture_one_up_left.setEnabled(false);    
            gesture_one_up_right.setEnabled(false);
            gesture_double_tap_only_on_wallpaper.setEnabled(false);
            
            gesture_one_up_middle.setEntryValues(R.array.gesture_actions_values_limited);
            gesture_one_up_middle.setEntries(R.array.gesture_actions_entries_limited);
            gesture_one_up_middle.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					
					MyListPreference pref = (MyListPreference) preference;
					pref.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
					
					gesture_one_up_left.setValue((String) newValue);
					gesture_one_up_right.setValue((String) newValue);
					gesture_one_up_left.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
					gesture_one_up_right.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
					
					if (!toastShown) {                 
                    	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    	toastShown = true;
                    }
					
					return true;
				}
			});
        }
        else {
        	preferenceScreen.removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
        
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if (data == null) return;
    	
    	String gestureKey = data.getStringExtra("gesture");
    	
    	if (!gestureKey.equals("")) {
	    	MyListPreference pref = (MyListPreference) this.findPreference(gestureKey);
			String newSummary = pref.getSummary() + " - " + getAppName(gestureKey);
			pref.setSummary(newSummary);
			
			if (requestCode == Activity.RESULT_OK) {
				if (!toastShown) {                 
	            	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
	            	toastShown = true;
	            }
			}
    	}
    }
    
    @SuppressWarnings("deprecation")
	private String getAppName(String gestureKey) {
    	final PackageManager pm = mContext.getPackageManager();
    	String app = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(gestureKey + "_launch", "");
    	
    	ApplicationInfo ai;
    	try {
    	    ai = pm.getApplicationInfo(app, 0);
    	} catch (final NameNotFoundException e) {
    	    ai = null;
    	}
    	
    	return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }
}