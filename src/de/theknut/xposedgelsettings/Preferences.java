package de.theknut.xposedgelsettings;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

public class Preferences extends Activity {
	
	public static boolean toastShown = false;
	public static Context mContext;
	public SharedPreferences prefs;
	
	public Preferences () {	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = getApplicationContext();
		
		if (savedInstanceState == null)
			getFragmentManager().beginTransaction().replace(R.id.container, new PrefsFragment()).commit();
	}
	
	public static class PrefsFragment extends PreferenceFragment {
		
        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
            addPreferencesFromResource(R.xml.prefs);
            
            OnPreferenceChangeListener onChangeListenerSwitch = new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    
                    String key = preference.getKey();
                    SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
            		SharedPreferences.Editor editor = prefs.edit();
            		editor.remove(key);
            		editor.commit();
            		editor.putBoolean(key, (Boolean) newValue);
            		editor.commit();
                    
                    if (!toastShown) {                 
                    	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    	toastShown = true;
                    }
                    
                    return true;
                }
            };
                        
            Preference pref = this.findPreference("hidesearchbar");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("autohidehidesearchbar");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("hideiconhomescreen");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("hideiconappdrawer");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("changegridsize");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("changehotseatcount");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("restartlauncher");
            pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                         public boolean onPreferenceClick(Preference preference) {
                        	 ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        	 String msg = "Killed:\n";
                        	 
                        	 List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
                        	 for (RunningAppProcessInfo process : processes) {
                        		 if (Common.PACKAGE_NAMES.contains(process.processName)) {
                        			 am.killBackgroundProcesses(process.processName);
                        			 msg += process.processName + "\n"; 
                        		 }                        			 
                        	 }
                        	 
                        	 if (msg.equals("Killed:\n")) {
                        		 msg = msg.substring(0, msg.lastIndexOf('\n')) + " " + getString(R.string.toast_reboot_failed_nothing_msg) + "... :(\n" + getString(R.string.toast_reboot_failed);
                        	 }
                        	 else {
                        		 msg = msg.substring(0, msg.lastIndexOf('\n'));
                        	 }
                        	 
                        	 Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                        	 return true;
                         }
                     });
        }
    }
}