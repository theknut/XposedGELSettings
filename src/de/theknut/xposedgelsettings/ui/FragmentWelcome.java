package de.theknut.xposedgelsettings.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;

public class FragmentWelcome extends FragmentBase {
	
	static boolean shown = false;
     
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
    	}
    	else {
    		return;
    	}
    	
    	if (!isXposedInstalled()) {
    		new AlertDialog.Builder(CommonUI.CONTEXT)
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
    	     })
    	     .show();
    	}
    	
    	if (!isXGELSActive()) {
    		new AlertDialog.Builder(CommonUI.CONTEXT)
    		.setCancelable(false)
    	    .setTitle("Module not active!")
    	    .setMessage("XGELS is not active. Please activate the module in Xposed Installer -> Modules")
    	    .setPositiveButton("Open Xposed Installer", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	        	Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("de.robv.android.xposed.installer");
    	        	startActivity(LaunchIntent);
    	        }
	        }
    	     )
    	    .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            dialog.dismiss();
    	        }
    	     })
    	     .show();
    	}
    	
    	if (!isInstalledFromPlay()) {
    		new AlertDialog.Builder(CommonUI.CONTEXT)
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
    	     .show();
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
}