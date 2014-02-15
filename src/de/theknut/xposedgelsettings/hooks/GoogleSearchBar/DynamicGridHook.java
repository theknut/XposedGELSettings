package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.theknut.xposedgelsettings.Common;
import de.theknut.xposedgelsettings.hooks.GELSettings;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public final class DynamicGridHook extends XC_MethodHook {
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		Common.DEVICE_PROFILE_INSTANCE = param.thisObject;
		
		setObjectField(Common.DEVICE_PROFILE_INSTANCE, "searchBarSpaceHeightPx", 0);
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {	
		Common.LAUNCHER_INSTANCE = param.args[0];
		GELSettings.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);
		
		Common.SEARCH_BAR_SPACE_HEIGHT = getIntField(Common.DEVICE_PROFILE_INSTANCE, "searchBarHeightPx") + 12;
		Common.SEARCH_BAR_SPACE_WIDTH = getIntField(Common.DEVICE_PROFILE_INSTANCE, "searchBarSpaceWidthPx");		
	}
}