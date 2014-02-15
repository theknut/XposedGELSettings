package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.Common;
import de.theknut.xposedgelsettings.hooks.GELSettings;

public class StopSearchHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable
	{
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		GELSettings.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);
	}
}