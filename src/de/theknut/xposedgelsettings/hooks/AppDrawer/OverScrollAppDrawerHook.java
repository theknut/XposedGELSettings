package de.theknut.xposedgelsettings.hooks.AppDrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.GoogleSearchBarHooks;

public class OverScrollAppDrawerHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/PagedView.java#1601
	// overScroll(float amount)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		float overscroll = (Float) param.args[0];
		
		if(overscroll > 50.0) {
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				if (Common.OVERSCROLLED) {
					Common.OVERSCROLLED = false;
					
					if (Common.GEL_INSTANCE != null && getBooleanField(Common.GEL_INSTANCE, "mNowEnabled")) {
						callMethod(Common.WORKSPACE_INSTANCE, "setCurrentPage", 1);
					}
					else {
						callMethod(Common.WORKSPACE_INSTANCE, "setCurrentPage", 0);
					}
				}
				else {					
					callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);
				}
			}			
			
			callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);
		}
		else if (overscroll < -50.0) {
			
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				if (Common.OVERSCROLLED) {
					Common.OVERSCROLLED = false;
					
					int lastPage = (Integer) callMethod(Common.WORKSPACE_INSTANCE, "getChildCount") - 1;
					callMethod(Common.WORKSPACE_INSTANCE, "setCurrentPage", lastPage);
				}
			}
			
			callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);			
			
			if (PreferencesHelper.hideSearchBar) {
				GoogleSearchBarHooks.setLayoutParams(getObjectField(param.thisObject, "mLauncher"), 0, 0, 0, 0);
			}
		}
	}
}