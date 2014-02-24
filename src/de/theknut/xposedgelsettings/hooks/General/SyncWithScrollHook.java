package de.theknut.xposedgelsettings.hooks.General;

import de.robv.android.xposed.XC_MethodHook;

public class SyncWithScrollHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#1233
	// public void syncWithScroll()
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		// don't sync with scroll
		param.setResult(null);
	}
}
