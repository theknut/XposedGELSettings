package de.theknut.xposedgelsettings.hooks.Icons;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import android.graphics.Color;
import de.robv.android.xposed.XC_MethodHook;

public final class PagedViewIconHook extends XC_MethodHook {
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		callMethod(param.thisObject, "setTextColor", Color.argb(0,0,0,0));
	}
}