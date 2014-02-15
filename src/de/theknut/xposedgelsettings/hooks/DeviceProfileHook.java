package de.theknut.xposedgelsettings.hooks;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public final class DeviceProfileHook extends XC_MethodHook {

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
		if (!(param.args[NAME] instanceof Context)) {
			
			if (PreferencesHelper.changeGridSize) {
				param.args[NUMROWS] = PreferencesHelper.yCountHomescreen;
				param.args[NUMCOLUMNS] = PreferencesHelper.xCountHomescreen;
			}
			
			if (PreferencesHelper.iconSettingsSwitch) {
				float newIconSize = (float) (Math.ceil((Float) param.args[ICONSIZE] * (PreferencesHelper.iconSize / 100.0)));
				float newHotseatIconSize = (float) (Math.ceil((Float) param.args[HOTSEATICONSIZE] * (PreferencesHelper.hotseatIconSize / 100.0)));
				float newIconTextSize = (float) (Math.ceil((Float) param.args[ICONTEXTSIZE] * (PreferencesHelper.iconTextSize / 100.0)));
				
				if (newIconSize > 0.0 ) {
					param.args[ICONSIZE] = newIconSize;
				}
				else {
					XposedBridge.log("Didn't change icon size! Value was " + newIconSize);
				}
				
				if (newHotseatIconSize > 0.0) {
					param.args[HOTSEATICONSIZE] = newHotseatIconSize;
				}
				else {
					XposedBridge.log("Didn't change hotseat icon size! Value was " + newHotseatIconSize);
				}
				
				if (newIconTextSize > 0.0) {
					param.args[ICONTEXTSIZE] = newIconTextSize;
				}
				else {
					XposedBridge.log("Didn't change icon text size! Value was " + newIconTextSize);
				}
				
				// number of hotseat icons also includes the app drawer so there has to be an odd number
				param.args[NUMHOTSEATICONS] = PreferencesHelper.hotseatCount + 1;
			}
		}
	}
}