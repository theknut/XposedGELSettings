package de.theknut.xposedgelsettings.hooks.Apps;

import static de.robv.android.xposed.XposedHelpers.setObjectField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class AllAppsGridHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (PreferencesHelper.changeGridSize) {
			setObjectField(param.thisObject, "allAppsNumCols", PreferencesHelper.xCountAllApps);
			setObjectField(param.thisObject, "allAppsNumRows", PreferencesHelper.yCountAllApps);
		}
	}
}