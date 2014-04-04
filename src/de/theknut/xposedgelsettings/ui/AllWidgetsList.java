package de.theknut.xposedgelsettings.ui;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class AllWidgetsList extends ListActivity {
	
	public static Set<String> hiddenWidgets;
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/LauncherModel.java#3089
	public static final Comparator<AppWidgetProviderInfo> getWidgetNameComparator() {
	    final Collator collator = Collator.getInstance();
	    return new Comparator<AppWidgetProviderInfo>() {
	        public final int compare(AppWidgetProviderInfo a, AppWidgetProviderInfo b) {
	            return collator.compare(a.label.toString().trim(), b.label.toString().trim());
	        }
	    };
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setCacheColorHint(CommonUI.UIColor);
		getListView().setBackgroundColor(CommonUI.UIColor);
		getActionBar().setBackgroundDrawable(new ColorDrawable(CommonUI.UIColor));
		
		// load all widgets        
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        List<AppWidgetProviderInfo> widgets = manager.getInstalledProviders();
        
        // sort them
        Collections.sort(widgets, getWidgetNameComparator());
        
		AppArrayAdapter adapter = new AppArrayAdapter(this, widgets);
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
		editor.remove("hiddenwidgets");
		editor.apply();
		editor.putStringSet("hiddenwidgets", hiddenWidgets);
		editor.apply();
	}
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		// get our hidden widgets list
		hiddenWidgets = getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getStringSet("hiddenwidgets", new HashSet<String>());
	}
	
	public class AppArrayAdapter extends ArrayAdapter<AppWidgetProviderInfo> {
		  private Context context;
		  private List<AppWidgetProviderInfo> values;

		  public AppArrayAdapter(Context context, List<AppWidgetProviderInfo> values) {
			super(context, R.layout.row, values);
			this.context = context;
			this.values = values;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.row, parent, false);
				
				AppWidgetProviderInfo item = values.get(position);
				
				// setup app icon to row
				ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
				
				try {
					imageView.setImageDrawable(context.getPackageManager().getApplicationIcon(item.provider.getPackageName()));
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				
				// setup app label to row
				TextView textView = (TextView) rowView.findViewById(R.id.name);
				textView.setText(item.label);
				
				// setup checkbox to row
				CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
				checkBox.setTag(item.provider.getPackageName() + "#" + item.provider.getShortClassName());
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
								hiddenWidgets.remove((String)buttonView.getTag());
							}
						}
					}
				});
				
				// add the row to the listview
				return rowView;
		  }
	}
}