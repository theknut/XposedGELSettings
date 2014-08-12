package de.theknut.xposedgelsettings.hooks.resourcereplacements;

import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

/**
 * Created by Alexander Schulz on 12.08.2014.
 */
public class ResourceReplacements {

    public static void initAllReplacements(InitPackageResourcesParam resparam) {

        resparam.res.setReplacement(resparam.packageName, "color", "outline_color", PreferencesHelper.glowColor);
    }
}
