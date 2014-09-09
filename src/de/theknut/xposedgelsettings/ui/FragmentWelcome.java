package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.theknut.xposedgelsettings.BuildConfig;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

@SuppressLint("WorldReadableFiles")
public class FragmentWelcome extends FragmentBase {
	
	static boolean shown = false;
	AlertDialog IsXposedInstalledAlert, IsModuleActive, IsInstalledFromPlayStore, IsSupportedLauncherInstalled, NeedReboot;
	List<AlertDialog> alerts;
	int alertToShow = 0;
	boolean cancel;
     
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.welcome, container, false);

        if (BuildConfig.DEBUG) {

        }


        return CommonUI.setBackground(rootView, R.id.welcomebackground);
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onResume() {
    	super.onResume();
    	
    	if (!shown) {
    		shown = true;
    		
    		createAlertDialogs();
    		alerts = new ArrayList<AlertDialog>();
    		
    		if (!isXposedInstalled()) {
        		alerts.add(IsXposedInstalledAlert);
        		new ShowAlertsAsyncTask().execute();
        		return;
        	}
    		
    		if (!isSupportedLauncherInstalled()) {
    			alerts.add(IsSupportedLauncherInstalled);
    		}
        	
        	if (!isXGELSActive()) {
        		alerts.add(IsModuleActive);
        	}
        	
        	SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        	if (!isInstalledFromPlay()) {
        		if (!settings.getBoolean("dontshowgoogleplaydialog", false)) {
        			alerts.add(IsInstalledFromPlayStore);
        		}
        	}
    		
    		Changelog cl = new Changelog(mContext);
    	    if (cl.firstRun()) {
    	    	CommonUI.needFullReboot = true;
    	        alerts.add(cl.getFullLogDialog());
    	        getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentReverseEngineering()).commit();
    	    }
    	    
    	    if (alerts.size() != 0) {
    	    	new ShowAlertsAsyncTask().execute();
    	    }
    	}
    }
    
    // this method gets replaced by the module and returns true
    public boolean isXGELSActive() {
    	return false;
    }
    
    public boolean isSupportedLauncherInstalled() {
    	
    	boolean retVal = false;
    	PackageManager pm = mContext.getPackageManager();
    	
        try {
            pm.getPackageInfo(Common.GEL_PACKAGE, PackageManager.GET_ACTIVITIES);
            retVal = true;
        }
        catch (PackageManager.NameNotFoundException e) { }
        
        try {
            pm.getPackageInfo(Common.TREBUCHET_PACKAGE, PackageManager.GET_ACTIVITIES);
            retVal = true;
        }
        catch (PackageManager.NameNotFoundException e) { }

        try {
            pm.getPackageInfo("com.android.launcher3", PackageManager.GET_ACTIVITIES);
            retVal = true;
        }
        catch (PackageManager.NameNotFoundException e) { }
        
        return retVal;
    }
    
    private boolean isXposedInstalled() {
    	PackageManager pm = mContext.getPackageManager();
    	
        try {
            pm.getPackageInfo("de.robv.android.xposed.installer", PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    
    private boolean isInstalledFromPlay() {
    	String installer = mContext.getPackageManager().getInstallerPackageName("de.theknut.xposedgelsettings");
    	
    	if (installer == null) {
    		return false;
    	}
    	else {
    		return installer.equals("com.android.vending");
    	}
    }
    
    private void createAlertDialogs() {
    	IsXposedInstalledAlert = new AlertDialog.Builder(mContext)
		.setCancelable(false)
	    .setTitle(getString(R.string.missing_framework))
	    .setMessage(getString(R.string.missing_framework_msg))
	    .setPositiveButton(getString(R.string.go_to_framework), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/xposed/xposed-installer-versions-changelog-t2714053"));
	        	startActivity(browserIntent);
	        	shown = false;
	        	getActivity().finish();
	        }
        }
	     )
	    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	shown = false;
	            getActivity().finish();
	        }
	     }).create();
		
		IsModuleActive = new AlertDialog.Builder(mContext)
		.setCancelable(false)
	    .setTitle(getString(R.string.module_not_active))
	    .setMessage(getString(R.string.module_not_active_msg))
	    .setPositiveButton(getString(R.string.open_xposed_installer), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        	Intent LaunchIntent = null;
	        	
	        	try {
		        	LaunchIntent = mContext.getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
		        	if (LaunchIntent == null) {
		        		Toast.makeText(mContext, R.string.toast_openinstaller_failed, Toast.LENGTH_LONG).show();
		        	} else {
		        		Intent intent = new Intent("de.robv.android.xposed.installer.OPEN_SECTION");
		        		intent.setPackage("de.robv.android.xposed.installer");
		        		intent.putExtra("section", "modules");
		        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        		mContext.startActivity(intent);
		        	}
	        	} catch (Exception e) {
	        		if (LaunchIntent != null) {
	        			LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        			mContext.startActivity(LaunchIntent);
	        		} else {
	        			e.printStackTrace();
	        			Toast.makeText(mContext, getString(R.string.active_manually), Toast.LENGTH_LONG).show();
	        		}
	        	}
	        }
        }
	     )
	    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.dismiss();
	        }
	     })
	     .create();
		
		LayoutInflater adbInflater = LayoutInflater.from(mContext);
        View dontShowAgainLayout = adbInflater.inflate(R.layout.dialog_with_checkbox, null);
        final CheckBox dontShowAgain = (CheckBox) dontShowAgainLayout.findViewById(R.id.skip);
        dontShowAgain.setIncludeFontPadding(false);
        dontShowAgain.setText(getString(R.string.dont_show_again));
        
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        adb.setView(dontShowAgainLayout);
        adb.setCancelable(false);
	    adb.setTitle(getString(R.string.module_not_from_google_play));
	    adb.setMessage(getString(R.string.module_not_from_google_play_msg));
	    adb.setPositiveButton(getString(R.string.open_play_store), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        	if (dontShowAgain.isChecked()) {
		        	SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
	                SharedPreferences.Editor editor = settings.edit();
	                editor.putBoolean("dontshowgoogleplaydialog", true);
	                editor.commit();
	        	}
	        	
	        	final String appPackageName = Common.PACKAGE_NAME;
	        	try {
	        	    CommonUI.ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
	        	} catch (android.content.ActivityNotFoundException anfe) {
                    CommonUI.ACTIVITY.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
	        	}
	        }
        });
	    adb.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        	if (dontShowAgain.isChecked()) {
		        	SharedPreferences settings = mContext.getSharedPreferences(Common.PREFERENCES_NAME, 0);
	                SharedPreferences.Editor editor = settings.edit();
	                editor.putBoolean("dontshowgoogleplaydialog", true);
	                editor.commit();
	        	}
	        	
	            dialog.dismiss();
	        }
	    });
	     
	    IsInstalledFromPlayStore = adb.create();
	    
	    NeedReboot = new AlertDialog.Builder(mContext)
		.setCancelable(false)
	    .setTitle(R.string.alert_xgels_updated_title)
	    .setMessage(R.string.alert_xgels_updated_summary)
	    .setPositiveButton(getString(R.string.full_reboot), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        	CommonUI.openRootShell(new String[]{"su", "-c", "reboot now"});
	        	cancel = true;
	        }
        }
	     )
	     .setNeutralButton(getString(R.string.hot_reboot), new DialogInterface.OnClickListener() {

		      public void onClick(DialogInterface dialog, int id) {
		    	  
		    	  CommonUI.openRootShell(new String[]{ "su", "-c", "killall system_server"});
		    	  cancel = true;
		    }})
	    .setNegativeButton(R.string.alert_xgels_updated_cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.dismiss();
	        }
	     }).create();
	    
	    IsSupportedLauncherInstalled = new AlertDialog.Builder(mContext)
		.setCancelable(false)
	    .setTitle(R.string.alert_launcher_not_installed_title)
	    .setMessage(R.string.alert_launcher_not_installed_summary)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        	dialog.dismiss();
	        }
         })
	     .setNeutralButton(R.string.alert_launcher_not_installed_get_gnl, new DialogInterface.OnClickListener() {

		      public void onClick(DialogInterface dialog, int id) {
		    	  try {
		    		  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Common.GEL_PACKAGE)));
		    	  } catch (android.content.ActivityNotFoundException anfe) {
		    		  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Common.GEL_PACKAGE)));
		    	  }	
		 }}).create();
    }
    
    private class ShowAlertsAsyncTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		try {
    			if (cancel) {
    				this.cancel(true);
    			}
    			
    			alerts.get(alertToShow).show();
    		} catch (Exception e) {
    			e.printStackTrace();
    			this.cancel(true);
    		}
    	}
    	
		@Override
		protected Void doInBackground(final Void... params) {
			
			while(alerts.get(alertToShow).isShowing()) {
				try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
				
				if (cancel) {
    				this.cancel(true);
    			}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			alertToShow++;
			
			if (alertToShow < alerts.size()) {					
				new ShowAlertsAsyncTask().execute();
			}
		}
    }
}