package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class OverScrollWorkspaceHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#1536
	// overScroll(float amount)
	
	enum State { NORMAL, SPRING_LOADED, SMALL, OVERVIEW};
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		float overscroll = (Float) param.args[0];
		boolean isPageMoving = getBooleanField(param.thisObject, "mIsPageMoving");
		
		if(overscroll > 50.0 && isPageMoving) {
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				callMethod(getObjectField(param.thisObject, "mLauncher"), "showAllApps", true, Common.CONTENT_TYPE, true);
				Common.OVERSCROLLED = true;
			}
			else {				
				if (Common.GEL_INSTANCE != null && getBooleanField(Common.GEL_INSTANCE, "mNowEnabled")) {
					callMethod(Common.WORKSPACE_INSTANCE, "moveToScreen", 1, true);
				}
				else {
					callMethod(Common.WORKSPACE_INSTANCE, "moveToScreen", 0, true);
				}
			}
		}
		else if (overscroll < -50.0 && isPageMoving) {
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				callMethod(getObjectField(param.thisObject, "mLauncher"), "showAllApps", true, Common.CONTENT_TYPE, true);
				
				int lastPage = (Integer) callMethod(Common.APP_DRAWER_INSTANCE, "getChildCount") - 1;
				callMethod(Common.APP_DRAWER_INSTANCE, "setCurrentPage", lastPage);
				Common.OVERSCROLLED = true;
			}
			else {
				int lastPage = (Integer) callMethod(Common.WORKSPACE_INSTANCE, "getChildCount") - 1;
				callMethod(Common.WORKSPACE_INSTANCE, "moveToScreen", lastPage, true);
			}
		}
	}
}