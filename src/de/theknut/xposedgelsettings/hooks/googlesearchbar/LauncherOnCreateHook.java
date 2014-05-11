package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

public final class LauncherOnCreateHook extends HooksBaseClass {
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		// hide search bar
		GoogleSearchBarHooks.hideSearchbar();
	}
}