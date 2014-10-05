package de.theknut.xposedgelsettings.hooks.appdrawer;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class UpdateFromConfigurationHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#225
	// void updateFromConfiguration(Resources resources, int wPx, int hPx, int awPx, int ahPx)
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {

        if (PreferencesHelper.xCountAllApps == -1) {
            Common.ALL_APPS_X_COUNT = getIntField(param.thisObject, Fields.acpvAllAppsNumCols);
        } else {
            Common.ALL_APPS_X_COUNT = PreferencesHelper.xCountAllApps;
        }
        if (PreferencesHelper.yCountAllApps == -1) {
            Common.ALL_APPS_Y_COUNT = getIntField(param.thisObject, Fields.acpvAllAppsNumRows);
        } else {
            Common.ALL_APPS_Y_COUNT = PreferencesHelper.yCountAllApps;
        }

		if (PreferencesHelper.changeGridSizeApps) {
			// set custom app drawer column count
			setObjectField(param.thisObject, Fields.acpvAllAppsNumCols, Common.ALL_APPS_X_COUNT);
			
			// set custom app drawer row count
			setObjectField(param.thisObject, Fields.acpvAllAppsNumRows, PreferencesHelper.yCountAllApps);
		}

        if (PreferencesHelper.iconSettingsSwitchApps) {
            if (Common.APP_DRAWER_ICON_SIZE == -1) {
                Common.APP_DRAWER_ICON_SIZE = (int) Math.round(getIntField(param.thisObject, Fields.dpAllAppsIconSize) * (PreferencesHelper.iconSizeAppDrawer / 100.0));
            }

            setIntField(param.thisObject, Fields.dpAllAppsIconSize, Common.APP_DRAWER_ICON_SIZE);
        }
	}
}