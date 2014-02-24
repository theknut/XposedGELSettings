package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class OnShowNowOverlayHook extends XC_MethodHook {
	
	// this method is called when the Google Now overlay is shown
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}
}