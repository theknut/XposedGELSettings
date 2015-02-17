package de.theknut.xposedgelsettings.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.ui.ImageLoader.ViewHolder;

public class ShortcutsList extends ActionBarListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CommonUI.CONTEXT = CommonUI.ACTIVITY = this;
        getListView().setCacheColorHint(getResources().getColor(R.color.primary_dark));
        getListView().setBackgroundColor(getResources().getColor(R.color.primary_dark));

        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager(), getPackageManager().queryIntentActivities(shortcutsIntent, 0));
        setListAdapter(adapter);
    }

    public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
        private Context context;
        private List<ResolveInfo> values;
        private PackageManager pm;
        private LayoutInflater inflater;

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
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setTag(new ComponentName(item.activityInfo.packageName, item.activityInfo.name).flattenToString());
            holder.loadImageAsync(pm, item, holder);

            return rowView;
        }
    }
}