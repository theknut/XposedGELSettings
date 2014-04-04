package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class StartTextSearchHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		// show the search if a text search is started
		GoogleSearchBarHooks.showSearchbar();
	}
}