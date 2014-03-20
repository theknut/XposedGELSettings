package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class StopSearchHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		// hide the search bar on stop search
		GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);
	}
}