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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class ChooseAppList extends ListActivity {

    String prefKey;
    Intent intent;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView().setCacheColorHint(CommonUI.UIColor);
        getListView().setBackgroundColor(CommonUI.UIColor);
        getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));

        // retrieve the preference key so that we can save a app linked with the gesture
        intent = getIntent();
        prefKey = intent.getStringExtra("prefKey");

        PackageManager pm = getPackageManager();

        // load all apps which are listed in the app drawer
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

        // sort them
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager(), apps);
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, intent);
        ChooseAppList.this.finish();
    }

    public static class ViewHolder {
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;
        String cmpName;
    }

    public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
        private Context context;
        private List<ResolveInfo> values;
        private PackageManager pm;
        private LayoutInflater inflater;

        OnClickListener onClickListener = new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(prefKey + "_launch");
                editor.apply();
                editor.putString(prefKey + "_launch", ((ViewHolder) v.getTag()).cmpName);
                editor.apply();
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
                startActivity(i);
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

            ViewHolder holder;
            View rowView = convertView;

            if (rowView == null) {
                holder = new ViewHolder();
                rowView = inflater.inflate(R.layout.row, parent, false);
                holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
                holder.textView = (TextView) rowView.findViewById(R.id.name);
                holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

                rowView.setTag(holder);
            }

            holder = (ViewHolder) rowView.getTag();
            ResolveInfo item = values.get(position);

            new ImageLoader(pm, item, holder.imageView).execute();
            holder.textView.setText(item.loadLabel(pm));
            holder.checkBox.setVisibility(View.GONE);

            if (prefKey != null) {
                holder.cmpName = item.activityInfo.packageName;
                rowView.setOnClickListener(onClickListener);
            } else {
                holder.cmpName = new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString();
                rowView.setOnClickListener(onClickListenerApp);
            }

            // add the row to the listview
            return rowView;
        }
    }
}