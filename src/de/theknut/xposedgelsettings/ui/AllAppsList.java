package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Folder;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.ui.ImageLoader.ViewHolder;

public class AllAppsList extends ListActivity {

    List<String> apps, initialItems, itemsToAdd, itemsToRemove;
    String appComponentName;
    long itemID;
    int mode;

    private Intent responseIntent;
    private String itemName;
    private String contentType;
    private boolean newItem;

    public static final int MODE_PICK_APPS_TO_HIDE = 1;
    public static final int MODE_SELECT_FOLDER_APPS = 2;
    public static final int MODE_MANAGE_TAB = 3;
    public static final int MODE_MANAGE_FOLDER = 7;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUI.CONTEXT = CommonUI.ACTIVITY = this;

        if (FragmentIcon.iconPack == null) {
            try {
                FragmentIcon.iconPack = new IconPack(
                        CommonUI.CONTEXT,
                        CommonUI.CONTEXT.getSharedPreferences(
                                Common.PREFERENCES_NAME,
                                Context.MODE_WORLD_READABLE
                        ).getString("iconpack", Common.ICONPACK_DEFAULT));
                FragmentIcon.iconPack.loadAppFilter();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Intent intent = getIntent();
        appComponentName = intent.getStringExtra("app");
        mode = intent.getIntExtra("mode", 1);

        responseIntent = new Intent();

        initialItems = intent.getStringArrayListExtra("items");
        if (initialItems == null) {
            apps = new ArrayList<String>();
            initialItems = new ArrayList<String>();
        } else {
            apps = new ArrayList<String>(initialItems);
        }

        if (mode == MODE_PICK_APPS_TO_HIDE) {
            initialItems = new ArrayList<String>(new ArrayList<String>(
                    getSharedPreferences(
                            Common.PREFERENCES_NAME,
                            Context.MODE_WORLD_READABLE
                    ).getStringSet(
                            "hiddenapps",
                            new HashSet<String>()
                    )
            ));
            apps = new ArrayList<String>(initialItems);
        }

        itemsToAdd = new ArrayList<String>();
        itemsToRemove = new ArrayList<String>();

        if (mode != MODE_PICK_APPS_TO_HIDE) {
            itemID = intent.getLongExtra("itemid", -1);
            newItem = intent.getBooleanExtra("new", false);
            itemName = intent.getStringExtra("name");
            contentType = intent.getStringExtra("contenttype");
            responseIntent.putExtra("itemid", itemID);
            responseIntent.putExtra("index", intent.getIntExtra("index", -1));
        }

        if (mode == MODE_SELECT_FOLDER_APPS) {
            getActionBar().setTitle(intent.getStringExtra("foldername"));
        } else if (mode == MODE_MANAGE_TAB || mode == MODE_MANAGE_FOLDER) {
            getActionBar().setTitle(itemName);
        }

        getListView().setCacheColorHint(CommonUI.UIColor);
        getListView().setBackgroundColor(CommonUI.UIColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager(), CommonUI.getAllApps());
        setListAdapter(adapter);
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

        menu.findItem(R.id.action_refresh).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:

                if (mode == MODE_PICK_APPS_TO_HIDE) {
                    for (String folderItem : initialItems) {
                        if (!apps.contains(folderItem)) {
                            itemsToAdd.add(folderItem);
                        }
                    }

                    responseIntent.setAction(Common.XGELS_ACTION_MODIFY_TAB);
                    responseIntent.putStringArrayListExtra("additems", new ArrayList<String>(itemsToAdd));
                    SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                    editor.remove("hiddenapps").commit();
                    editor.putStringSet("hiddenapps", new HashSet<String>(apps)).commit();
                } else if (mode == MODE_SELECT_FOLDER_APPS) {
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

                    if (apps.size() < 2) {
                        Toast.makeText(this, R.string.toast_appdrawer_folder_minimum_app, Toast.LENGTH_LONG).show();
                        return true;
                    }

                    responseIntent.setAction(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS);
                    responseIntent.putStringArrayListExtra("additems", new ArrayList<String>(itemsToAdd));
                    responseIntent.putStringArrayListExtra("removeitems", new ArrayList<String>(itemsToRemove));

                    if (getIntent().getBooleanExtra("save", false)) {
                        SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                        editor.remove("folder_" + itemID).commit();
                        editor.putStringSet("folder_" + itemID, new HashSet<String>(apps)).commit();
                    }
                } else if (mode == MODE_MANAGE_TAB || mode == MODE_MANAGE_FOLDER) {
                    if (mode == MODE_MANAGE_FOLDER && apps.size() < 2) {
                        Toast.makeText(this, R.string.toast_appdrawer_folder_minimum_app, Toast.LENGTH_LONG).show();
                        return true;
                    }

                    SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, MODE_WORLD_READABLE);
                    SharedPreferences.Editor editor = prefs.edit();

                    responseIntent.setAction(mode == MODE_MANAGE_TAB ? Common.XGELS_ACTION_MODIFY_TAB : Common.XGELS_ACTION_MODIFY_FOLDER);
                    responseIntent.putExtra("name", itemName);
                    responseIntent.putExtra("itemid", itemID);
                    responseIntent.putExtra("contenttype", contentType);

                    String key = mode == MODE_MANAGE_TAB ? "appdrawertabdata" : "appdrawerfolderdata";
                    String prefix = mode == MODE_MANAGE_TAB ? "tab_" : "folder_";
                    ArrayList<String> order = new ArrayList<String>(prefs.getStringSet(key, new LinkedHashSet<String>()));

                    if (apps.size() == 0) {
                        if (!newItem) {
                            responseIntent.putExtra("remove", true);
                            if (mode == MODE_MANAGE_TAB) {
                                order.remove(new Tab(getIntent(), false).toString());
                            } else if (mode == MODE_MANAGE_TAB) {
                                order.remove(new Folder(getIntent(), false).toString());
                            }
                            editor.remove(prefix + itemID).commit();
                        } else {
                            finish();
                            return true;
                        }
                    } else {
                        if (newItem) {
                            responseIntent.putExtra("add", true);
                            if (mode == MODE_MANAGE_TAB) {
                                order.add(new Tab(getIntent(), false).toString());
                            } else if (mode == MODE_MANAGE_FOLDER) {
                                order.add(new Folder(getIntent(), false).toString());
                            }
                        }

                        editor.remove(prefix + itemID)
                                .putStringSet(prefix + itemID, new LinkedHashSet<String>(apps))
                                .commit();
                    }

                    editor.remove(key)
                            .putStringSet(key, new LinkedHashSet<String>(order))
                            .commit();
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
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ResolveInfo item = values.get(position);
            ViewHolder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ViewHolder();
                rowView = inflater.inflate(R.layout.row, parent, false);
                holder.imageView = (ImageView) rowView.findViewById(R.id.badgepreviewicon);
                holder.textView = (TextView) rowView.findViewById(R.id.name);
                holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                holder.imageView.setImageResource(android.R.drawable.sym_def_app_icon);

                rowView.setTag(holder);
            }

            holder = (ViewHolder) rowView.getTag();
            holder.textView.setText(item.loadLabel(pm));
            holder.checkBox.setTag(new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString());
            holder.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
            holder.checkBox.setChecked(apps.contains(holder.checkBox.getTag()));
            holder.loadImageAsync(pm, item, holder);

            return rowView;
        }
    }
}