package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

public class OverScrollWorkspaceHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#1536
	// overScroll(float amount)
	
	boolean boom = false;
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable
	{
		float overscroll = (Float) param.args[0];
		boolean isPageMoving = getBooleanField(param.thisObject, Fields.pvIsPageMoving);
		boolean isSwitchingState = getBooleanField(param.thisObject, Fields.wIsSwitchingState);
		
		if (!isPageMoving || isSwitchingState) {
			return;
		}
		
		if (overscroll > 50.0) {
			
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				
				callMethod(Common.LAUNCHER_INSTANCE, "onClickAllAppsButton", new View(Common.LAUNCHER_CONTEXT));//Methods.launcherShowAllApps, true, Common.CONTENT_TYPE, !PreferencesHelper.appdrawerRememberLastPosition);
				Common.OVERSCROLLED = true;
			}
			else {				
				if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherHasCustomContentToLeft)) {
					callMethod(Common.WORKSPACE_INSTANCE, Methods.wSnapToPage, 1);
				}
				else {
					callMethod(Common.WORKSPACE_INSTANCE, Methods.wSnapToPage, 0);
				}
			}
		}
		else if (overscroll < -50.0) {
			if (PreferencesHelper.continuousScrollWithAppDrawer) {
				//callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherShowAllApps, true, Common.CONTENT_TYPE, !PreferencesHelper.appdrawerRememberLastPosition);
				callMethod(Common.LAUNCHER_INSTANCE, "onClickAllAppsButton", new View(Common.LAUNCHER_CONTEXT));
				
				int lastPage = (Integer) callMethod(Common.APP_DRAWER_INSTANCE, "getChildCount") - 1;
				callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetCurrentPage, lastPage);
				Common.OVERSCROLLED = true;
			}
			else {
				int lastPage = (Integer) callMethod(Common.WORKSPACE_INSTANCE, "getChildCount") - 1;
				callMethod(Common.WORKSPACE_INSTANCE, Methods.wSnapToPage, lastPage);
			}
		}
	}
}