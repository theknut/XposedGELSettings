package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class StopSearchHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		boolean hasGNowEnabled = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, "hasCustomContentToLeft");
		
		if ((hasGNowEnabled && getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage") != 0)
			|| !hasGNowEnabled) {
			
			// hide the search bar on stop search
			GoogleSearchBarHooks.hideSearchbar();
		}
	}
}