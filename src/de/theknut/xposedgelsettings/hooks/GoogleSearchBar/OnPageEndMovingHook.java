package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;

public final class OnPageEndMovingHook extends XC_MethodHook {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#599
    // protected void onPageEndMoving()

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (Common.LAUNCHER_INSTANCE == null) return;
        int page = getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage);
        boolean shouldShow = (page == 0 && PreferencesHelper.autoHideSearchBar) || (PreferencesHelper.searchBarOnDefaultHomescreen && page == (PreferencesHelper.defaultHomescreen - 1));

        // show the search bar as soon as the page has stopped moving and the GNow overlay is visible
        if ((Common.IS_TREBUCHET || (Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)) && shouldShow) {

            GoogleSearchBarHooks.showSearchbar();

        } else if (!Common.IS_DRAGGING) {

            GoogleSearchBarHooks.hideSearchbar();
        }
    }
}