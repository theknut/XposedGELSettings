package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class EnableAndShowSoundLevelsHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		// show the search bar
		GoogleSearchBarHooks.showSearchbar();
	}
}