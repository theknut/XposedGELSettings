package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;

public class FragmentHomescreen extends FragmentBase {
	
	public FragmentHomescreen() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.homescreen_fragment);
        
        this.findPreference("hide_appdock").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                
            	if ((Boolean) newValue) {
	                new AlertDialog.Builder(CommonUI.CONTEXT)
	        		.setCancelable(false)
	        	    .setTitle(android.R.string.dialog_alert_title)
	        	    .setMessage(R.string.alert_hidedock_summary)
	        	    .setPositiveButton(android.R.string.ok, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							
							if (!toastShown) {                 
				            	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
				            	toastShown = true;
				            }
						}
					})
	        	    .show();
            	}
                
                return true;
            }});
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}