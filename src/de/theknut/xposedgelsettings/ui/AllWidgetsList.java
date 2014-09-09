package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class AllWidgetsList extends ListActivity {

    public static Set<String> hiddenWidgets;

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/LauncherModel.java#3089

    public static class WidgetAndShortcutNameComparator implements Comparator<Object> {
        private Collator mCollator;
        private PackageManager mPackageManager;
        private HashMap<Object, String> mLabelCache;
        WidgetAndShortcutNameComparator(PackageManager pm) {
            mPackageManager = pm;
            mLabelCache = new HashMap<Object, String>();
            mCollator = Collator.getInstance();
        }
        public final int compare(Object a, Object b) {
            String labelA, labelB;
            if (mLabelCache.containsKey(a)) {
                labelA = mLabelCache.get(a);
            } else {
                labelA = (a instanceof AppWidgetProviderInfo) ?
                        ((AppWidgetProviderInfo) a).label :
                        ((ResolveInfo) a).loadLabel(mPackageManager).toString().trim();
                mLabelCache.put(a, labelA);
            }
            if (mLabelCache.containsKey(b)) {
                labelB = mLabelCache.get(b);
            } else {
                labelB = (b instanceof AppWidgetProviderInfo) ?
                        ((AppWidgetProviderInfo) b).label :
                        ((ResolveInfo) b).loadLabel(mPackageManager).toString().trim();
                mLabelCache.put(b, labelB);
            }
            return mCollator.compare(labelA, labelB);
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setCacheColorHint(CommonUI.UIColor);
        getListView().setBackgroundColor(CommonUI.UIColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        // load all widgets
        PackageManager packageManager = getPackageManager();
        final ArrayList<Object> widgetsAndShortcuts = new ArrayList<Object>();
        widgetsAndShortcuts.addAll(AppWidgetManager.getInstance(this).getInstalledProviders());
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        widgetsAndShortcuts.addAll(packageManager.queryIntentActivities(shortcutsIntent, 0));
        Collections.sort(widgetsAndShortcuts, new WidgetAndShortcutNameComparator(packageManager));

        AppArrayAdapter adapter = new AppArrayAdapter(this, widgetsAndShortcuts);
        setListAdapter(adapter);
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();

        // save our new list
        SharedPreferences.Editor editor = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).edit();
        editor.remove("hiddenwidgets").commit();
        editor.putStringSet("hiddenwidgets", hiddenWidgets).commit();
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // get our hidden widgets list
        hiddenWidgets = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getStringSet("hiddenwidgets", new HashSet<String>());

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (Common.PACKAGE_NAMES.contains(process.processName)) {
                am.killBackgroundProcesses(process.processName);
            }
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

                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningAppProcessInfo process : am.getRunningAppProcesses()) {
                    if (Common.PACKAGE_NAMES.contains(process.processName)) {
                        am.killBackgroundProcesses(process.processName);
                    }
                }

                finish();
                break;
        }

        return true;
    }

    public class AppArrayAdapter extends ArrayAdapter<AppWidgetProviderInfo> {
        private Context context;
        private List values;

        public AppArrayAdapter(Context context, List values) {
            super(context, R.layout.row, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row, parent, false);

            ComponentName cmp;
            String pkg, label;
            Object item = values.get(position);

            if (item instanceof AppWidgetProviderInfo) {
                cmp = ((AppWidgetProviderInfo) item).provider;
                pkg = cmp.getPackageName();
                label = ((AppWidgetProviderInfo) item).label;
            } else {
                pkg = ((ResolveInfo) item).activityInfo.packageName;
                label = ((ResolveInfo) item).activityInfo.loadLabel(getPackageManager()).toString();
                cmp = new ComponentName(pkg, label);
            }

            // setup app icon to row
            ImageView imageView = (ImageView) rowView.findViewById(R.id.badgepreviewicon);

            try {
                imageView.setImageDrawable(context.getPackageManager().getApplicationIcon(pkg));
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            // setup app label to row
            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText((item instanceof ResolveInfo) ? (label + " (1 x 1)") : label);

            // setup checkbox to row
            CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
            checkBox.setTag(cmp.flattenToString());
            checkBox.setChecked(hiddenWidgets.contains(checkBox.getTag()));
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener () {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        if (!hiddenWidgets.contains(buttonView.getTag())) {
                            // app is not in the list, so lets add it
                            hiddenWidgets.add((String)buttonView.getTag());
                        }
                    }
                    else {
                        if (hiddenWidgets.contains(buttonView.getTag())) {
                            // app is in the list but the checkbox is no longer checked, we can remove it
                            hiddenWidgets.remove(buttonView.getTag());
                        }
                    }
                }
            });

            // add the row to the listview
            return rowView;
        }
    }
}