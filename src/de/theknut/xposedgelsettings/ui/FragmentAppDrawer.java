package de.theknut.xposedgelsettings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.theknut.xposedgelsettings.R;

public class FragmentAppDrawer extends FragmentBase {
	
	public FragmentAppDrawer() { }
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.options_fragment, container, false);
        addPreferencesFromResource(R.xml.appdrawer_fragment);
        
        this.findPreference("hideiconappdrawer").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);        
        this.findPreference("changegridsizeapps").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        this.findPreference("iconsettingsswitchapps").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);        
        this.findPreference("closeappdrawerafterappstarted").setOnPreferenceChangeListener(onChangeListenerLauncherReboot);
        
        rootView = CommonUI.setBackground(rootView, R.id.prefbackground);
        
        return rootView;
    }
}