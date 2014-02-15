package de.theknut.xposedgelsettings;

import static de.robv.android.xposed.XposedHelpers.getShortField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
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
            
            Preference pref = this.findPreference("iconsettingsswitch");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("hidesearchbar");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("autohidehidesearchbar");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("hideiconhomescreen");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("hideiconappdrawer");
            pref.setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            pref = this.findPreference("changegridsize");
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
            
            OnPreferenceClickListener ImExportSettingsListener = new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                	
                	File sdcard = new File(Environment.getExternalStorageDirectory().getPath() + "/XposedGELSettings/" + Common.PREFERENCES_NAME + ".xml");
                	File data = new File(mContext.getFilesDir(), "../shared_prefs/"+ Common.PREFERENCES_NAME + ".xml");
                	
                	sdcard.getParentFile().mkdirs();
                	data.getParentFile().mkdirs();
                	
                	if (preference.getKey().contains("import")) {
                		boolean success = false;
                		
						try {
							success = copy(sdcard, data);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		
                		if (success) {
                			Toast.makeText(mContext, R.string.toast_import, Toast.LENGTH_LONG).show();
                			
                			Intent mStartActivity = new Intent(mContext, Preferences.class);
                			int mPendingIntentId = 0xB00B5; // tell me when you've found this
                			PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                			AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
                			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                			System.exit(0);
                		}
                	}
                	else if (preference.getKey().contains("export")) {
                		boolean success = false;
                		
						try {
							success = copy(data, sdcard);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		
                		if (success) {
                			Toast.makeText(mContext, R.string.toast_export, Toast.LENGTH_LONG).show();
                		}
                	}
                	
                	return true;
                }
                
                public boolean copy(File src, File dst) throws IOException {
                	
                	if (!src.exists()) {
                		src.createNewFile();
                	}
                	if (!dst.exists()) {
                		dst.createNewFile();
                	}
                	
                    InputStream in = new FileInputStream(src);
                    OutputStream out = new FileOutputStream(dst);

                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    
                    in.close();
                    out.close();
                    
                    return true;
                }
            };
            
            
            pref = this.findPreference("importsettings");
            pref.setOnPreferenceClickListener(ImExportSettingsListener);
            
            pref = this.findPreference("exportsettings");
            pref.setOnPreferenceClickListener(ImExportSettingsListener);
        }
    }
}