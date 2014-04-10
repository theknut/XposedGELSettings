package de.theknut.xposedgelsettings.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentWelcome extends FragmentBase {
	
	static boolean shown = false;
	AlertDialog IsXposedInstalledAlert, IsModuleActive, IsInstalledFromPlayStore;
	List<AlertDialog> alerts;
	int alertToShow = 0;
     
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	
    	View rootView = inflater.inflate(R.layout.welcome, container, false);
    	
    	rootView = CommonUI.setBackground(rootView, R.id.welcomebackground);
        return rootView;
    }
    
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
        	
        	if (!isXGELSActive()) {
        		alerts.add(IsModuleActive);
        	}
        	
        	if (!isInstalledFromPlay()) {
        		alerts.add(IsInstalledFromPlayStore);
        	}
    		
    		Changelog cl = new Changelog(CommonUI.CONTEXT);
    	    if (cl.firstRun()) {
    	    	CommonUI.needFullReboot = true;
    	        alerts.add(cl.getFullLogDialog());
    	    }
    	    
    	    if (alerts.size() != 0) {
    	    	new ShowAlertsAsyncTask().execute();
    	    }
    	}
    }
    
    public boolean isXGELSActive() {
    	return false;
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
    	IsXposedInstalledAlert = new AlertDialog.Builder(CommonUI.CONTEXT)
		.setCancelable(false)
	    .setTitle("Missing framework!")
	    .setMessage("The Xposed Framework is not installed. This app will not work without the framework!")
	    .setPositiveButton("Go to Xposed Framework", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/showthread.php?t=1574401"));
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
		
		IsModuleActive = new AlertDialog.Builder(CommonUI.CONTEXT)
		.setCancelable(false)
	    .setTitle("Module not active!")
	    .setMessage("XGELS is not active. Please activate the module in Xposed Installer -> Modules")
	    .setPositiveButton("Open Xposed Installer", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	try {
		        	Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
		        	if (LaunchIntent == null) {
		        		Toast.makeText(mContext, R.string.toast_openinstaller_failed, Toast.LENGTH_LONG).show();
		        	} else {
		        		startActivity(LaunchIntent);
		        	}
	        	} catch (Exception e) {
	        		Toast.makeText(mContext, "Ehm... that didn't work. Please open Xposed Installer manually and activate the module. Restart your device!", Toast.LENGTH_LONG).show();
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
		
		IsInstalledFromPlayStore = new AlertDialog.Builder(CommonUI.CONTEXT)
		.setCancelable(false)
	    .setTitle("Module not installed from Google Play!")
	    .setMessage("XGELS is not installed from Google Play! Please reinstall XGELS and download the module from the store to get updates automatically!")
	    .setPositiveButton("Open Play Store", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	
	        	final String appPackageName = Common.PACKAGE_NAME;
	        	try {
	        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
	        	} catch (android.content.ActivityNotFoundException anfe) {
	        	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
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
    }
    
    private class ShowAlertsAsyncTask extends AsyncTask<Void, Void, Void> {
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
    		try {
    			//if (!getActivity().isFinishing()) {
    				alerts.get(alertToShow).show();
    			//}
    		} catch (Exception e) {
    			e.printStackTrace();
    			this.cancel(true);
    		}
    	}
    	
		@Override
		protected Void doInBackground(final Void... params) {
			
			while(alerts.get(alertToShow).isShowing()) {
				try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
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