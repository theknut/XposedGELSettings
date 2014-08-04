package de.theknut.xposedgelsettings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.theknut.xposedgelsettings.R;

public class FragmentAndroidIntegration extends FragmentBase {
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
  
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.androidintegration_fragment);
        
        this.findPreference("hideclock").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("dynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("dynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("changeicondynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("changeicondynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("animatedynamicbackbutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("animatedynamichomebutton").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("dynamicbackbuttononeveryscreen").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("transparentsystembars").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}