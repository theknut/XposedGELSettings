package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;

public final class OnPageEndMovingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#599
	// protected void onPageEndMoving()
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (	Common.LAUNCHER_INSTANCE == null
			||	Common.GEL_INSTANCE == null) {
			return;
		}
		
		// show the search bar as soon as the page has stopped moving and the GNow overlay is visible
		if (getBooleanField(Common.GEL_INSTANCE, "mNowEnabled") && getBooleanField(Common.WORKSPACE_INSTANCE, "mCustomContentShowing")
			&& getIntField(Common.WORKSPACE_INSTANCE, "mCurrentPage") == 0) {
			
			GoogleSearchBarHooks.showSearchbar();
			
		} else if (!Common.IS_DRAGGING) {
			
			GoogleSearchBarHooks.hideSearchbar();
		}
	}	
}