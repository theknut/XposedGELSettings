package de.theknut.xposedgelsettings.hooks.notificationbadges;

import static de.robv.android.xposed.XposedHelpers.callMethod;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.View.MeasureSpec;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class NotificationBadgesHelper extends HooksBaseClass {
	
	static PackageManager pm;
	static ActivityManager activityManager;
	
	static List<Shortcut> shortcutsDesktop = new ArrayList<Shortcut>();
	static List<Shortcut> shortcutsAppDrawer = new ArrayList<Shortcut>();
	static List<FolderIcon> folders = new ArrayList<FolderIcon>();
	static List<ShortcutInfo> shortcutsToUpdate = new ArrayList<ShortcutInfo>();
	
	static int displayWidth = -1, displayHeigth = -1;
	static int measuredWidth = -1, measuredHeigth = -1;
	static int leftRightPadding, topBottomPadding;
	static int frameSize, cornerRadius;
	
	static class ShortcutInfo {
		
		public Shortcut shortcut;
		public int cnt;
		
		public ShortcutInfo (Shortcut shortcut, int cnt) {
			this.shortcut = shortcut;
			this.cnt = cnt;
		}
	}
	
	static BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context context, Intent intent) {
    		
    		// being a little bit lazy
    		Intent i = intent;
    		String action = i.getAction();
    		
    		if (DEBUG) log("Received Intent: " + action);
    		
    		if (action.equals(Common.MISSEDIT_CALL_NOTIFICATION)) {
    			
    			if (i.hasExtra("COUNT")) {
    				handleMissedCalls(i.getIntExtra("COUNT", 0));
    			}
    			
    		} else if (action.equals(Common.MISSEDIT_SMS_NOTIFICATION)) {
    			
    			if (i.hasExtra("COUNT")) {
    				handleUnreadSMS(i.getIntExtra("COUNT", 0));
    			}
    			
    		} else if (action.equals(Common.MISSEDIT_GMAIL_NOTIFICATION)) {
    			if (DEBUG) log("GMAIL NOTIFICATION " + i.getStringExtra("ACCOUNT"));
    			
    			requestCounters();
    			
    		} else if (action.equals(Common.MISSEDIT_APP_NOTIFICATION)) {
    			if (DEBUG) log("APP NOTIFICATION " + i.getStringExtra("COMPONENTNAME"));
    			
    			if (i.hasExtra("COMPONENTNAME")) {
    				
    				String componentName = i.getStringExtra("COMPONENTNAME");
    				if (componentName.contains("/")) {
    					componentName = componentName.substring(0, componentName.indexOf("/"));
    				}
    				
    				if (!setBadges(getShortcut(componentName), i)) {
    					if (DEBUG) log("APP_NOTIFICATION - No shortcut found");
	        		}
        		}
    		} else if (action.equals(Common.MISSEDIT_COUNTERS_STATUS)) {
    			
    			Bundle bundles = i.getBundleExtra("MISSED_CALLS");
    			
    			if (bundles != null) {    				
    				handleMissedCalls(bundles.getInt("COUNT"));
    			} else {
    				if (DEBUG) log("No MISSED_CALLS");
    			}
    			
    			bundles = i.getBundleExtra("UNREAD_SMS");
    			
    			if (bundles != null) {        			
        			handleUnreadSMS(bundles.getInt("COUNT"));
    			} else {
    				if (DEBUG) log("No UNREAD_SMS");
    			}
    			
    			bundles = i.getBundleExtra("PENDING_VOICEMAILS");
    			
    			if (bundles != null) {        			
        			//handleUnreadSMS(bundles.getInt("COUNT"));
    			} else {
    				if (DEBUG) log("No PENDING_VOICEMAILS ");
    			}
    			
    			handleEMailBundles(i, "GMAIL_ACCOUNTS", "com.google.android.gm");
    			handleEMailBundles(i, "K9MAIL_ACCOUNTS", "com.fsck.k9");
    			handleEMailBundles(i, "AQUAMAIL_ACCOUNTS", "org.kman.AquaMail");
    			
    			bundles = i.getBundleExtra("APPLICATIONS");
    			
    			if (bundles != null) {
    				
					for (int j = 0; j < bundles.size(); j++) {
    				
	        			Bundle bundle = bundles.getBundle("" + j);				        			
	        			if (bundle != null) {
	        				
	        				if (bundle.containsKey("COMPONENTNAME")) {
	            				
	            				String componentName = bundle.getString("COMPONENTNAME");
	            				if (DEBUG) log("App " + componentName);
	            				if (componentName.contains("/.")) {
	            					componentName = componentName.substring(0, componentName.indexOf("/."));
	            				}
		        				
		        				if (!setBadges(getShortcut(componentName), bundle)) {
		    	    				if (DEBUG) log("APP_NOTIFICATION - No shortcut found");
		    	        		}
	        				}
        				}
					}
    			} else {
    				if (DEBUG) log("No APPLICATIONS");
    			}
    		} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
    			
    			requestCounters();
    		}
    		
    		if (!action.equals(Intent.ACTION_BOOT_COMPLETED)) {
    			updateFolders();
    		}
    	}
    	
    	void handleEMailBundles(Intent intent, String bundleName, String componentName) {
    		
    		Bundle bundles = intent.getBundleExtra(bundleName);
			
			if (bundles != null) {    				
				List<Shortcut> shortcuts = getShortcut(componentName);
				
				for (Shortcut shortcut : shortcuts) {
					
					int totalCnt = 0;
					for (int j = 0; j < bundles.size(); j++) {
					
	        			Bundle bundle = bundles.getBundle("" + j);				        			
	        			if (bundle != null) {
			        		if (bundle.containsKey("COUNT")) {
			        			totalCnt += bundle.getInt("COUNT", 0);
			        			shortcut.setBadge(totalCnt);
			        		}
	    				}
					}
				}    				
			} else {
				if (DEBUG) log("No " + bundleName);
				
				// reset badges since there is no notification left
				if (!setBadges(getShortcut(componentName), 0)) {
					if (DEBUG) log("Reset " + bundleName + " Badge - No shortcut found");
        		}
			}
    	}
    	
    	void handleUnreadSMS(int cnt) {
    		
    		if (pm == null) pm = Common.LAUNCHER_CONTEXT.getPackageManager();
			
			Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			smsIntent.setType("vnd.android-dir/mms-sms");
			ResolveInfo mInfo = pm.resolveActivity(smsIntent, 0);
			
			try {
    			if (!setBadges(getShortcut(mInfo.activityInfo.packageName), cnt)) {
    				if (DEBUG) log("SMS_NOTIFICATION - No shortcut found");
        		}
			} catch (Exception ex) {
				log("Couldn't resolve default sms app. You may want to add it as an application in MissedIt!");
				log("Show this to the dev: " + ex);
			}
    	}
    	
    	void handleMissedCalls(int cnt) {
    		
    		if (pm == null) pm = Common.LAUNCHER_CONTEXT.getPackageManager();
	    	
			try {
    			ResolveInfo mInfo = pm.resolveActivity(new Intent(Intent.ACTION_DIAL) , 0);
    			
    			if (!setBadges(getShortcut(mInfo.activityInfo.packageName), cnt)) {
    				if (DEBUG) log("CALL_NOTIFICATION - No shortcut found");
        		}
    		} catch (Exception ex) {
				log("Couldn't resolve default caller app. You may want to add it as an application in MissedIt!");
				log("Show this to the dev: " + ex);
			}
    	}
    	
    	List<Shortcut> getShortcut(String componentName) {
    		
    		List<Shortcut> shortcuts;
    		List<Shortcut> ret = new ArrayList<Shortcut>();
    		boolean isAllAppsVisible = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, "isAllAppsVisible");
    		
    		if (isAllAppsVisible) {
    			shortcuts = shortcutsAppDrawer;
    			if (DEBUG) log("getShortcut: Searching " + componentName + " using shortcutsAppDrawer");
    		} else {
    			shortcuts = shortcutsDesktop;
    			if (DEBUG) log("getShortcut: Searching " + componentName + " using shortcutsDesktop");
    		}
    			
    		for (Shortcut shortcut : shortcuts) {
    			if (shortcut.pgkName.equals(componentName)) {
    				if (DEBUG) log("getShortcut: Add " + componentName + " (ID" + shortcut.childID + ")");
    				ret.add(shortcut);
    			}
    		}
    		
    		for (FolderIcon folder : folders) {
    			
    			for (Shortcut shortcut : folder.children) {
    				if (shortcut.pgkName.equals(componentName)) { 
    					if (DEBUG) log("getShortcut: Found " + componentName + " (ID" + shortcut.childID + ") in folder " + folder.folderName + " (ID" + folder.childID + ")");
    					ret.add(shortcut);
        			}
    			}
    		}
    		
    		return ret;
    	}
    	
    	boolean setBadges(List<Shortcut> shortcuts, Bundle bundle) {
    		
    		if (bundle.containsKey("COUNT")) {
    			return setBadges(shortcuts, bundle.getInt("COUNT", 0));
    		}
    		
    		return false;
    	}
    	
    	boolean setBadges(List<Shortcut> shortcuts, Intent intent) {
    		
    		if (intent.hasExtra("COUNT")) {
    			return setBadges(shortcuts, intent.getIntExtra("COUNT", 0));
    		}
    		
    		return false;
    	}
    	
    	boolean setBadges(List<Shortcut> shortcuts, int cnt) {
    		
    		if (shortcuts.size() != 0) {
    			
    			for (Shortcut shortcut : shortcuts) {
    				shortcut.setBadge(cnt);
        		}
    			
    			return true;
			}
    		
    		return false;
    	}
    	
    	void updateFolders() {
    		
    		for (FolderIcon folder : folders) {
    			
    			int totalCnt = 0;
    			for (Shortcut shortcut : folder.children) {
    				totalCnt += shortcut.currCnt;
    			}
    			
    			folder.setBadge(totalCnt);
    		}
    	}    	
    };
	
	public static void requestCounters() {
		
		// start or call MissedIt service in order to receive notification intents
		Common.LAUNCHER_CONTEXT.startService(new Intent(Common.MISSEDIT_REQUESET_COUNTERS));
	}
	
	public static void initMeasures() {
		long time = System.currentTimeMillis();
		//if (displayWidth == -1) {
			WindowManager wm = (WindowManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			displayWidth = size.x;
			displayHeigth = size.y;
			
			DisplayMetrics displayMetrics = Common.LAUNCHER_CONTEXT.getResources().getDisplayMetrics();
			
			measuredWidth = MeasureSpec.makeMeasureSpec(NotificationBadgesHelper.displayWidth, MeasureSpec.AT_MOST);
			measuredHeigth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			
			leftRightPadding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeLeftRightPadding, displayMetrics));
			topBottomPadding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeTopBottomPadding, displayMetrics));
			
			frameSize = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PreferencesHelper.notificationBadgeFrameSize, displayMetrics));
			
			if (DEBUG) log("InitMeasures - width: " + displayWidth +" height: " + displayHeigth + " took " + (System.currentTimeMillis() - time) + "ms");
	}
}