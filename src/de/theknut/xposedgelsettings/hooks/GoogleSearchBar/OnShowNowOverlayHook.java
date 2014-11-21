package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;

public class OnShowNowOverlayHook extends XGELSCallback {

    // this method is called when the Google Now overlay is shown

    @Override
    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {

        if (Common.LAUNCHER_INSTANCE == null) {	return;	}

        if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                && getBooleanField(Common.WORKSPACE_INSTANCE, Fields.wCustomContentShowing)
                && getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage) == 0) {

            GoogleSearchBarHooks.showSearchbar();
        }
    }
}