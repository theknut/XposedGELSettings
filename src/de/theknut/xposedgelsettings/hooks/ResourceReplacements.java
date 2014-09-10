package de.theknut.xposedgelsettings.hooks;

import android.graphics.Color;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

/**
 * Created by Alexander Schulz on 12.08.2014.
 */
public class ResourceReplacements extends XC_MethodHook implements IXposedHookInitPackageResources {

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!Common.PACKAGE_NAMES.contains(resparam.packageName)) {
            return;
        }

        ResourceReplacements.initAllReplacements(resparam);
    }

    public static void initAllReplacements(InitPackageResourcesParam resparam) {
        XSharedPreferences prefs = new XSharedPreferences(Common.PACKAGE_NAME);
        int glowColor = prefs.getInt("glowcolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));

        if (resparam.packageName.equals(Common.TREBUCHET_PACKAGE)) {
            resparam.res.setReplacement("com.android.launcher3", "color", "outline_color", glowColor);
        } else {
            resparam.res.setReplacement(resparam.packageName, "color", "outline_color", glowColor);
            resparam.res.setReplacement(resparam.packageName, "integer", "config_tabTransitionDuration", 0);
        }
    }
}
