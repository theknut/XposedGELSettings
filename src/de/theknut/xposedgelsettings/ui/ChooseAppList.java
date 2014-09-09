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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.ImageLoader.ViewHolder;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;

@SuppressLint("WorldReadableFiles")
public class ChooseAppList extends ListActivity {

    AppArrayAdapter adapter;
    String prefKey;
    Intent intent;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setCacheColorHint(CommonUI.UIColor);
        getListView().setBackgroundColor(CommonUI.UIColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        // retrieve the preference key so that we can save an app linked with the gesture
        intent = getIntent();
        prefKey = intent.getStringExtra("prefKey");

        adapter = new AppArrayAdapter(this, getPackageManager(), CommonUI.getAllApps());
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, intent);
        ChooseAppList.this.finish();
    }

    SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (sharedPreferences.getBoolean("autokilllauncher", false)) {
                CommonUI.restartLauncher(false);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (prefKey == null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.chooseapp_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_reset_all:
                SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
                editor.remove("selectedicons").commit();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
        private Context context;
        private List<ResolveInfo> values;
        private PackageManager pm;
        private LayoutInflater inflater;
        private IconPack iconPack;

        OnClickListener onClickListener = new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(prefKey + "_launch").commit();
                editor.putString(prefKey + "_launch", ((ViewHolder) v.getTag()).cmpName).commit();
                setResult(RESULT_OK, intent);
                ChooseAppList.this.finish();
            }
        };

        OnClickListener onClickListenerApp = new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ChooseAppList.this, FragmentSelectiveIcon.class);
                i.putExtra("app", ((ViewHolder) v.getTag()).cmpName);
                i.putExtra("mode", FragmentSelectiveIcon.MODE_PICK_GLOBAL_ICON);
                startActivityForResult(i, 0);
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
        public View getView(int position, final View convertView, ViewGroup parent) {

            ResolveInfo item = values.get(position);
            ViewHolder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ViewHolder();
                rowView = inflater.inflate(R.layout.row, parent, false);
                holder.imageView = (ImageView) rowView.findViewById(R.id.badgepreviewicon);
                holder.textView = (TextView) rowView.findViewById(R.id.name);
                holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                holder.delete = (ImageButton) rowView.findViewById(R.id.deletebutton);
                holder.selectedIcon = (ImageView) rowView.findViewById(R.id.selectedicon);
                holder.checkBox.setVisibility(View.GONE);

                if (CommonUI.TextColor == -1) {
                    CommonUI.TextColor = holder.textView.getCurrentTextColor();
                }

                String cmpName = new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString();
                holder.imageView.setImageDrawable(
                        iconPack == null
                                ? item.loadIcon(pm)
                                : iconPack.loadIcon(cmpName)
                );

                rowView.setTag(holder);
            }

            holder = (ViewHolder) rowView.getTag();
            holder.textView.setText(item.loadLabel(pm));

            if (prefKey != null) {
                holder.cmpName = item.activityInfo.packageName;
                rowView.setOnClickListener(onClickListener);
            } else {
                String cmpName = new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString();
                holder.cmpName = cmpName;
                holder.delete.setTag(cmpName);

                boolean visible = false;
                SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet("selectedicons", new HashSet<String>());
                for (String selectedIcon : selectedIcons) {
                    if (selectedIcon.split("\\|")[0].equals(cmpName)) {
                        visible = true;
                    }
                }

                holder.delete.setVisibility(visible ? View.VISIBLE : View.GONE);
                holder.selectedIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
                rowView.setOnClickListener(onClickListenerApp);
            }

            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                    SharedPreferences.Editor editor = prefs.edit();
                    String key = "selectedicons";
                    String appComponentName = (String) v.getTag();
                    HashSet<String> selectedIcons = (HashSet<String>) prefs.getStringSet(key, new HashSet<String>());

                    Iterator it = selectedIcons.iterator();
                    while (it.hasNext()) {
                        String[] item = it.next().toString().split("\\|");
                        if (item[0].equals(appComponentName)) {
                            it.remove();
                        }
                    }

                    editor.remove(key).commit();
                    editor.putStringSet(key, selectedIcons).commit();

                    notifyDataSetChanged();
                }
            });
            holder.loadImageAsync(pm, item, holder);

            return rowView;
        }
    }
}