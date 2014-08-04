package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.ComponentName;

import java.util.ArrayList;
import java.util.Iterator;
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
    final int APPINFOLIST = 0;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (PreferencesHelper.iconPackHide && !init && Common.LAUNCHER_CONTEXT != null) {
            init = true;
            packages = CommonUI.getIconPacks(Common.LAUNCHER_CONTEXT);
        }

        ArrayList appInfoList = (ArrayList) param.args[APPINFOLIST];
        Iterator it = appInfoList.iterator();

        while(it.hasNext()) {
            Object appInfo = it.next();
            String title = (String) getObjectField(appInfo, Fields.iiTitle);
            ComponentName componentName = (ComponentName) getObjectField(appInfo, Fields.aiComponentName);

            if (PreferencesHelper.hiddenApps.contains(componentName.getPackageName() + "#" + title)
                    || packages.contains(componentName.getPackageName())) {
                // don't add it to the allAppsList if it is in our list
                it.remove();
            }
        }
	}
}