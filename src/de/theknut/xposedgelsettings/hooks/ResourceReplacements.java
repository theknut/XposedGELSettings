package de.theknut.xposedgelsettings.hooks;

import android.content.res.Resources;

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

        PreferencesHelper.init();
        ResourceReplacements.initAllReplacements(resparam);
    }

    public static void initAllReplacements(InitPackageResourcesParam resparam) {

        try {
            resparam.res.setReplacement(resparam.packageName, "color", "outline_color", PreferencesHelper.glowColor);
        } catch (Resources.NotFoundException nte) {
            // not working on Trebuchet for no reason...
            resparam.res.setReplacement("com.android.launcher3", "color", "outline_color", PreferencesHelper.glowColor);
        }
    }
}
