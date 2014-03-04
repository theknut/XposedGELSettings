package de.theknut.xposedgelsettings.hooks.Homescreen;

import static de.robv.android.xposed.XposedHelpers.setIntField;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class DeviceProfileConstructorHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#99
	// DeviceProfile(String n, float w, float h, float r, float c, float is, float its, float hs, float his)

	public static int NAME = 0;
	public static int MINWIDTHDPS = 1;
	public static int MINHEIGHTDPS = 2;
	public static int NUMROWS = 3;
	public static int NUMCOLUMNS = 4;
	public static int ICONSIZE = 5;
	public static int ICONTEXTSIZE = 6;
	public static int NUMHOTSEATICONS = 7;
	public static int HOTSEATICONSIZE = 8;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		// making sure to only hook to the appropriate constructor
		if (!(param.args[NAME] instanceof Context)) {
			
			if (PreferencesHelper.changeGridSizeHome) {				
				// set custom row count
				param.args[NUMROWS] = PreferencesHelper.yCountHomescreen;
				
				// set custom column count
				param.args[NUMCOLUMNS] = PreferencesHelper.xCountHomescreen;
			}
			
			if (PreferencesHelper.iconSettingsSwitchHome) {	
				
				// calculating custom sizes
				float newIconSize = (float) (Math.ceil((Float) param.args[ICONSIZE] * (PreferencesHelper.iconSize / 100.0)));
				float newHotseatIconSize = (float) (Math.ceil((Float) param.args[HOTSEATICONSIZE] * (PreferencesHelper.hotseatIconSize / 100.0)));
				float newIconTextSize = (float) (Math.ceil((Float) param.args[ICONTEXTSIZE] * (PreferencesHelper.iconTextSize / 100.0)));
				
				// some validation
				if (newIconSize > 0.0 ) {
					param.args[ICONSIZE] = Common.NEW_ICON_SIZE = newIconSize;
				}
				else {
					XposedBridge.log("Didn't change icon size! Value was " + newIconSize);
				}
				
				// some validation
				if (newHotseatIconSize > 0.0) {
					param.args[HOTSEATICONSIZE] = Common.NEW_HOTSEAT_ICON_SIZE = newHotseatIconSize;
				}
				else {
					XposedBridge.log("Didn't change hotseat icon size! Value was " + newHotseatIconSize);
				}
				
				// some validation
				if (newIconTextSize > 0.0) {
					param.args[ICONTEXTSIZE] = newIconTextSize;
				}
				else {
					XposedBridge.log("Didn't change icon text size! Value was " + newIconTextSize);
				}
				
				// number of hotseat icons also includes the app drawer so there has to be an odd number
				param.args[NUMHOTSEATICONS] = PreferencesHelper.hotseatCount + 1;
			}
			
			int hotseatBarHeight = (int) (Math.round((Float)param.args[ICONSIZE]) + 24);
			setIntField(param.thisObject, "hotseatBarHeightPx", hotseatBarHeight);
		}
	}
}