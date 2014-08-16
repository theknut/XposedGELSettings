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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.ui.ImageLoader.ViewHolder;

public class AllAppsList extends ListActivity {

    public static Set<String> hiddenApps;

    static List<String> originalFolderItems, folderItems, newFolderItems, removedFolderItems;
    static String appComponentName;
    static long itemID;
    static int mode;

    static final int MODE_PICK_APPS_TO_HIDE = 1;
    public static final int MODE_SELECT_FOLDER_APPS = 2;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        appComponentName = intent.getStringExtra("app");
        mode = intent.getIntExtra("mode", 1);

        if (mode == MODE_SELECT_FOLDER_APPS) {
            itemID = intent.getLongExtra("itemtid", 0);
            originalFolderItems = intent.getStringArrayListExtra("items");
            folderItems = new ArrayList<String>(originalFolderItems);
            newFolderItems = new ArrayList<String>();
            removedFolderItems = new ArrayList<String>();
        }

        CommonUI.CONTEXT = this;

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
            editor.putStringSet("hiddenapps", hiddenApps).commit();
        }
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        if (mode == MODE_PICK_APPS_TO_HIDE) {
            // get our hidden app list
            hiddenApps = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getStringSet("hiddenapps", new HashSet<String>());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (mode == MODE_SELECT_FOLDER_APPS) {
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
                CommonUI.restartLauncherOrDevice();
                break;
            case R.id.action_save:

                for (String folderItem : folderItems) {
                    if (!originalFolderItems.contains(folderItem)) {
                        newFolderItems.add(folderItem);
                    }
                }

                for (String folderItem : originalFolderItems) {
                    if (!folderItems.contains(folderItem)) {
                        removedFolderItems.add(folderItem);
                    }
                }

                Intent intent = new Intent(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS);
                intent.putExtra("itemid", itemID);
                intent.putStringArrayListExtra("additems", new ArrayList<String>(newFolderItems));
                intent.putStringArrayListExtra("removeitems", new ArrayList<String>(removedFolderItems));
                sendBroadcast(intent);
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

        OnCheckedChangeListener onCheckedChangeListenerHideApps = new OnCheckedChangeListener () {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!hiddenApps.contains((String) buttonView.getTag())) {
                        // app is not in the list, so lets add it
                        hiddenApps.add((String)buttonView.getTag());
                    }
                }
                else {
                    if (hiddenApps.contains((String) buttonView.getTag())) {
                        // app is in the list but the checkbox is no longer checked, we can remove it
                        hiddenApps.remove((String) buttonView.getTag());
                    }
                }
            }
        };

        OnCheckedChangeListener onCheckedChangeListenerSelectApps = new OnCheckedChangeListener () {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!folderItems.contains((String) buttonView.getTag())) {
                        // app is not in the list, so lets add it
                        folderItems.add((String)buttonView.getTag());
                    }
                }
                else {
                    if (folderItems.contains((String) buttonView.getTag())) {
                        // app is in the list but the checkbox is no longer checked, we can remove it
                        folderItems.remove((String) buttonView.getTag());
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
                holder.checkBox.setOnCheckedChangeListener(onCheckedChangeListenerHideApps);
            } else if (mode == MODE_SELECT_FOLDER_APPS) {
                holder.checkBox.setTag(new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString());
                holder.checkBox.setOnCheckedChangeListener(onCheckedChangeListenerSelectApps);
            }

            holder.checkBox.setChecked(isChecked(holder));
            holder.loadImageAsync(pm, item, holder, iconPack);

            return rowView;
        }

        private boolean isChecked(ViewHolder holder) {
            if (mode == MODE_PICK_APPS_TO_HIDE) {
                return hiddenApps.contains(holder.checkBox.getTag());
            } else if (mode == MODE_SELECT_FOLDER_APPS) {
                return folderItems.contains(holder.checkBox.getTag().toString());
            }

            return false;
        }
    }
}