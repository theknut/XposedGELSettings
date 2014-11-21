package de.theknut.xposedgelsettings.hooks.homescreen;

import android.graphics.Rect;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class GetWorkspacePaddingHook extends XGELSCallback {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#301
    // Rect getWorkspacePadding(int orientation)

    @Override
    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {

        if (PreferencesHelper.appdockSettingsSwitch && PreferencesHelper.hideAppDock) {

            int tmp = getIntField(param.thisObject, Fields.dpHotseatBarHeightPx);
            if (tmp != 0) {
                Common.HOTSEAT_BAR_HEIGHT = tmp;
                setIntField(param.thisObject, Fields.dpHotseatBarHeightPx, 0);
            }
        }
    }

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {

        // 0 = landscape
        // 1 = portrait
        int orientation;

        if (param.args.length == 0) {
            orientation = 1;
        } else {
            orientation = (Integer) param.args[0];
        }

        if (PreferencesHelper.changeGridSizeHome && orientation == 1) {
            Rect padding = (Rect) param.getResult();
            int multiplier = PreferencesHelper.workspaceRect;

            if (padding.left == 0 || padding.right == 0) {
                // give them something if they are 0
                padding.left = padding.right = 16;
            }

            padding.set(padding.left * multiplier, padding.top, padding.right * multiplier, padding.bottom);
            param.setResult(padding);
        }
    }
}
