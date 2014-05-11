package de.theknut.xposedgelsettings.hooks.general;

import de.theknut.xposedgelsettings.hooks.HooksBaseClass;

public class IsRotationEnabledHook extends HooksBaseClass {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Launcher.java#4056
	// public boolean isRotationEnabled()
	
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		if (DEBUG) log(param, "Allow Rotation");
		
		param.setResult(true);
	}
}