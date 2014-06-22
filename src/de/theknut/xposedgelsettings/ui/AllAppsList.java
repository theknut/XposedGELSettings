package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;

public class AllAppsList extends ListActivity {

    public static Set<String> hiddenApps;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CommonUI.CONTEXT = getApplicationContext();

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

        // save our new list
        SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("hiddenapps");
        editor.apply();
        editor.putStringSet("hiddenapps", hiddenApps);
        editor.apply();
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // get our hidden app list
        hiddenApps = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getStringSet("hiddenapps", new HashSet<String>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                CommonUI.restartLauncherOrDevice();
                break;
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
            ChooseAppList.ViewHolder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ChooseAppList.ViewHolder();
                rowView = inflater.inflate(R.layout.row, parent, false);
                holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
                holder.textView = (TextView) rowView.findViewById(R.id.name);
                holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
                holder.imageView.setImageDrawable(item.loadIcon(pm));

                rowView.setTag(holder);
            }

            holder = (ChooseAppList.ViewHolder) rowView.getTag();
            holder.textView.setText(item.loadLabel(pm));
            holder.checkBox.setTag(item.activityInfo.packageName + "#" + item.loadLabel(pm));
            holder.checkBox.setChecked(hiddenApps.contains(holder.checkBox.getTag()));
            holder.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
            holder.loadImageAsync(pm, item, holder, iconPack);

            return rowView;
        }
    }
}