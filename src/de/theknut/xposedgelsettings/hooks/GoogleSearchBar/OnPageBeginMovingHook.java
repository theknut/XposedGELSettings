package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public final class OnPageBeginMovingHook extends XGELSCallback {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#595
    // protected void onPageBeginMoving()

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
        if (Common.LAUNCHER_INSTANCE == null) return;

        if ((Common.IS_TREBUCHET || (Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)) && !Common.IS_DRAGGING) {
            GoogleSearchBarHooks.hideSearchbar();
        }
    }
}