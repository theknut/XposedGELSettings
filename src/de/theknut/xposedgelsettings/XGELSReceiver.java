package de.theknut.xposedgelsettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.hooks.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class XGELSReceiver extends BroadcastReceiver {
	
	public static Object sbservice = null;
	public static Class<?> statusbarManager = null;
	public static Method showNotificationPanel = null;
	public static Method showSettingsPanel = null;
	public static String NOTIFICATION_PANEL = "expandNotificationsPanel";
	public static String SETTINGS_PANEL = "expandSettingsPanel";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Common.PACKAGE_NAME + ".Intent")) {
        	
        	if (intent.getStringExtra("XGELSACTION").equals("NOTIFICATION_BAR")) {
        		showNotificationPanel(context);
			}
        	else if (intent.getStringExtra("XGELSACTION").equals("SETTINGS_BAR")) {
        		showSettingsPanel(context);
        	}
        }
    }
	
	public static boolean init(Context context) {
    	
    	if (sbservice != null && statusbarManager != null && showNotificationPanel != null && showSettingsPanel != null) {
    		return true;
    	}
    	
    	try {
    		sbservice = context.getSystemService("statusbar");				
    		statusbarManager = Class.forName("android.app.StatusBarManager");
    		showNotificationPanel = statusbarManager.getMethod(NOTIFICATION_PANEL);
    		showSettingsPanel = statusbarManager.getMethod(SETTINGS_PANEL);
    		
    		if (sbservice != null && statusbarManager != null && showNotificationPanel != null && showSettingsPanel != null) {
    			Common.IS_INIT = true;
    			return true;
    		}
    		
    		return false;
    	} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			
			return false;
		}    	
    }
    
    public void showNotificationPanel(Context context) {    	
		try {
			if (init(context)) {
				showNotificationPanel.invoke(sbservice);
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
    
    public void showSettingsPanel(Context context) {    	
		try {
			if (init(context)) {
				showSettingsPanel.invoke(sbservice);
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.toast_fail, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
    }
}