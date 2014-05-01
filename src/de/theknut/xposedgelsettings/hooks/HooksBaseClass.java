package de.theknut.xposedgelsettings.hooks;

import java.text.DateFormat;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HooksBaseClass extends XC_MethodHook {
	
	protected static boolean DEBUG = PreferencesHelper.Debug;
	
	public static void log(String msg) {
		String timestamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		XposedBridge.log(timestamp + " XGELS| " + msg);
	}
	
	public static void log(MethodHookParam param, String msg) {
		String timestamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
		XposedBridge.log(timestamp + " XGELS| " + param.method.getName() + ": " + msg);
	}	
}