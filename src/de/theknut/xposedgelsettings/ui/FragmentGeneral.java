package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.theknut.xposedgelsettings.R;

public class FragmentGeneral extends FragmentBase {
	
	public FragmentGeneral() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.general_fragment);
        
        this.findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerFullReboot);        
        this.findPreference("hidewidgets").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				new AlertDialog.Builder(CommonUI.CONTEXT)
				.setCancelable(false)
			    .setTitle(R.string.alert_hidewidgets_title)
			    .setMessage(R.string.alert_hidewidgets_summary)
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	Intent intent = new Intent(getActivity(), AllWidgetsList.class);
			        	CommonUI.CONTEXT.startActivity(intent);
			        	dialog.dismiss();
			        }
		        }).show();
				
				return true;
			}
		});
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}