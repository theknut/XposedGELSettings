package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.Common;
import de.theknut.xposedgelsettings.hooks.GELSettings;

public final class OnDragEnd extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		setBooleanField(param.thisObject, "mIsSearchBarHidden", true);
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {		
		View qsb = (View) getObjectField(param.thisObject, "mQSBSearchBar");
		qsb.setAlpha(1f);
		
		GELSettings.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);
	}
}