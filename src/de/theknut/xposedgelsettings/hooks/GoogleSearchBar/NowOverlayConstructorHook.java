package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.Common;

public final class NowOverlayConstructorHook extends XC_MethodHook {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		Common.NOW_OVERLAY_INSTANCE = param.thisObject;
	}	
}
