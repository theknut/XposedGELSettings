package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;

public class SaveActivity extends ListActivity {

    int mode;

    public static final int MODE_MANAGE_TAB = 3;
    public static final int CONVERT_APPSWIDGETS = 4;
    public static final int MODE_PICK_COLOR = 5;

    ColorPickerDialog colorPickerDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);

        SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();

        if (mode == MODE_MANAGE_TAB) {
            String key = "appdrawertabdata";
            ArrayList<String> tabOrder = new ArrayList<String>(prefs.getStringSet(key, new LinkedHashSet<String>()));

            if (intent.getBooleanExtra("newtab", false)) {
                tabOrder.add(new Tab(intent, false).toString());
            } else {
                tabOrder = intent.getStringArrayListExtra("tabsdata");
            }

            editor.remove(key)
                    .putStringSet(key, new LinkedHashSet<String>(tabOrder))
                    .commit();
        } else if (mode == CONVERT_APPSWIDGETS) {
            String key = "hiddenwidgets";
            Set<String> items = prefs.getStringSet(key, new HashSet<String>());
            Set<String> tmp = new HashSet<String>();

            if (items.size() != 0 && items.iterator().next().contains("#")) {
                Iterator<String> it = items.iterator();
                while (it.hasNext()) {
                    String[] item = it.next().split("#");
                    if (item.length > 1) {
                        tmp.add(item[0] + "/" + item[0] + item[1]);
                    }
                }

                prefs.edit().remove(key).commit();
                prefs.edit().putStringSet(key, tmp).commit();
            }

            key = "hiddenapps";
            items = prefs.getStringSet(key, new HashSet<String>());
            tmp = new HashSet<String>();

            if (items.size() != 0 && items.iterator().next().contains("#")) {
                PackageManager pm = getPackageManager();
                Iterator<String> it = items.iterator();
                while (it.hasNext()) {
                    String[] item = it.next().split("#");
                    if (item.length > 1) {
                        try {
                            tmp.add(pm.getLaunchIntentForPackage(item[0]).getComponent().flattenToString());
                        } catch (Exception e) { }
                    }
                }

                prefs.edit().remove(key).commit();
                prefs.edit().putStringSet(key, tmp).commit();
            }

            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
                if (Common.PACKAGE_NAMES.contains(process.processName)) {
                    am.killBackgroundProcesses(process.processName);
                }
            }
        }

        if (!getIntent().getBooleanExtra("keep", false)) {
            sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        colorPickerDialog = new ColorPickerDialog(this, getIntent().getIntExtra("initcolor", Color.WHITE));
        colorPickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                Intent intent = new Intent(Common.XGELS_ACTION_MODIFY_TAB);
                intent.putExtra("color", color);
                sendBroadcast(intent);
                finish();
            }
        });
        colorPickerDialog.show();
    }
}