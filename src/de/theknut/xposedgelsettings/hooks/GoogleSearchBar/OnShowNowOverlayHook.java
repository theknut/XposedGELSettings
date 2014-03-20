package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class OnShowNowOverlayHook extends XC_MethodHook {
	
	// this method is called when the Google Now overlay is shown
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		if (	Common.LAUNCHER_INSTANCE == null
				||	Common.NOW_OVERLAY_INSTANCE == null
				||	Common.GEL_INSTANCE == null) {
				return;
			}
		
		if (getBooleanField(Common.GEL_INSTANCE, "mNowEnabled") && getBooleanField(Common.WORKSPACE_INSTANCE, "mCustomContentShowing")
				&& getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage") == 0) {
			GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
	}
}