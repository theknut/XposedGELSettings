package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public final class OnPageBeginMovingHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#595
	// protected void onPageBeginMoving()
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (	Common.LAUNCHER_INSTANCE == null
				||	Common.NOW_OVERLAY_INSTANCE == null
				||	Common.GEL_INSTANCE == null) {
				return;
		}
		
		if (getBooleanField(Common.GEL_INSTANCE, "mNowEnabled") && !Common.IS_DRAGGING) {
			GoogleSearchBarHooks.hideSearchbar();					
		}
	}
}