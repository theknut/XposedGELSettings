package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.ui.preferences.SwitchCompatPreference;

@SuppressLint("WorldReadableFiles")
public class FragmentSettings extends FragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.settings_fragment);

        findPreference("nobackgroundimage").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                CommonUI.NO_BACKGROUND_IMAGE = (Boolean) newValue;
                return true;
            }
        });

        findPreference("forceenglishlocale").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                        .edit()
                        .putBoolean("forceenglishlocale", (Boolean) newValue)
                        .commit();
                CommonUI.restartActivity();
                return true;
            }
        });

        this.findPreference("autoblurimage").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                CommonUI.needFullReboot = true;
                Toast.makeText(mContext, R.string.toast_full_reboot, Toast.LENGTH_LONG).show();

                if ((Boolean) newValue) {
                    Intent myIntent = new Intent();
                    myIntent.setAction(Intent.ACTION_WALLPAPER_CHANGED);
                    mContext.sendBroadcast(myIntent);
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

        final SwitchCompatPreference debugPreference = (SwitchCompatPreference) this.findPreference("debug");
        debugPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if ((Boolean) newValue) {
                    new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .cancelable(false)
                            .title(R.string.alert_debug_logging_activated_title)
                            .content(R.string.alert_debug_logging_activated_summary)
                            .positiveText(android.R.string.ok)
                            .callback(new MaterialDialog.SimpleCallback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {
                                    CommonUI.restartLauncher(false);
                                }
                            })
                            .build()
                            .show();
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
                    new MaterialDialog.Builder(mActivity)
                            .theme(Theme.DARK)
                            .cancelable(false)
                            .autoDismiss(true)
                            .title(R.string.alert_debug_logging_not_activated_title)
                            .content(R.string.alert_debug_logging_not_activated_summary)
                            .positiveText(android.R.string.ok)
                            .build()
                            .show();

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

                File debugLogFile = new File(pathDebugLog);
                File xgelsPrefsFile = new File(pathXGELSPrefs);
                if (debugLogFile.exists() && xgelsPrefsFile.exists()) {
                    ArrayList<Uri> uris = new ArrayList<>();
                    uris.add(save(pathDebugLog));
                    uris.add(save(pathXGELSPrefs));

                    if (logfile.exists()) {
                        uris.add(Uri.fromFile(logfile));
                    }

                    char ls = '\n';
                    StringBuilder deviceInfo = new StringBuilder();
                    deviceInfo.append("Manufacturer: " + Build.MANUFACTURER).append(ls);
                    deviceInfo.append("Device: " + Build.DEVICE).append(ls);
                    deviceInfo.append("Model: " + Build.MODEL).append(ls);
                    deviceInfo.append("OS: " + Build.DISPLAY + " (" + Build.VERSION.RELEASE + ")").append(ls);

                    String version = null;
                    try {
                        PackageManager pkgMgr = mContext.getPackageManager();
                        PackageInfo packageInfo = pkgMgr.getPackageInfo(mContext.getPackageName(), 0);
                        version = "XGELS" + (InAppPurchase.isPremium ? " Premium" : "") + ": " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
                        deviceInfo.append(version).append(ls);

                        packageInfo = pkgMgr.getPackageInfo("de.robv.android.xposed.installer", 0);
                        version = "Xposed: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
                        deviceInfo.append(version).append(ls);

                        packageInfo = pkgMgr.getPackageInfo(Common.GEL_PACKAGE, 0);
                        version = "Google Search: " + packageInfo.versionName + " (" + packageInfo.versionCode + ")";
                        deviceInfo.append(version).append(ls);

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        ResolveInfo resolveInfo = pkgMgr.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        deviceInfo.append("Launcher: " + resolveInfo.activityInfo.name + " (" + resolveInfo.activityInfo.packageName + ")").append(ls);
                    } catch (NameNotFoundException e) {
                        // shouldn't be here but lets prevent this from crashing...
                    }

                    Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String[] recipients = {"theknutcoding+xgels.bugreport@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "XGELS Debug log");
                    intent.putExtra(Intent.EXTRA_TEXT, deviceInfo.toString() + '\n' + '\n' + "Bug report description: <short description>" + '\n' + '\n' + "Steps to reproduce: <repro steps>" + '\n' + '\n');
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    intent.setType("text/html");
                    startActivityForResult(Intent.createChooser(intent, "Send mail"), 0);

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
        }
        else {
            preferenceScreen.removePreference(this.findPreference("needsDonate"));
        }

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        return rootView;
    }

    @SuppressLint("DefaultLocale")
    private Uri save(String fromPath) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), "Access to SD denied", Toast.LENGTH_LONG).show();
            return null;
        }

        String filename = String.format(fromPath.substring(fromPath.lastIndexOf('/') + 1, fromPath.length()));
        File targetFile = new File(getActivity().getExternalFilesDir(null), filename);
        targetFile.getParentFile().mkdirs();

        try {
            FileInputStream in = new FileInputStream(new File(fromPath));
            FileOutputStream out = new FileOutputStream(targetFile);
            out.write(new StringBuilder(512).toString().getBytes());

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "Failed to copy file to SD \n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        return Uri.fromFile(targetFile);
    }
}