package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.graphics.Rect;
import de.robv.android.xposed.XC_MethodHook;

public class GetWorkspacePaddingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#301
	// Rect getWorkspacePadding(int orientation)
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	    
		// 0 = landscape
        // 1 = portrait
        int orientation;

        if (param.args.length == 0) {
            orientation = 1;
        } else {
            orientation = (Integer) param.args[0];
        }
        boolean isLandscape = orientation == 0;
        
        Rect padding = (Rect) param.getResult();
        padding.set(
            isLandscape ? 0 : padding.left,
            isLandscape ? padding.top : 0,
            padding.right,
            padding.bottom
        );

        param.setResult(padding);
	}
}