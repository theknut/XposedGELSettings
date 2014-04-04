package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import de.robv.android.xposed.XC_MethodHook;

public class OnClickHook  extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#492
	// void onClick(View v)
	
	@Override
	protected void afterHookedMethod(final MethodHookParam param) throws Throwable
	{
		callMethod(getObjectField(param.thisObject, "mLauncher"), "showWorkspace", true);
	}
}