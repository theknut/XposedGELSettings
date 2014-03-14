
package de.theknut.xposedgelsettings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.theknut.xposedgelsettings.hooks.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllAppsListToRename extends ListActivity {

    public static Set<String> renamedApps;

    public static Set<String> modifiedIconApps;

    public static final String separator = "#";

    private List<ResolveInfo> mApps;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager pm = getPackageManager();

        // load all apps which are listed in the app drawer
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);

        // sort them
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        mApps = apps;

        AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager());
        setListAdapter(adapter);
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();

        // save our new list
        SharedPreferences prefs = getSharedPreferences(Common.PREFERENCES_NAME,
                Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("renamedapps");
        editor.remove("modifiediconapps");
        editor.commit();
        editor.putStringSet("renamedapps", renamedApps);
        editor.putStringSet("modifiediconapps", modifiedIconApps);
        editor.commit();
    }

    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // get our hidden app list
        renamedApps = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                .getStringSet("renamedapps", new HashSet<String>());
        modifiedIconApps = getSharedPreferences(Common.PREFERENCES_NAME,
                Context.MODE_WORLD_READABLE)
                .getStringSet("modifiediconapps", new HashSet<String>());
    }

    public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
        private Context context;
        private PackageManager pm;
        private final boolean Original_Icon = true;
        private final boolean Modified_Icon = false;

        public AppArrayAdapter(Context context, PackageManager pm) {
            super(context, R.layout.rename_row, mApps);
            this.context = context;
            this.pm = pm;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rename_row, parent, false);

            final ResolveInfo item = mApps.get(position);

            rowView.setTag(item.activityInfo.packageName);

            // setup app icon to row
            final ImageView imageView = (ImageView) rowView.findViewById(R.id.rename_icon);
            
            if (!modifiedIconApps.contains(item.activityInfo.packageName)){
                imageView.setImageDrawable(item.loadIcon(pm));
                imageView.setTag(Original_Icon);
            }else{
                Drawable modifiedDrawable = Resources.getSystem().getDrawable(android.R.drawable.sym_def_app_icon);
                imageView.setImageDrawable(modifiedDrawable);
                imageView.setTag(Modified_Icon);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if ((Boolean) imageView.getTag() == Original_Icon){
                        Drawable modifiedDrawable = Resources.getSystem().getDrawable(android.R.drawable.sym_def_app_icon);
                        imageView.setImageDrawable(modifiedDrawable);
                        imageView.setTag(Modified_Icon);
                        //由于图标暂时固定，不需判断图标是否一致。
                        modifiedIconApps.add(item.activityInfo.packageName);
                    }else{
                        imageView.setImageDrawable(item.loadIcon(pm));
                        imageView.setTag(Original_Icon);
                        modifiedIconApps.remove(item.activityInfo.packageName);
                    }
                }
            });

            // setup app label to row
            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText(item.loadLabel(pm));

            String renamedName = getRenamedName(item.activityInfo.packageName);
            if (!renamedName.isEmpty()) {
                TextView renameTextView = (TextView) rowView.findViewById(R.id.rename);
                renameTextView.setText(renamedName);
            }

            // setup checkbox to row
            // CheckBox checkBox = (CheckBox)
            // rowView.findViewById(R.id.checkbox);
            // checkBox.setTag(item.activityInfo.packageName + "#" +
            // item.loadLabel(pm));
            // checkBox.setChecked(hiddenApps.contains(checkBox.getTag()));
            // checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener
            // () {
            //
            // @Override
            // public void onCheckedChanged(CompoundButton buttonView, boolean
            // isChecked) {
            //
            // if (isChecked) {
            // if (!hiddenApps.contains(buttonView.getTag())) {
            // // app is not in the list, so lets add it
            // hiddenApps.add((String)buttonView.getTag());
            // }
            // }
            // else {
            // if (hiddenApps.contains(buttonView.getTag())) {
            // // app is in the list but the checkbox is no longer checked, we
            // can remove it
            // hiddenApps.remove((String)buttonView.getTag());
            // }
            // }
            // }
            // });

            // add the row to the listview
            return rowView;
        }

        private String getRenamedName(String packageName) {
            for (String item : renamedApps) {
                if (item.split(separator)[0].equals(packageName)) {
                    return item.split(separator, 2)[1];
                }
            }
            return "";
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final EditText et = new EditText(this);
        final String oldName = mApps.get(position).loadLabel(getPackageManager()).toString();
        et.setText(oldName);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(et)
                .setTitle(getResources().getString(R.string.input_new_name_dialog_title))
                .setPositiveButton(android.R.string.ok, new NewNameDialogListener(oldName, et, v))
                .setNegativeButton(android.R.string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();

    }

    class NewNameDialogListener implements DialogInterface.OnClickListener {
        private String mOldName;
        private EditText mEditText;
        private View mView;

        public NewNameDialogListener(String oldName, EditText et, View v) {
            mOldName = oldName;
            mEditText = et;
            mView = v;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String newName = mEditText.getText().toString();
            if (!newName.equals(mOldName)) {
                List<String> toRemove = new ArrayList<String>();

                for (String item : renamedApps) {
                    if (item.split(separator)[0].equals(mView.getTag())) {
                        toRemove.add(item);
                    }
                }
                renamedApps.removeAll(toRemove);
                renamedApps.add(mView.getTag() + separator + newName);
            } else {
                List<String> toRemove = new ArrayList<String>();
                for (String item : renamedApps) {
                    if (item.split(separator)[0].equals(mView.getTag())) {
                        toRemove.add(item);
                    }
                }
                renamedApps.removeAll(toRemove);
            }
        }

    }
}
