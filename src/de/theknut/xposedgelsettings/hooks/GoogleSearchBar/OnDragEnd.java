package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.view.View;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;

public final class OnDragEnd extends XGELSCallback {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/SearchDropTargetBar.java#202
    // public void onDragEnd()

    @Override
    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
        if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_4_24) return;
        // set the search bar hidden so that the animation wouldn't be shown (fade out)
        setBooleanField(param.thisObject, Fields.sdtbIsSearchBarHidden, true);
    }

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
        View qsb = (View) getObjectField(param.thisObject, Fields.sdtbQsbBar);
        qsb.setAlpha(1f);

        // hide the search bar
        Common.IS_DRAGGING = false;
        GoogleSearchBarHooks.hideSearchbar();
    }
}