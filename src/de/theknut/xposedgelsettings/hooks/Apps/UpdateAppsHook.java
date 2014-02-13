package de.theknut.xposedgelsettings.hooks.Apps;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.util.ArrayList;

import android.content.ComponentName;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class UpdateAppsHook extends XC_MethodHook {
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {		
		ArrayList apps = (ArrayList) param.args[1];
		for (Object app : apps) {
			String title = (String) getObjectField(app, "title");
			ComponentName componentName = (ComponentName) getObjectField(app, "componentName");
			String fullAppName = componentName.getPackageName() + "#" + title;
			XposedBridge.log(fullAppName);
			if (PreferencesHelper.hiddenApps.contains(fullAppName)) {
				XposedBridge.log("Update: " + fullAppName);
				//PreferencesHelper.hiddenApps.remove(fullAppName);
			}
		}
	}
}