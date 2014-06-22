package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.ComponentName;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.ui.CommonUI;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

public final class AllAppsListAddHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
	// public void add(AppInfo info)

    List<String> packages = new ArrayList<String>();
    boolean init;
    final int APPINFO = 0;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (PreferencesHelper.iconPackHide && !init && Common.LAUNCHER_CONTEXT != null) {
            init = true;
            packages = CommonUI.getIconPacks(Common.LAUNCHER_CONTEXT);
        }

		String title = (String) getObjectField(param.args[0], Fields.itemInfoTitle);
		ComponentName componentName = (ComponentName) getObjectField(param.args[APPINFO], Fields.aiComponentName);

		if (PreferencesHelper.hiddenApps.contains(componentName.getPackageName() + "#" + title)
            || packages.contains(componentName.getPackageName())) {
			// don't add it to the allAppsList if it is in our list
			param.setResult(null);
		}
	}
}