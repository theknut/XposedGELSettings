package de.theknut.xposedgelsettings.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentGeneral extends FragmentBase {
	
	public FragmentGeneral() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.general_fragment);

        findPreference("enablellauncher").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (!(Boolean) newValue) return true;

                try {
                    PackageManager pkgMgr = mContext.getPackageManager();
                    PackageInfo packageInfo = pkgMgr.getPackageInfo(Common.GEL_PACKAGE, 0);
                    if ((Integer.parseInt(packageInfo.versionName.split("\\.")[0]) >= 3)
                        && (Integer.parseInt(packageInfo.versionName.split("\\.")[1]) < 5)) {
                        Toast.makeText(
                                mContext,
                                "Your Google Search version is outdated (" + packageInfo.versionName + ")\n" + "Google Search 3.5 and up is required!\nThis tweak will have no effect!",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                } catch (Exception e) {
                    // shouldn't be here but lets prevent this from crashing...
                }
                return true;
            }
        });

        findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerFullReboot);

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

        final MyListPreference contextmenuMode = (MyListPreference) findPreference("contextmenumode");
        contextmenuMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                MyListPreference pref = (MyListPreference) preference;
                pref.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);

                return true;
            }
        });
        contextmenuMode.setSummary(contextmenuMode.getEntry());

        if (!InAppPurchase.isPremium) {
            findPreference("overlappingwidgets").setEnabled(false);
        } else {
            getPreferenceScreen().removePreference(this.findPreference("needsDonate"));
        }

        findPreference("hidewidgets").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startActivity(new Intent(getActivity(), AllWidgetsList.class));
                return true;
            }
        });

        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}