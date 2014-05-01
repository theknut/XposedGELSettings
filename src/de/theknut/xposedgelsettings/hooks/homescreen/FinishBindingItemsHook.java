package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class FinishBindingItemsHook extends HooksBaseClass {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		if (PreferencesHelper.defaultHomescreen == -1) {
			boolean gnow = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, "hasCustomContentToLeft");
			
			if (gnow) {
				PreferencesHelper.defaultHomescreen = 2;
			} else {
				PreferencesHelper.defaultHomescreen = 1;
			}
			
			if (DEBUG) log(param, "Setting default homescreen = " + PreferencesHelper.defaultHomescreen);
		}
		
		// move to default screen
		callMethod(Common.WORKSPACE_INSTANCE, "moveToDefaultScreen", true);
	}
}