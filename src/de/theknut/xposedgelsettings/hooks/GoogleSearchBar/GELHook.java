package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.Common;

public class GELHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		Common.GEL_INSTANCE = param.thisObject;
	}	
}