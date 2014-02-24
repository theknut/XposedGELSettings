package de.theknut.xposedgelsettings.hooks.Homescreen;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class HomescreenHooks {

	public static void initAllHooks(LoadPackageParam lpparam) {
		
		// change the default homescreen
		final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
		XposedBridge.hookAllMethods(WorkspaceClass, "moveToDefaultScreen", new MoveToDefaultScreenHook());
		
		// animate background to semitransparent
		XposedBridge.hookAllMethods(WorkspaceClass, "animateBackgroundGradient", new AnimateBackgroundGradientHook());
		
		final Class<?> DeviceProfileClass = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);		
		// modify homescreen grid
		XposedBridge.hookAllConstructors(DeviceProfileClass, new DeviceProfileConstructorHook());
		
		if (PreferencesHelper.iconSettingsSwitchHome) {			
			final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
			// changing the appearence of the icons on the homescreen
			XposedBridge.hookAllMethods(CellLayoutClass, "addViewToCellLayout", new AddViewToCellLayoutHook());
		}
	}
}
