package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;

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
					//Common.OVERSCROLLED = false;
					
					if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherHasCustomContentToLeft)) {
						callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, 1);
					}
					else {
						callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, 0);
					}
				}
				else {					
					callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherShowWorkspace, true, null);
				}
			}

			if (PreferencesHelper.hideSearchBar) {
				GoogleSearchBarHooks.hideSearchbar();
			}
			
			callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherShowWorkspace, true, null);
		}
		else if (overscroll < -50.0) {
			
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				if (Common.OVERSCROLLED) {
					//Common.OVERSCROLLED = false;
					
					int lastPage = (Integer) callMethod(Common.WORKSPACE_INSTANCE, "getChildCount") - 1;
					callMethod(Common.WORKSPACE_INSTANCE, Methods.wSetCurrentPage, lastPage);
				}
			}
			
			if (PreferencesHelper.hideSearchBar) {
				GoogleSearchBarHooks.hideSearchbar();
			}
			
			callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherShowWorkspace, true, null);			
		}
	}
}