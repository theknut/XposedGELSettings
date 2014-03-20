package de.theknut.xposedgelsettings.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
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
        
        this.findPreference("hidepageindicator").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("enablerotation").setOnPreferenceChangeListener(onChangeListenerFullReboot);
        this.findPreference("resizeallwidgets").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);        
        this.findPreference("longpressallappsbutton").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("disablewallpaperscroll").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("lockhomescreen").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("continuousscroll").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("continuousscrollwithappdrawer").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}