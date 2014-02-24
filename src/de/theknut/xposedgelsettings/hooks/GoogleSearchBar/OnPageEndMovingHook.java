package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import android.view.ViewGroup.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public final class OnPageEndMovingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#599
	// protected void onPageEndMoving()
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (	Common.LAUNCHER_INSTANCE == null
			||	Common.NOW_OVERLAY_INSTANCE == null
			||	Common.GEL_INSTANCE == null) {
			return;
		}
		
		// show the search bar as soon as the page has stopped moving and the GNow overlay is visible
		if (getBooleanField(Common.GEL_INSTANCE, "mNowEnabled") && getBooleanField(Common.NOW_OVERLAY_INSTANCE, "mVisible")) {
			GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Common.SEARCH_BAR_SPACE_WIDTH, Common.SEARCH_BAR_SPACE_HEIGHT);					
		}
	}	
}