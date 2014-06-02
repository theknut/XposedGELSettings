package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class LauncherOnCreateHook extends HooksBaseClass {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
	    if (!PreferencesHelper.searchBarOnDefaultHomescreen) {
            // hide search bar
            GoogleSearchBarHooks.hideSearchbar();
        }
	}
}