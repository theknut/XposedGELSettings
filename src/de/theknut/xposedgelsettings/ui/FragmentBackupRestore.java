package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("SdCardPath")
public class FragmentBackupRestore extends FragmentBase {
	
	public FragmentBackupRestore() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.backuprestore_fragment);
        
        OnPreferenceClickListener BackupRestoreHomescreenListener = new OnPreferenceClickListener() {
        	
        	String copyCommand = "cat ";
        	
            @SuppressLint("WorldReadableFiles")
			public boolean onPreferenceClick(Preference preference) {
            	String path = getExternalSDCardDirectory();            	
            	String sd_trebuchet = path + "XposedGELSettings/Trebuchet/";
            	String data_trebuchet = "/data/data/" + Common.TREBUCHET_PACKAGE + "/";
            	String sd_gel = path + "XposedGELSettings/GEL/";
            	String data_gel = "/data/data/" + Common.GEL_PACKAGE + "/";
            	String sdcard_gel_launcherDB = sd_gel + "launcher.db";
            	String sdcard_gel_launcherDBJournal = sd_gel + "launcher.db-journal";
            	String data_gel_launcherDB = data_gel + "databases/launcher.db";
            	String data_gel_launcherDBJournal = data_gel + "databases/launcher.db-journal";
            	String sdcard_trebuchet_launcherDB = sd_trebuchet + "launcher.db";
            	String sdcard_trebuchet_launcherDBJournal = sd_trebuchet + "launcher.db-journal";
            	String data_trebuchet_launcherDB = data_trebuchet + "databases/launcher.db";
            	String data_trebuchet_launcherDBJournal = data_trebuchet + "databases/launcher.db-journal";
            	
            	//sdcard_xgels.getParentFile().mkdirs();
            	
            	if (preference.getKey().contains("restorehomescreen")) {
            		
					if (CommonUI.isPackageInstalled(Common.GEL_PACKAGE, mContext)) {
						File launcherDBJournal = new File(data_gel_launcherDBJournal);
						
						if (launcherDBJournal.length() == 0) {
							// copy from sdcard to data
							String sd2data_1 = copyCommand + sdcard_gel_launcherDB + " > " + data_gel_launcherDB;
							copy(Common.GEL_PACKAGE, sd2data_1, true);
						}
						else {
							// copy from sdcard to data
							String sd2data_1 = copyCommand + sdcard_gel_launcherDB + " > " + data_gel_launcherDB;
							String sd2data_2 = copyCommand + sdcard_gel_launcherDBJournal + " > " + data_gel_launcherDBJournal;
							copy(Common.GEL_PACKAGE, sd2data_1, sd2data_2, true);
						}
					}
					
					if (CommonUI.isPackageInstalled(Common.TREBUCHET_PACKAGE, mContext)) {
						File launcherDBJournal = new File(data_trebuchet_launcherDBJournal);
						
						if (launcherDBJournal.length() == 0) {
							// copy from sdcard to data
							String sd2data_3 = copyCommand + sdcard_trebuchet_launcherDB + " > " + data_trebuchet_launcherDB;
							copy(Common.TREBUCHET_PACKAGE, sd2data_3, true);
						}
						else {
							// copy from sdcard to data
							String sd2data_3 = copyCommand + sdcard_trebuchet_launcherDB + " > " + data_trebuchet_launcherDB;
							String sd2data_4 = copyCommand + sdcard_trebuchet_launcherDBJournal + " > " + data_trebuchet_launcherDBJournal;
							copy(Common.TREBUCHET_PACKAGE, sd2data_3, sd2data_4, true);
						}
					}
					
					restartLauncher();
            	}
            	else if (preference.getKey().contains("backuphomescreen")) {
            		
					if (CommonUI.isPackageInstalled(Common.GEL_PACKAGE, mContext)) {
						File launcherDBJournal = new File(data_gel_launcherDBJournal);
						
						if (launcherDBJournal.length() == 0) {
							String data2sd_1 = copyCommand + data_gel_launcherDB + " > " + sdcard_gel_launcherDB;
							//sdcard_gels.getParentFile().mkdirs();
							copy(Common.GEL_PACKAGE, data2sd_1, false);
						}
						else {
							
							String data2sd_1 = copyCommand + data_gel_launcherDB + " > " + sdcard_gel_launcherDB;
							String data2sd_2 = copyCommand + data_gel_launcherDBJournal + " > " + sdcard_gel_launcherDBJournal;
							//sdcard_gels.getParentFile().mkdirs();
							
							copy(Common.GEL_PACKAGE, data2sd_1, data2sd_2, false);
						}
					}
					
					if (CommonUI.isPackageInstalled(Common.TREBUCHET_PACKAGE, mContext)) {
						File launcherDBJournal = new File(data_trebuchet_launcherDBJournal);
						
						if (launcherDBJournal.length() == 0) {
							String data2sd_1 = copyCommand + data_trebuchet_launcherDB + " > " + sdcard_trebuchet_launcherDB;
							//sdcard_trebuchet.getParentFile().mkdirs();
							copy(Common.TREBUCHET_PACKAGE, data2sd_1, false);
						}
						else {
							String data2sd_3 = copyCommand + data_trebuchet_launcherDB + " > " + sdcard_trebuchet_launcherDB;
							String data2sd_4 = copyCommand + data_trebuchet_launcherDBJournal + " > " + sdcard_trebuchet_launcherDBJournal;
							//sdcard_trebuchet.getParentFile().mkdirs();
							copy(Common.TREBUCHET_PACKAGE, data2sd_3, data2sd_4, false);
						}
					}
            	}
            	
            	return true;
            }

            public boolean restartLauncher() {
            	 Context context = mContext;
	       		 ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	          	 String msg = "Killed:\n";
	          	 
	          	 List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
	          	 for (RunningAppProcessInfo process : processes) {
	          		 if (Common.PACKAGE_NAMES.contains(process.processName)) {   			 
	          			 am.killBackgroundProcesses(process.processName);
	          			 msg += process.processName + "\n"; 
	          		 }                        			 
	          	 }
	          	 
	          	 if (msg.equals("Killed:\n")) {
	          		 msg = msg.substring(0, msg.lastIndexOf('\n')) + " " + context.getString(R.string.toast_reboot_failed_nothing_msg) + "... :(\n" + context.getString(R.string.toast_reboot_failed);
	          	 }
	          	 else {
	          		 msg = msg.substring(0, msg.lastIndexOf('\n'));
	          	 }
	          	 
	          	 Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	          	 return true;
            }
            
            public boolean copy(String packageName, String command1, String command2, boolean setPermissions) {
            	
            	String data_launcherDB = "/data/data/" + packageName + "/databases/launcher.db";
            	String data_launcherDBJournal = "/data/data/" + packageName + "/databases/launcher.db-journal";
            	
            	ArrayList<String> commands = new ArrayList<String>();
            	commands.add("su");
            	commands.add("mkdir -p " + getExternalSDCardDirectory() + "/XposedGELSettings/GEL");
            	commands.add("mkdir -p " + getExternalSDCardDirectory() + "/XposedGELSettings/Trebuchet");
            	commands.add(command1);
            	commands.add(command2);

        	   if (setPermissions) {
        		   commands.add("chmod 0660 " + data_launcherDB);	
        		   commands.add("chmod 0700 " + data_launcherDBJournal);
    		   }
        	   
        	   CommonUI.openRootShell(commands.toArray(new String[commands.size()]));
        	   
        	   return true;
            }
            
            public boolean copy(String packageName, String command1, boolean setPermissions) {
 
            	String data_launcherDB = "/data/data/" + packageName + "/databases/launcher.db";
            	ArrayList<String> commands = new ArrayList<String>();
            	commands.add("su");
            	commands.add("mkdir -p " + getExternalSDCardDirectory() + "/XposedGELSettings/GEL");
            	commands.add("mkdir -p " + getExternalSDCardDirectory() + "/XposedGELSettings/Trebuchet");
            	commands.add(command1);
            	
            	if (setPermissions) {
         		   commands.add("chmod 0660 " + data_launcherDB);	
     		   }
        	   
        	   CommonUI.openRootShell(commands.toArray(new String[commands.size()]));
        	   
        	   return true;
            }
            
            public String getExternalSDCardDirectory()
            {            	
            	return "/mnt/sdcard/";
            }
        };
        
        this.findPreference("backuphomescreen").setOnPreferenceClickListener(BackupRestoreHomescreenListener);
        this.findPreference("restorehomescreen").setOnPreferenceClickListener(BackupRestoreHomescreenListener);

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
                        CommonUI.restartActivity();
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
        };

        this.findPreference("importsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        this.findPreference("exportsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        this.findPreference("resetsettings").setOnPreferenceClickListener(ImExportResetSettingsListener);
        
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        
        if (!InAppPurchase.isPremium) {        	
        	for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
        		Preference pref = preferenceScreen.getPreference(i);
        		
        		if (pref.hasKey() && pref.getKey().equals("needsDonate")) {
        			continue;
        		}
        		
        		pref.setEnabled(false);
        	}
        	
        	this.findPreference("needsDonate").setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					CommonUI.needFullReboot = true;
					return false;
				}
			});
        }
        else {
        	preferenceScreen.removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}