package de.theknut.xposedgelsettings.hooks.Homescreen;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public class OnWorkspaceShownHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Launcher.java#3074
	// void onWorkspaceShown(boolean animated)
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		Common.OVERSCROLLED = false;
	}
}
