package de.theknut.xposedgelsettings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.theknut.xposedgelsettings.R;

public class FragmentSearchbar extends FragmentBase {
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.searchbar_fragment);
        
        this.findPreference("hidesearchbar").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("autohidehidesearchbar").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}