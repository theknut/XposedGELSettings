package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XC_MethodHook;
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

        Common.HOOKED_PACKAGE = resparam.packageName;
        Common.IS_TREBUCHET = Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE);

        PreferencesHelper.init();
        ResourceReplacements.initAllReplacements(resparam);
    }

    public static void initAllReplacements(InitPackageResourcesParam resparam) {

        if (Common.IS_TREBUCHET) {
            resparam.res.setReplacement("com.android.launcher3", "color", "outline_color", PreferencesHelper.glowColor);
        } else {
            resparam.res.setReplacement(resparam.packageName, "color", "outline_color", PreferencesHelper.glowColor);
            resparam.res.setReplacement(resparam.packageName, "integer", "config_tabTransitionDuration", 0);
        }
    }
}
