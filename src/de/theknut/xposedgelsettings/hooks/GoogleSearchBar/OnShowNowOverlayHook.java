package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;

public class OnShowNowOverlayHook extends XC_MethodHook {
	
	// this method is called when the Google Now overlay is shown
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (Common.LAUNCHER_INSTANCE == null) {	return;	}
		
		if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
			&& getBooleanField(Common.WORKSPACE_INSTANCE, Fields.wCustomContentShowing)
			&& getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage) == 0) {
			
			GoogleSearchBarHooks.showSearchbar();
		}
	}
}