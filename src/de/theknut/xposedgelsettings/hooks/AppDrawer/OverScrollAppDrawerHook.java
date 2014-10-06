package de.theknut.xposedgelsettings.hooks.appdrawer;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class OverScrollAppDrawerHook extends XC_MethodHook {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#1601
    // overScroll(float amount)

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable
    {
        float overscroll = (Float) param.args[0];

        if(overscroll > 50.0) {
            if (PreferencesHelper.continuousScrollWithAppDrawer) {
                if (Common.OVERSCROLLED) {
                    //Common.OVERSCROLLED = false;

                    if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)) {
                        callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, 1);
                    }
                    else {
                        callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, 0);
                    }
                }

                callMethod(Common.LAUNCHER_INSTANCE, Methods.lShowWorkspace, true, null);
            }

            if (PreferencesHelper.hideSearchBar) {
                GoogleSearchBarHooks.hideSearchbar();
            }
        }
        else if (overscroll < -50.0) {

            if (PreferencesHelper.continuousScrollWithAppDrawer) {
                if (Common.OVERSCROLLED) {
                    //Common.OVERSCROLLED = false;

                    int lastPage = (Integer) callMethod(Common.WORKSPACE_INSTANCE, "getChildCount") - 1;
                    callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, lastPage);
                }

                callMethod(Common.LAUNCHER_INSTANCE, Methods.lShowWorkspace, true, null);
            }

            if (PreferencesHelper.hideSearchBar) {
                GoogleSearchBarHooks.hideSearchbar();
            }
        }
    }
}