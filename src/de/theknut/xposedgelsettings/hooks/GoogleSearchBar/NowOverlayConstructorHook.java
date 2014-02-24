package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public final class NowOverlayConstructorHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		// save the instance
		Common.NOW_OVERLAY_INSTANCE = param.thisObject;
	}	
}
