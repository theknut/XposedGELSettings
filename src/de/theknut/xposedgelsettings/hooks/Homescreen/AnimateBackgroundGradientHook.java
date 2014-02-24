package de.theknut.xposedgelsettings.hooks.Homescreen;

import de.robv.android.xposed.XC_MethodHook;

public class AnimateBackgroundGradientHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/Workspace.java#1334
	// private void animateBackgroundGradient(float finalAlpha, boolean animated)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		// make it fully transparent
		param.args[0] = 0.0f;
		param.args[1] = false;
	}
}