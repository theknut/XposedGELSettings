package de.theknut.xposedgelsettings.hooks.Apps;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.ComponentName;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AllAppsListHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {		
		String title = (String) getObjectField(param.args[0], "title");
		ComponentName componentName = (ComponentName) getObjectField(param.args[0], "componentName");
		
		if (PreferencesHelper.hiddenApps.contains(componentName.getPackageName() + "#" + title)) {
			param.setResult(null);
		}
	}
}