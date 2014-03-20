package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentSystemUI extends FragmentBase {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.systemui_fragment);
        
        this.findPreference("hideclock").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("dynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("dynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("changeicondynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("changeicondynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("animatedynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("animatedynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        //this.findPreference("dynamicbackbuttononeveryscreen").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}