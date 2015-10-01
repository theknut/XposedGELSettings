package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.graphics.Rect;

import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

public class GetWorkspacePaddingHook extends XGELSCallback {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#301
    // Rect getWorkspacePadding(int orientation)

    @Override
    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {

        // 0 = landscape
        // 1 = portrait
        int orientation;

        if (param.args[0] instanceof Boolean) {
            orientation = 1;
        } else {
            orientation = (Integer) param.args[0];
        }
        boolean isLandscape = orientation == 0;

        Rect padding = (Rect) param.getResult();

        if (PreferencesHelper.hideSearchBar) {
            padding.set(
                    isLandscape ? 0 : padding.left,
                    isLandscape ? padding.top : 0,
                    padding.right,
                    padding.bottom
            );
        } else if (PreferencesHelper.searchBarWeatherWidget) {

            padding.set(
                    isLandscape ? padding.left + Utils.dpToPx(12) : padding.left,
                    isLandscape ? padding.top : padding.top + Utils.dpToPx(12),
                    padding.right,
                    padding.bottom
            );
        }

        param.setResult(padding);
    }
}