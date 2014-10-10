package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentHomescreen extends FragmentBase {
	
	public FragmentHomescreen() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.homescreen_fragment);

        findPreference("gridsize").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final ViewGroup numberPickerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.number_picker, null);
                int padding = Math.round(mContext.getResources().getDimension(R.dimen.tab_menu_padding));
                final AlertDialog numberPickerDialog = new AlertDialog.Builder(mActivity).create();
                numberPickerDialog.setView(numberPickerView, padding, padding, padding, padding);
                numberPickerView.findViewById(R.id.horizontallayout).setVisibility(View.GONE);

                final SharedPreferences prefs = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);

                int minValue = 4, maxValue = 15;
                final NumberPicker npvc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalColumn);
                npvc.setMinValue(minValue);
                npvc.setMaxValue(maxValue);
                npvc.setValue(Integer.parseInt(prefs.getString("xcounthomescreen", "" + 4)));

                final NumberPicker npvr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerVerticalRow);
                npvr.setMinValue(minValue);
                npvr.setMaxValue(maxValue);
                npvr.setValue(Integer.parseInt(prefs.getString("ycounthomescreen", "" + 4)));

//                final NumberPicker nphc = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalColumn);
//                nphc.setMinValue(minValue);
//                nphc.setMaxValue(maxValue);
//                nphc.setValue(Integer.parseInt(prefs.getString("xcounthomescreenhorizontal", "" + 6)));
//
//                final NumberPicker nphr = (NumberPicker) numberPickerView.findViewById(R.id.numberPickerHorizontalRow);
//                nphr.setMinValue(minValue);
//                nphr.setMaxValue(maxValue);
//                nphr.setValue(Integer.parseInt(prefs.getString("ycounthomescreenhorizontal", "" + 3)));

                numberPickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // due to legacy reasons we need to save them as strings.........
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("ycounthomescreen", "" + npvr.getValue())
                                .putString("xcounthomescreen", "" + npvc.getValue())
                                //.putString("ycounthomescreenhorizontal", "" + nphr.getValue())
                                //.putString("xcounthomescreenhorizontal", "" + nphc.getValue())
                                .commit();

                        numberPickerDialog.dismiss();
                    }
                });

                numberPickerDialog.setCancelable(false);
                numberPickerDialog.show();
                return true;
            }
        });
        
        this.findPreference("hide_appdock").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                
            	if ((Boolean) newValue) {
	                new AlertDialog.Builder(mContext)
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

        MyListPreference smartFolderMode = (MyListPreference) findPreference("smartfoldermode");
        smartFolderMode.setSummary(smartFolderMode.getEntry());
        smartFolderMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MyListPreference pref = (MyListPreference) preference;
                pref.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);
                return true;
            }
        });

        if (!InAppPurchase.isPremium) {
            findPreference("unlimitedfoldersize").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}