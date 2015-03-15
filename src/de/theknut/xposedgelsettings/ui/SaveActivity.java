package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;

public class SaveActivity extends ListActivity {

    int mode, itemId;

    public static final int MODE_MANAGE_TAB = 3;
    public static final int MODE_MANAGE_FOLDER = 7;
    public static final int CONVERT_APPSWIDGETS = 4;
    public static final int MODE_PICK_COLOR = 5;
    public static final int MODE_PICK_GESTURE = 6;

    public static final int GESTURE_CALL = 20;

    ColorPickerDialog colorPickerDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);
        itemId = intent.getIntExtra("itemid", -1);

        SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();

        if (mode == MODE_MANAGE_TAB || mode == MODE_MANAGE_FOLDER) {
            String key = mode == MODE_MANAGE_TAB ? "appdrawertabdata" : "appdrawerfolderdata";
            ArrayList<String> order = intent.getStringArrayListExtra(mode == MODE_MANAGE_TAB ? "tabsdata" : "folderdata");
            editor.remove(key)
                    .putStringSet(key, new LinkedHashSet<String>(order))
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

        if (!(getIntent().hasExtra("initcolor") || mode == MODE_PICK_GESTURE)) {
            sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent().hasExtra("initcolor")) {
            colorPickerDialog = new ColorPickerDialog(this, getIntent().getIntExtra("initcolor", Color.WHITE));
            colorPickerDialog.setDefaultColor(Tab.DEFAULT_COLOR);
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
        } else if (mode == MODE_PICK_GESTURE) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.DARK)
                    .title("Pick gesture action")
                    .items(getResources().getStringArray(R.array.icongestures_entries))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which) {
                                case 0: // call contact
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                                    startActivityForResult(intent, GESTURE_CALL);
                            }
                            dialog.dismiss();
                        }
                    })
                    .build()
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            return;
        }

        if (requestCode == GESTURE_CALL) {
            Uri uri = data.getData();
            if (uri == null) return;

            Cursor c = null;
            try {
                c = getContentResolver().query(uri, new String[]{
                                ContactsContract.CommonDataKinds.Phone.NUMBER},
                        null, null, null);

                if (c != null && c.moveToFirst()) {
                    String key = "icongestures";
                    SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, CONTEXT_IGNORE_SECURITY);
                    Set<String> iconGestures = prefs.getStringSet(key, new HashSet<String>());

                    Iterator<String> it = iconGestures.iterator();
                    while (it.hasNext()) {
                        String[] split = it.next().split("#");
                        if (Integer.parseInt(split[0]) == itemId) {
                            it.remove();
                            break;
                        }
                    }

                    iconGestures.add(itemId + "#call#" + c.getString(0));
                    prefs.edit().remove(key)
                            .putStringSet(key, iconGestures)
                            .commit();

                    sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));
                    finish();
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }
}