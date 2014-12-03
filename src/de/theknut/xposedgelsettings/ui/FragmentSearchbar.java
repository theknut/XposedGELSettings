package de.theknut.xposedgelsettings.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.ui.preferences.MyListPreference;
import de.theknut.xposedgelsettings.ui.preferences.SwitchCompatPreference;

public class FragmentSearchbar extends FragmentBase {
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.searchbar_fragment);

        final MyListPreference searchbarStyle = (MyListPreference) findPreference("searchbarstyle");
        searchbarStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                MyListPreference pref = (MyListPreference) preference;
                pref.setSummary(pref.getEntries()[pref.findIndexOfValue((String) newValue)]);

                return true;
            }
        });
        searchbarStyle.setSummary(searchbarStyle.getEntry());

        final SwitchCompatPreference hideSearchbar = (SwitchCompatPreference) findPreference("hidesearchbar");
        final SwitchCompatPreference weatherWidget = (SwitchCompatPreference) findPreference("searchbarweatherwidget");

        weatherWidget.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    hideSearchbar.setChecked(false);
                }
                return true;
            }
        });

        hideSearchbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((Boolean) newValue) {
                    weatherWidget.setChecked(false);
                }
                return true;
            }
        });

        try {
            if (mContext.getPackageManager().getPackageInfo(Common.GEL_PACKAGE, 0).versionCode >= ObfuscationHelper.GNL_4_0_26) {
                getPreferenceScreen().removePreference(findPreference("searchbarstyle"));
            } else {
                getPreferenceScreen().removePreference(findPreference("searchbarweatherwidget"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}