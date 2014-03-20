package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.ComponentName;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class AllAppsListAddHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
	// public void add(AppInfo info)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {		
		String title = (String) getObjectField(param.args[0], "title");
		ComponentName componentName = (ComponentName) getObjectField(param.args[0], "componentName");
		
		if (PreferencesHelper.hiddenApps.contains(componentName.getPackageName() + "#" + title)) {
			// don't add it to the allAppsList if it is in our list
			param.setResult(null);
		}
	}
}