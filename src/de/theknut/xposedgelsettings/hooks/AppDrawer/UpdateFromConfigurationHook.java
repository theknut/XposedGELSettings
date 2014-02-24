package de.theknut.xposedgelsettings.hooks.AppDrawer;

import static de.robv.android.xposed.XposedHelpers.setObjectField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class UpdateFromConfigurationHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#225
	// void updateFromConfiguration(Resources resources, int wPx, int hPx, int awPx, int ahPx)
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		if (PreferencesHelper.changeGridSizeApps) {
			// set custom app drawer column count
			setObjectField(param.thisObject, "allAppsNumCols", PreferencesHelper.xCountAllApps);
			
			// set custom app drawer row count
			setObjectField(param.thisObject, "allAppsNumRows", PreferencesHelper.yCountAllApps);
		}
	}
}