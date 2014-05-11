package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

public class OnShowNowOverlayHook extends XC_MethodHook {
	
	// this method is called when the Google Now overlay is shown
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (Common.LAUNCHER_INSTANCE == null) {	return;	}
		
		if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherHasCustomContentToLeft)
			&& getBooleanField(Common.WORKSPACE_INSTANCE, Fields.workspaceCustomContentShowing)
			&& getIntField(Common.WORKSPACE_INSTANCE, Fields.workspaceCurrentPage) == 0) {
			
			GoogleSearchBarHooks.showSearchbar();
		}
	}
}