package de.theknut.xposedgelsettings.hooks.pagindicator;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;

import static de.robv.android.xposed.XposedHelpers.setIntField;

public final class DeviceProfileConstructorHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#54
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		// set the page indicator height to 0
		setIntField(param.thisObject, Fields.dpPageIndicatorHeightPx, 0);
	}
}