package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import android.content.Intent;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class FinishBindingItemsHook extends HooksBaseClass {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		if (PreferencesHelper.defaultHomescreen == -1) {
			boolean gnow = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherHasCustomContentToLeft);
			
			if (gnow) {
				PreferencesHelper.defaultHomescreen = 2;
			} else {
				PreferencesHelper.defaultHomescreen = 1;
			}
			
			if (DEBUG) log(param, "Setting default homescreen = " + PreferencesHelper.defaultHomescreen);
		}
		
		if (!Common.MOVED_TO_DEFAULTHOMESCREEN) {
			// move to default screen
			callMethod(Common.WORKSPACE_INSTANCE, Methods.workspaceMoveToDefaultScreen, true);
			Common.MOVED_TO_DEFAULTHOMESCREEN = true;
		} else {
			int currPage = getIntField(Common.WORKSPACE_INSTANCE, Fields.workspaceCurrentPage);
			
			Intent myIntent = new Intent();
			myIntent.setAction(Common.XGELS_INTENT);
			myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_NAVBAR);
			
			if (currPage == (PreferencesHelper.defaultHomescreen - 1)) {
				
				myIntent.putExtra(Common.XGELS_ACTION, "ON_DEFAULT_HOMESCREEN");
				Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
			} else {						
				if (PreferencesHelper.dynamicBackButtonOnEveryScreen) {
					myIntent.putExtra(Common.XGELS_ACTION, "BACK_POWER_OFF");
					Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
				}						
			}
		}
	}
}