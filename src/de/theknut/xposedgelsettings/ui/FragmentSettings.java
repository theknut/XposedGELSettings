package de.theknut.xposedgelsettings.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class FragmentSettings extends FragmentBase {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.settings_fragment);
        
        OnPreferenceClickListener ImExportResetSettingsListener = new OnPreferenceClickListener() {
            @SuppressWarnings("deprecation")
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
            			CommonUI.restartLauncher(false);
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
        
        this.findPreference("showchangelog").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Changelog cl = new Changelog(mContext);
				cl.getFullLogDialog().show();
				return true;
			}
		});
        
        final MyCheckboxPreference debugPreference = (MyCheckboxPreference) this.findPreference("debug");
        debugPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				
				if ((Boolean) newValue) {
					new AlertDialog.Builder(CommonUI.CONTEXT)
					.setCancelable(false)
				    .setTitle(R.string.alert_debug_logging_activated_title)
				    .setMessage(R.string.alert_debug_logging_activated_summary)
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	CommonUI.restartLauncher(false);
				        }
			        }).show();
				}
				
				return true;
			}
		});
        
        this.findPreference("sendbugreport").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("SdCardPath")
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				boolean debugLoggingEnabled = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getBoolean("debug", false);
				
				if (!debugLoggingEnabled) {
					new AlertDialog.Builder(CommonUI.CONTEXT)
					.setCancelable(false)
				    .setTitle(R.string.alert_debug_logging_not_activated_title)
				    .setMessage(R.string.alert_debug_logging_not_activated_summary)
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	// do nothing
				        }
			        }).show();
					
					return false;
				}
				
				String pathDebugLog = null;
				String pathXGELSPrefs = mContext.getApplicationInfo().dataDir + "/shared_prefs/de.theknut.xposedgelsettings_preferences.xml";
				
				try {
					Context xposedInstallerContext = mContext.createPackageContext("de.robv.android.xposed.installer", Context.CONTEXT_IGNORE_SECURITY);
					pathDebugLog = xposedInstallerContext.getApplicationInfo().dataDir + "/log/error.log";
				} catch (Exception e) {
					e.printStackTrace();
					pathDebugLog = "/data/data/de.robv.android.xposed.installer/log/error.log";
				}
				
				if (!new File(pathDebugLog).exists()) {
					pathDebugLog.replace("error.log", "debug.log");
				}
				
				String logfilePath = "/mnt/sdcard/XposedGELSettings/logs/logcat.log";
				File logfile = new File(logfilePath);
				
				if (logfile.exists()) {
					logfile.delete();
				}
				
				logfile.getParentFile().mkdirs();
				
				try {
					logfile.createNewFile();
					Process process = Runtime.getRuntime().exec("logcat -v time -d");
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					StringBuilder log = new StringBuilder();
					String line;
					
					while ((line = bufferedReader.readLine()) != null) {
						log.append(line).append('\n');
					}
					
					FileWriter out = new FileWriter(logfile);
		            out.write(log.toString());
		            out.close();
				} catch (IOException e) {}
				
				if (new File(pathDebugLog).exists() && new File(pathXGELSPrefs).exists()) {	
					
					ArrayList<Uri> uris = new ArrayList<Uri>();
					uris.add(Uri.parse("file://" + pathDebugLog));
					uris.add(Uri.parse("file://" + pathXGELSPrefs));
					
					if (logfile.exists()) {
						uris.add(Uri.parse("file://" + logfilePath));
					}
					
					char ls = '\n';
					StringBuilder deviceInfo = new StringBuilder();
					deviceInfo.append("Manufacturer: " + Build.MANUFACTURER).append(ls);
					deviceInfo.append("Device: " + Build.DEVICE).append(ls);
					deviceInfo.append("Model: " + Build.MODEL).append(ls);
					deviceInfo.append("OS: " + Build.DISPLAY + " (" + Build.VERSION.RELEASE + ")").append(ls);
					
				    String version = null;
				    try {
				    	PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
				    	version = "XGELS: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
				    	deviceInfo.append(version).append(ls);
				    	
				    	packageInfo = mContext.getPackageManager().getPackageInfo("de.robv.android.xposed.installer", 0);
				    	version = "Xposed: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
				    	deviceInfo.append(version).append(ls);
				    	
				    	packageInfo = mContext.getPackageManager().getPackageInfo(Common.GEL_PACKAGE, 0);
				    	version = "GNL: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
				    	deviceInfo.append(version).append(ls);
				    } catch (NameNotFoundException e) {
				        // shouldn't be here but lets prevent this from crashing...
				    }
					
					Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String[] recipients = {"theknutcoding@gmail.com"};
					intent.putExtra(Intent.EXTRA_EMAIL, recipients);
					intent.putExtra(Intent.EXTRA_SUBJECT, "XGELS Debug log");
					intent.putExtra(Intent.EXTRA_TEXT, "Bug report description: <short description>" + '\n' + '\n' + "Steps to reproduce: <repro steps>" + '\n' + '\n' + deviceInfo.toString());
					intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
					intent.setType("text/html");
					startActivity(Intent.createChooser(intent, "Send mail"));
					
					debugPreference.setChecked(false);
					CommonUI.restartLauncher(false);
					
					return true;
				}
				
				Toast.makeText(mContext, getString(R.string.toast_no_log_found), Toast.LENGTH_LONG).show();
				return false;				
			}
		});
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        if (!InAppPurchase.isPremium) {
        	this.findPreference("autoblurimage").setEnabled(false);
        	this.findPreference("importsettings").setEnabled(false);
            this.findPreference("exportsettings").setEnabled(false);
            this.findPreference("resetsettings").setEnabled(false);
        }
        else {
        	preferenceScreen.removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}