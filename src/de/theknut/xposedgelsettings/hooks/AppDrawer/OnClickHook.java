package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;

import static de.robv.android.xposed.XposedHelpers.callMethod;

public class OnClickHook  extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#492
	// void onClick(View v)	
	
	@Override
	protected void beforeHookedMethod(final MethodHookParam param) throws Throwable
	{
		if (Common.PACKAGE_OBFUSCATED) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			callMethod(Common.LAUNCHER_INSTANCE, "startActivity", startMain);
		}
	}
	
	@Override
	protected void afterHookedMethod(final MethodHookParam param) throws Throwable
	{
		if (!Common.PACKAGE_OBFUSCATED) {
			callMethod(Common.LAUNCHER_INSTANCE, Methods.lShowWorkspace, true);
		}
	}
}