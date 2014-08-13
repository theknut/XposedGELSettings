package de.theknut.xposedgelsettings.hooks.resourcereplacements;

import android.content.res.Resources;

import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

/**
 * Created by Alexander Schulz on 12.08.2014.
 */
public class ResourceReplacements extends HooksBaseClass{

    public static void initAllReplacements(InitPackageResourcesParam resparam) {

        try {
            resparam.res.setReplacement(resparam.packageName, "color", "outline_color", PreferencesHelper.glowColor);
        } catch (Resources.NotFoundException nte) {
            // not working on Trebuchet for no reason...
        }
    }
}
