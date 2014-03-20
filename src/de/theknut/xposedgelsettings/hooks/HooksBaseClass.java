package de.theknut.xposedgelsettings.hooks;

import java.text.DateFormat;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HooksBaseClass {
	
	static Calendar cal = Calendar.getInstance();
	static DateFormat sdf = DateFormat.getTimeInstance(DateFormat.SHORT);
	
	public static void log(String msg) {
		if (PreferencesHelper.Debug) {
			XposedBridge.log(sdf.format(cal.getTime()) + " XGELS: " + msg);
		}
	}
}
