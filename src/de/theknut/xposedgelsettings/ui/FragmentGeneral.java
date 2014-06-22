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
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;

public class FragmentGeneral extends FragmentBase {
	
	public FragmentGeneral() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.general_fragment);
        
        findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        findPreference("hidewidgets").setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {

                try {
                    new AlertDialog.Builder(getActivity())
                            .setCancelable(false)
                            .setTitle(R.string.alert_hidewidgets_title)
                            .setMessage(R.string.alert_hidewidgets_summary)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().startActivity(new Intent(getActivity(), AllWidgetsList.class));
                                    dialog.dismiss();
                                }
                            }).show();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), R.string.alert_hidewidgets_summary, Toast.LENGTH_LONG).show();
                    getActivity().startActivity(new Intent(getActivity(), AllWidgetsList.class));
                }

				return true;
			}
		});

        final CustomSwitchPreference resizeallwidgets = (CustomSwitchPreference) findPreference("resizeallwidgets");
        final CustomSwitchPreference overlappingWidgets = (CustomSwitchPreference) findPreference("overlappingwidgets");

        resizeallwidgets.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(!(Boolean) newValue) {
                    overlappingWidgets.setChecked(false);
                }
                return true;
            }
        });
        overlappingWidgets.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean) newValue) {
                    resizeallwidgets.setChecked(true);
                }

                return true;
            }
        });

        if (!InAppPurchase.isPremium) {
            findPreference("overlappingwidgets").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}