package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.ui.ImageLoader.ViewHolder;

public class AllAppsList extends ListActivity {


    private Activity mActivity;
    private List<String> apps;

    static List<String> initialItems, itemsToAdd, itemsToRemove;
    static String appComponentName;
    static long itemID;
    static int mode;

    private Intent responseIntent;

    private String tabName;
    private boolean newTab;

    public static final int MODE_PICK_APPS_TO_HIDE = 1;
    public static final int MODE_SELECT_FOLDER_APPS = 2;
    public static final int MODE_MANAGE_TAB = 3;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        appComponentName = intent.getStringExtra("app");
        mode = intent.getIntExtra("mode", 1);

        if (mode != MODE_PICK_APPS_TO_HIDE) {
            responseIntent = new Intent();
            itemID = intent.getLongExtra("itemtid", 0);
            initialItems = intent.getStringArrayListExtra("items");
            apps = new ArrayList<String>(initialItems);
            itemsToAdd = new ArrayList<String>();
            itemsToRemove = new ArrayList<String>();
            newTab = intent.getBooleanExtra("newtab", false);
            tabName = intent.getStringExtra("tabname");
        }

        CommonUI.CONTEXT = mActivity = this;

        getListView().setCacheColorHint(CommonUI.UIColor);
        getListView().setBackgroundColor(CommonUI.UIColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager(), CommonUI.getAllApps());
        setListAdapter(adapter);

        if (mode == MODE_MANAGE_TAB) {
            setupManageTabAlert();
        }
    }

    private void setupManageTabAlert() {
        ViewGroup alertTitle = (ViewGroup) getLayoutInflater().inflate(R.layout.tab_settings_title, null);

        final ImageView deleteTab = (ImageView) alertTitle.findViewById(R.id.deletetab);
        deleteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String key = "appdrawertaborder";
                    SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, MODE_WORLD_READABLE);
                    LinkedHashSet<String> tabOrder = new LinkedHashSet<String>(prefs.getStringSet(key, new LinkedHashSet<String>()));

                    if (tabOrder.contains(tabName)) {
                        tabOrder.remove(tabName);
                        prefs.edit().remove(key).apply();
                    }

                    prefs.edit().remove("tab_" + tabName).apply();

                    responseIntent.setAction(Common.XGELS_ACTION_MODIFY_TAB);
                    responseIntent.putExtra("delete", true);
                    responseIntent.putExtra("tabname", tabName);
                    sendBroadcast(responseIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finish();
            }
        });
        deleteTab.setVisibility(!newTab ? View.VISIBLE : View.GONE);

        final EditText editText = (EditText) alertTitle.findViewById(R.id.tabname);
        editText.setText(tabName);

        new AlertDialog.Builder(this)
                //.setCustomTitle(alertTitle)
                .setTitle("Jo")
                .setView(alertTitle)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String key = "appdrawertaborder";
                        String newTabName = editText.getText().toString().trim().toLowerCase(Locale.US);
                        if (newTabName.length() != 0) {
                            SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, MODE_WORLD_READABLE);
                            LinkedHashSet<String> tabOrder = new LinkedHashSet<String>(prefs.getStringSet(key, new LinkedHashSet<String>()));

                            if (!newTab && !tabName.equals(newTabName)) {
                                if (tabOrder.contains(tabName)) {
                                    tabOrder.remove(tabName);
                                }
                                responseIntent.putExtra("oldtabname", tabName);
                                responseIntent.putExtra("rename", true);
                                Set<String> tabData = prefs.getStringSet("tab_" + tabName, null);
                                prefs.edit().remove("tab_" + tabName).apply();
                                prefs.edit().putStringSet("tab_" + newTabName, tabData).apply();
                            }

                            tabName = newTabName;
                            tabOrder.add(tabName);
                            prefs.edit().remove(key).apply();
                            prefs.edit().putStringSet(key, tabOrder).apply();
                        } else {
                            mActivity.finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        mActivity.finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();

        if (mode == MODE_PICK_APPS_TO_HIDE) {
            // save our new list
            SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
            editor.remove("hiddenapps").commit();
            editor.putStringSet("hiddenapps", new HashSet<String>(apps)).commit();
        }
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        if (mode == MODE_PICK_APPS_TO_HIDE) {
            // get our hidden app list
            apps = new ArrayList<String>(
                    getSharedPreferences(
                            Common.PREFERENCES_NAME,
                            Context.MODE_WORLD_READABLE
                    ).getStringSet(
                            "hiddenapps",
                            new HashSet<String>()
                    )
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (mode != MODE_PICK_APPS_TO_HIDE) {
            menu.findItem(R.id.action_refresh).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                CommonUI.CONTEXT = this;
                CommonUI.restartLauncherOrDevice();
                break;
            case R.id.action_save:

                for (String folderItem : apps) {
                    if (!initialItems.contains(folderItem)) {
                        itemsToAdd.add(folderItem);
                    }
                }

                for (String folderItem : initialItems) {
                    if (!apps.contains(folderItem)) {
                        itemsToRemove.add(folderItem);
                    }
                }

                if (mode == MODE_SELECT_FOLDER_APPS) {
                    responseIntent.setAction(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS);
                    responseIntent.putExtra("itemid", itemID);
                    responseIntent.putStringArrayListExtra("additems", new ArrayList<String>(itemsToAdd));
                    responseIntent.putStringArrayListExtra("removeitems", new ArrayList<String>(itemsToRemove));
                } else if (mode == MODE_MANAGE_TAB) {
                    SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                    responseIntent.setAction(Common.XGELS_ACTION_MODIFY_TAB);
                    responseIntent.putExtra("tabname", tabName);
                    responseIntent.putExtra("add", newTab);
                    editor.putStringSet("tab_" + tabName, new HashSet<String>(apps)).commit();
                }

                sendBroadcast(new Intent(Common.XGELS_ACTION_RELOAD_SETTINGS));
                sendBroadcast(responseIntent);

                finish();
            default:
                break;
        }

        return true;
    }

    public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
        private Context context;
        private List<ResolveInfo> values;
        private PackageManager pm;
        private LayoutInflater inflater;
        private IconPack iconPack;

        OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener () {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!apps.contains((String) buttonView.getTag())) {
                        // app is not in the list, so lets add it
                        apps.add((String)buttonView.getTag());
                    }
                }
                else {
                    if (apps.contains((String) buttonView.getTag())) {
                        // app is in the list but the checkbox is no longer checked, we can remove it
                        apps.remove((String) buttonView.getTag());
                    }
                }
            }
        };

        public AppArrayAdapter(Context context, PackageManager pm, List<ResolveInfo> values) {
            super(context, R.layout.row, values);
            this.context = context;
            this.values = values;
            this.pm = pm;
            this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.iconPack = FragmentIcon.iconPack;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ResolveInfo item = values.get(position);
            ViewHolder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ViewHolder();
                rowView = inflater.inflate(R.layout.row, parent, false);
                holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
                holder.textView = (TextView) rowView.findViewById(R.id.name);
                holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                holder.imageView.setImageDrawable(item.loadIcon(pm));

                rowView.setTag(holder);
            }

            holder = (ViewHolder) rowView.getTag();
            holder.textView.setText(item.loadLabel(pm));

            if (mode == MODE_PICK_APPS_TO_HIDE) {
                holder.checkBox.setTag(item.activityInfo.packageName + "#" + item.loadLabel(pm));

            } else if (mode == MODE_SELECT_FOLDER_APPS || mode == MODE_MANAGE_TAB) {
                holder.checkBox.setTag(new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString());
            }

            holder.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
            holder.checkBox.setChecked(apps.contains(holder.checkBox.getTag()));
            holder.loadImageAsync(pm, item, holder, iconPack);

            return rowView;
        }
    }
}