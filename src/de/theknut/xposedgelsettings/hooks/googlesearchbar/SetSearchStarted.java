package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class SetSearchStarted extends XC_MethodHook {
	
	int STARTED = 0;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		if (Common.LAUNCHER_INSTANCE == null) {
			return;
		}
		
		if ((Boolean) param.args[STARTED]) {
			// show the search if a text search is started
			GoogleSearchBarHooks.showSearchbar();
		} else {
			boolean hasGNowEnabled = (Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherHasCustomContentToLeft);
			
			if ((hasGNowEnabled && getIntField(Common.WORKSPACE_INSTANCE, Fields.workspaceCurrentPage) != 0)
				|| !hasGNowEnabled) {

                if (PreferencesHelper.searchBarOnDefaultHomescreen) {
                    GoogleSearchBarHooks.showSearchbar();
                } else {
                    // hide the search bar on stop search
                    GoogleSearchBarHooks.hideSearchbar();
                }
			}
		}
	}
}