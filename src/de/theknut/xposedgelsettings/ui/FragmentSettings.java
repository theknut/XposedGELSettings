package de.theknut.xposedgelsettings.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentSettings extends FragmentBase {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.settings_fragment);
        
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
            		}
            		else {
            			Toast.makeText(mContext, getString(R.string.toast_reset_failed), Toast.LENGTH_LONG).show();
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
            
            public int chmod(File path, int mode) throws Exception {
                Class<?> fileUtils = Class.forName("android.os.FileUtils");
                Method setPermissions = fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
                return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
            }
            
            private void restartActivity() {					
				Intent mStartActivity = new Intent(mContext, MainActivity.class);
				int mPendingIntentId = 0xBEEF;
				PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
				AlarmManager mgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
				mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
				System.exit(0);
			}
        };
        
        this.findPreference("importsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        this.findPreference("exportsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        this.findPreference("resetsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        
        this.findPreference("autoblurimage").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CommonUI.needFullReboot = true;
				Toast.makeText(mContext, R.string.toast_full_reboot, Toast.LENGTH_LONG).show();
				
				if ((Boolean) newValue) {
					Intent myIntent = new Intent();				
					myIntent.setAction(Intent.ACTION_WALLPAPER_CHANGED);
					CommonUI.CONTEXT.sendBroadcast(myIntent);
				}
				
				return true;
			}
		});
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        if (!InAppPurchase.isDonate) {        	
        	for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
        		Preference pref = preferenceScreen.getPreference(i);
        		
        		if (pref.hasKey() && pref.getKey().equals("needsDonate")) {
        			continue;
        		}
        		
        		pref.setEnabled(false);
        	}
        }
        else {
        	preferenceScreen.removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}