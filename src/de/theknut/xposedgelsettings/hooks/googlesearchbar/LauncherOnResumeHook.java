package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public final class LauncherOnResumeHook extends HooksBaseClass {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (!((Boolean) callMethod(param.thisObject, Methods.lIsAllAppsVisible))
                && !(Boolean) callMethod(Common.WORKSPACE_INSTANCE, Methods.wIsOnOrMovingToCustomContent)
                && getObjectField(Common.WORKSPACE_INSTANCE, Fields.wState).toString().equals("NORMAL")) {

            int currentPage = getIntField(Common.WORKSPACE_INSTANCE, Fields.wCurrentPage);
            if (currentPage == (PreferencesHelper.defaultHomescreen - 1)) {

                GoogleSearchBarHooks.showSearchbar();
            }
        }
    }
}