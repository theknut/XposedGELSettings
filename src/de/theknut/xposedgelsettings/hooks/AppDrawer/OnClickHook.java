package de.theknut.xposedgelsettings.hooks.appdrawer;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class OnClickHook  extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#492
	// void onClick(View v)
	
	@Override
	protected void afterHookedMethod(final MethodHookParam param) throws Throwable
	{
        callMethod(Common.LAUNCHER_INSTANCE, Methods.lAddOnResumeCallback, new Runnable() {
            @Override
            public void run() {
                callMethod(Common.LAUNCHER_INSTANCE, Methods.lShowWorkspace, false, null);
            }
        });
	}
}