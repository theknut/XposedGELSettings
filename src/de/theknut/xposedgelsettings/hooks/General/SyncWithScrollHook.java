package de.theknut.xposedgelsettings.hooks.general;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

public class SyncWithScrollHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#1233
	// public void syncWithScroll()
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		// don't sync with scroll
		param.setResult(null);
	}
}
