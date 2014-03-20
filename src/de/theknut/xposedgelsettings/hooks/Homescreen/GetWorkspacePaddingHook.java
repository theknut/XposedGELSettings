package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class GetWorkspacePaddingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#301
	// Rect getWorkspacePadding(int orientation)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		int tmp = getIntField(param.thisObject, "hotseatBarHeightPx");
		if (tmp != 0) {
			Common.HOTSEAT_BAR_HEIGHT = tmp;
			setIntField(param.thisObject, "hotseatBarHeightPx", 0);
		}
	}
}
