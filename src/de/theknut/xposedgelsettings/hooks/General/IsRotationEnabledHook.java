package de.theknut.xposedgelsettings.hooks.General;

import de.robv.android.xposed.XC_MethodHook;

public class IsRotationEnabledHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Launcher.java#4056
	// public boolean isRotationEnabled()
	
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		param.setResult(true);
	}
}