package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.hooks.Common;

public class EnableAndShowSoundLevelsHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		// show the search bar
		GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Common.SEARCH_BAR_SPACE_WIDTH, Common.SEARCH_BAR_SPACE_HEIGHT);
	}
}