package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

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
			
			if (getBooleanField(Common.GEL_INSTANCE, "mNowEnabled") && getBooleanField(Common.NOW_OVERLAY_INSTANCE, "mVisible")) {
				GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);					
			}
		
//		if (Common.LAUNCHER_INSTANCE == null)
//			return;
//		
//		
//		if (getIntField(param.thisObject, "mCurrentPage") == 0) {
//			GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);					
//		}
	}
}