package de.theknut.xposedgelsettings;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

public class AllAppsList extends ListActivity {
	
	public static Set<String> hiddenApps;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PackageManager pm = getPackageManager();
				
		// load all launcher activities 
    	final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        
		AppArrayAdapter adapter = new AppArrayAdapter(this, getPackageManager(), apps);
	    setListAdapter(adapter);    
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = getSharedPreferences(Common.PACKAGE_NAME + "_preferences", Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove("hiddenapps");
		editor.commit();
		editor.putStringSet("hiddenapps", hiddenApps);
		editor.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		hiddenApps = getSharedPreferences(Common.PACKAGE_NAME + "_preferences", Context.MODE_WORLD_READABLE).getStringSet("hiddenapps", new HashSet<String>());
	}
	
	public class AppArrayAdapter extends ArrayAdapter<ResolveInfo> {
		  private Context context;
		  private List<ResolveInfo> values;
		  private PackageManager pm;

		  public AppArrayAdapter(Context context, PackageManager pm, List<ResolveInfo> values) {
			super(context, R.layout.row, values);
			this.context = context;
			this.values = values;
			this.pm = pm;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = inflater.inflate(R.layout.row, parent, false);
				
				ResolveInfo item = values.get(position);
				
				ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
				imageView.setImageDrawable(item.loadIcon(pm));
				
				TextView textView = (TextView) rowView.findViewById(R.id.name);
				textView.setText(item.loadLabel(pm));
				
				CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);
				checkBox.setTag(item.activityInfo.packageName + "#" + item.loadLabel(pm));
				checkBox.setChecked(hiddenApps.contains(checkBox.getTag()));
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener () {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
												
						if (isChecked) {
							if (!hiddenApps.contains(buttonView.getTag())) {
								hiddenApps.add((String)buttonView.getTag());
							}
						}
						else {
							if (hiddenApps.contains(buttonView.getTag())) {
								hiddenApps.remove((String)buttonView.getTag());
							}
						}
					}
				});
				
				return rowView;
		  }
	}
}