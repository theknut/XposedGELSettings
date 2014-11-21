package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.view.View;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;

public final class OnDragStart extends XGELSCallback {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/SearchDropTargetBar.java#187
    // public void onDragEnd()

    @Override
    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {

        // make the search bar invisible
        View qsb = (View) getObjectField(param.thisObject, Fields.sdtbQsbBar);
        qsb.setAlpha(0f);

        // set the search bar to hidden
        setBooleanField(param.thisObject, Fields.sdtbIsSearchBarHidden, true);
    }

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
        Common.IS_DRAGGING = true;

        // show the search bar
        GoogleSearchBarHooks.showSearchbar();
    }
}