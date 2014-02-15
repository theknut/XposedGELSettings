package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.Common;
import de.theknut.xposedgelsettings.hooks.GELSettings;

public class EnableAndShowSoundLevelsHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		GELSettings.setLayoutParams(Common.LAUNCHER_INSTANCE, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Common.SEARCH_BAR_SPACE_WIDTH, Common.SEARCH_BAR_SPACE_HEIGHT);
	}
}