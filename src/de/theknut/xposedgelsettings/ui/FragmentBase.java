package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class FragmentBase extends PreferenceFragment {
	
	public static Context mContext;	
	public static Activity mActivity;
	public static boolean toastShown = false;
	public static boolean alertShown = false;
	public static boolean alertAnswerKill = false;
	public static String TAG = "XGELS";
	
	OnPreferenceChangeListener onChangeListenerLauncherReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
            if (!toastShown) {                 
            	Toast.makeText(mContext, R.string.toast_reboot, Toast.LENGTH_LONG).show();
            	toastShown = true;
            }
            
            return true;
        }
    };
    
    OnPreferenceChangeListener onChangeListenerFullReboot = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            
            if (!toastShown) {                 
            	Toast.makeText(mContext, R.string.toast_full_reboot, Toast.LENGTH_LONG).show();
            	toastShown = true;
            }
            
            CommonUI.needFullReboot = true;
            return true;
        }
    };
    
    OnSharedPreferenceChangeListener onChangeListenerKillLauncher = new OnSharedPreferenceChangeListener() {
    	
    	List<String> keys = Arrays.asList("PREFS_VERSION_KEY", "dontshowkilldialog", "autokilllauncher", "dontshowgoogleplaydialog", "debug", "premiumlistpref");
    	
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            sharedPreferences.edit().commit();
    		if (keys.contains(key)) return;
            
    		if (!sharedPreferences.getBoolean("dontshowkilldialog", false) && !alertShown) {
            
	            LayoutInflater adbInflater = LayoutInflater.from(mContext);
	            View dontShowAgainLayout = adbInflater.inflate(R.layout.dialog_with_checkbox, null);
	            final CheckBox dontShowAgain = (CheckBox) dontShowAgainLayout.findViewById(R.id.skip);
	            dontShowAgain.setIncludeFontPadding(false);
	            dontShowAgain.setText(R.string.alert_autokill_checkbox);
	            
	            new AlertDialog.Builder(mContext)
	            .setView(dontShowAgainLayout)
	            .setCancelable(false)
	    	    .setTitle(R.string.alert_autokill_title)
	    	    .setMessage(R.string.alert_autokill_summary)
	    	    .setPositiveButton(R.string.alert_autokill_ok, new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) { 
	    	        	
	    	        	if (dontShowAgain.isChecked()) {
	    		        	SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
	    	                SharedPreferences.Editor editor = settings.edit();
	    	                editor.putBoolean("dontshowkilldialog", true).commit();
	    	                editor.putBoolean("autokilllauncher", true).commit();
	    	        	}
	    	        	
	    	        	alertAnswerKill = true;
	    	        	CommonUI.restartLauncher(false);
	    	        }
	            })
	    	    .setNegativeButton(R.string.alert_autokill_cancel, new DialogInterface.OnClickListener() {
	    	        public void onClick(DialogInterface dialog, int which) {
	    	        	
	    	        	if (dontShowAgain.isChecked()) {
	    		        	SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
	    	                SharedPreferences.Editor editor = settings.edit();
	    	                editor.putBoolean("dontshowkilldialog", true).commit();
	    	                editor.putBoolean("autokilllauncher", false).commit();
	    	        	}
	    	        	
	    	            dialog.dismiss();
	    	        }
	    	    }).show();
	            
	            alertShown = true;
    		}
    		
    		if (sharedPreferences.getBoolean("autokilllauncher", false) || alertAnswerKill) {
    			CommonUI.restartLauncher(false);
    		}
        }
    };
    
    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
    	return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    @Override
    public void onAttach(Activity activity) {
    	super.onAttach(activity);
    	mContext = mActivity = activity;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(onChangeListenerKillLauncher);
    	MainActivity.closeDrawer();
    }
    
    @Override
    public void onPause() {   	
    	
    	getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onChangeListenerKillLauncher);
    	
    	super.onPause();
    }
}
