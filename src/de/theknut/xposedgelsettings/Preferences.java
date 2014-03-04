package de.theknut.xposedgelsettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.theknut.xposedgelsettings.hooks.Common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // action with ID action_refresh was selected
	    case R.id.action_refresh:
	      restartLauncher();
	      break;
	    default:
	      break;
	    }

	    return true;
	  }
	
	private static boolean restartLauncher() {
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
    		 msg = msg.substring(0, msg.lastIndexOf('\n')) + " " + mContext.getString(R.string.toast_reboot_failed_nothing_msg) + "... :(\n" + mContext.getString(R.string.toast_reboot_failed);
    	 }
    	 else {
    		 msg = msg.substring(0, msg.lastIndexOf('\n'));
    	 }
    	 
    	 Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    	 return true;
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
                    
                    if (!toastShown) {                 
                    	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    	toastShown = true;
                    }
                    
                    return true;
                }
            };
            
            this.findPreference("hidesearchbar").setOnPreferenceChangeListener(onChangeListenerSwitch);            
            this.findPreference("autohidehidesearchbar").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("hideiconhomescreen").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("hideiconappdrawer").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("changegridsizehome").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("changegridsizeapps").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("iconsettingsswitchapps").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("iconsettingsswitchhome").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("hidepageindicator").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("resizeallwidgets").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("homescreeniconlabelshadow").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("longpressallappsbutton").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("disablewallpaperscroll").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("lockhomescreen").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("continuousscroll").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("continuousscrollwithappdrawer").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("closeappdrawerafterappstarted").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("gestureswipedownleft").setOnPreferenceChangeListener(onChangeListenerSwitch);
            this.findPreference("gestureswipedownright").setOnPreferenceChangeListener(onChangeListenerSwitch);
            
            final Preference swipeuphotseat = this.findPreference("gestureswipeuphotseat");
            final Preference swipeupappdrawer = this.findPreference("gestureswipeupappdrawer");
            
            OnPreferenceChangeListener onChangeListenerSwitchSwipeUp = new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    
                    if (!toastShown) {                 
                    	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    	toastShown = true;
                    }
                    
                    if ((Boolean) newValue) {                		
                		if (preference.getKey().equals(swipeuphotseat.getKey())) {
                			((CustomSwitchPreference) swipeupappdrawer).setChecked(false);
                		}
                		else if (preference.getKey().equals(swipeupappdrawer.getKey())) {
                			((CustomSwitchPreference) swipeuphotseat).setChecked(false);
                		}
                    }
                    
                    return true;
                }
            };
            
            swipeuphotseat.setOnPreferenceChangeListener(onChangeListenerSwitchSwipeUp);
            swipeupappdrawer.setOnPreferenceChangeListener(onChangeListenerSwitchSwipeUp);
            
            this.findPreference("hidehotseat").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    
                    if (!toastShown) {                 
                    	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
                    	toastShown = true;
                    }
                    
                    if ((Boolean) newValue) {                		       		
            			((CustomSwitchPreference) swipeupappdrawer).setChecked(false);
            			((CustomSwitchPreference) swipeuphotseat).setChecked(true);
                    }
                    else {
                    	((CustomSwitchPreference) swipeuphotseat).setChecked(false);
                    	((CustomSwitchPreference) swipeupappdrawer).setChecked(true);
                    }
                    
                    return true;
                }});
            
            this.findPreference("restartlauncher").setOnPreferenceClickListener(new OnPreferenceClickListener() {
                         public boolean onPreferenceClick(Preference preference) {
                        	 return restartLauncher();
                         }
                     });
            
            OnPreferenceClickListener ImExportResetSettingsListener = new OnPreferenceClickListener() {
                @SuppressLint("WorldReadableFiles")
				public boolean onPreferenceClick(Preference preference) {
                	
                	File sdcard = new File(Environment.getExternalStorageDirectory().getPath() + "/XposedGELSettings/" + Common.PREFERENCES_NAME + ".xml");
                	File data = new File(mContext.getFilesDir(), "../shared_prefs/"+ Common.PREFERENCES_NAME + ".xml");
                	
                	sdcard.getParentFile().mkdirs();
                	data.getParentFile().mkdirs();
                	
                	if (preference.getKey().contains("importsettings")) {
                		boolean success = false;
                		
						try {
							// copy from sdcard to data
							success = copy(sdcard, data);
							
							// set permissions
							chmod(new File (mContext.getFilesDir(), "../shared_prefs"), 0771);
							chmod(data, 0664);							
						} catch (Exception e) {
							e.printStackTrace();
						}
                		
                		if (success) {
                			Toast.makeText(mContext, getString(R.string.toast_import), Toast.LENGTH_LONG).show();
                			restartActivity();
                		}
                		else {
                			Toast.makeText(mContext, getString(R.string.toast_import_failed), Toast.LENGTH_LONG).show();
                		}
                	}
                	else if (preference.getKey().contains("exportsettings")) {
                		boolean success = false;
                		
						try {
							// copy from data to sdcard
							success = copy(data, sdcard);
						} catch (IOException e) {
							e.printStackTrace();
						}
                		
                		if (success) {
                			Toast.makeText(mContext, getString(R.string.toast_export), Toast.LENGTH_LONG).show();
                		}
                		else {
                			Toast.makeText(mContext, getString(R.string.toast_export_failed), Toast.LENGTH_LONG).show();
                		}
                	}
                	else if (preference.getKey().contains("resetsettings")) {
                		// reset your settings, I mean you wanted that so lets do it!
                		boolean success = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit().clear().commit();
                		
                		if (success) {
                			Toast.makeText(mContext, getString(R.string.toast_reset), Toast.LENGTH_LONG).show();
                			restartActivity();
                		}
                		else {
                			Toast.makeText(mContext, getString(R.string.toast_reset_failed), Toast.LENGTH_LONG).show();
                		}
                	}
                	
                	return true;
                }

				private void restartActivity() {					
					Intent mStartActivity = new Intent(mContext, Preferences.class);
					int mPendingIntentId = 0xB00B5; // tell me when you've found this - make a post only saying "Banana" in the support thread and you'll get a cookie! Honestly!
					PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
					AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
					mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
					System.exit(0);
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
                
                public int chmod(File path, int mode) throws Exception {
                    Class<?> fileUtils = Class.forName("android.os.FileUtils");
                    Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
                    return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
                }
            };
            
            this.findPreference("importsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
            this.findPreference("exportsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
            this.findPreference("resetsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        }
    }
}