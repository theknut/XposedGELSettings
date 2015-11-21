package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.Utils;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Alexander Schulz on 29.07.2014.
 */
public class AppInfo extends HooksBaseClass {

    static Context SettingsContext;
    static Context XGELSContext;
    static LayoutInflater inflater;
    static Set<String> hiddenApps;

    public static void initAllHooks(LoadPackageParam lpparam) {

        final String key = "hiddenapps";

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            findAndHookMethod("com.android.settings.applications.InstalledAppDetails", lpparam.classLoader, "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (DEBUG) log(param, "AndroidSettings: Add \"Hide app\" checkbox");

                    if (inflater == null) {
                        SettingsContext = (Context) callMethod(param.thisObject, "getActivity");
                        XGELSContext = SettingsContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                        inflater = (LayoutInflater) XGELSContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    }

                    final SharedPreferences prefs = XGELSContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                    hiddenApps = prefs.getStringSet(key, new HashSet<String>());

                    Object mAppEntry = getObjectField(param.thisObject, "mAppEntry");
                    ApplicationInfo info = (ApplicationInfo) getObjectField(mAppEntry, "info");
                    // Application doesn't have a launch intent, nothing to do
                    Intent launchIntent = SettingsContext.getPackageManager().getLaunchIntentForPackage(info.packageName);
                    if (launchIntent == null) {
                        return;
                    }

                    CheckBox mNotificationSwitch = (CheckBox) getObjectField(param.thisObject, "mNotificationSwitch");
                    ViewGroup parent = (ViewGroup) mNotificationSwitch.getParent();

                    CheckBox checkbox = new CheckBox(parent.getContext());
                    checkbox.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    checkbox.setText(XGELSContext.getResources().getString(R.string.hide_app_switch_label));
                    checkbox.setTag(launchIntent.getComponent().flattenToString());
                    checkbox.setChecked(hiddenApps.contains(checkbox.getTag()));
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            hiddenApps = prefs.getStringSet(key, new HashSet<String>());
                            if (isChecked) {
                                if (!hiddenApps.contains(buttonView.getTag())) {
                                    // app is not in the list, so lets add it
                                    hiddenApps.add((String) buttonView.getTag());
                                }
                            } else {
                                if (hiddenApps.contains(buttonView.getTag())) {
                                    // app is in the list but the checkbox is no longer checked, we can remove it
                                    hiddenApps.remove(buttonView.getTag());
                                }
                            }

                            Utils.saveToSettings(SettingsContext, key, hiddenApps, true);
                        }
                    });

                    parent.addView(checkbox);
                }
            });
        } else if (false) {
            findAndHookMethod("com.android.settings.applications.InstalledAppDetails", lpparam.classLoader, "onActivityCreated", Bundle.class, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (DEBUG) log(param, "AndroidSettings: Add \"Hide app\" checkbox");

                    if (inflater == null) {
                        SettingsContext = (Context) callMethod(param.thisObject, "getActivity");
                        XGELSContext = SettingsContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                        inflater = (LayoutInflater) XGELSContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    }

                    final SharedPreferences prefs = XGELSContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                    hiddenApps = prefs.getStringSet(key, new HashSet<String>());

                    Object mAppEntry = getObjectField(param.thisObject, "mAppEntry");
                    ApplicationInfo info = (ApplicationInfo) getObjectField(mAppEntry, "info");
                    // Application doesn't have a launch intent, nothing to do
                    Intent launchIntent = SettingsContext.getPackageManager().getLaunchIntentForPackage(info.packageName);
                    if (launchIntent == null) {
                        return;
                    }

                    Button mForceStopButton = (Button) getObjectField(param.thisObject, "mForceStopButton");
                    ViewGroup parent = (ViewGroup) mForceStopButton.getParent().getParent();

                    CheckBox checkbox = new CheckBox(parent.getContext());
                    checkbox.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    checkbox.setText(XGELSContext.getResources().getString(R.string.hide_app_switch_label));
                    checkbox.setTag(launchIntent.getComponent().flattenToString());
                    log("hiddenApps: " + hiddenApps);
                    checkbox.setChecked(hiddenApps.contains(checkbox.getTag()));
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            hiddenApps = prefs.getStringSet(key, new HashSet<String>());
                            if (isChecked) {
                                if (!hiddenApps.contains(buttonView.getTag())) {
                                    // app is not in the list, so lets add it
                                    hiddenApps.add((String) buttonView.getTag());
                                }
                            } else {
                                if (hiddenApps.contains(buttonView.getTag())) {
                                    // app is in the list but the checkbox is no longer checked, we can remove it
                                    hiddenApps.remove(buttonView.getTag());
                                }
                            }

                            Utils.saveToSettings(SettingsContext, key, hiddenApps, true);
                        }
                    });

                    parent.addView(checkbox);
                }
            });
        }
    }
}