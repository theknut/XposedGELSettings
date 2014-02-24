package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;

public final class DynamicGridLayoutHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#366
	// public void layout(Launcher launcher)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		// save the instance
		Common.DEVICE_PROFILE_INSTANCE = param.thisObject;
		
		// claim back the search bar space
		setObjectField(Common.DEVICE_PROFILE_INSTANCE, "searchBarSpaceHeightPx", 0);
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		// hide search bar
		GoogleSearchBarHooks.setLayoutParams(Common.LAUNCHER_INSTANCE, 0, 0, 0, 0);
		
		// save height and with of the search bar
		Common.SEARCH_BAR_SPACE_HEIGHT = getIntField(Common.DEVICE_PROFILE_INSTANCE, "searchBarHeightPx") + 12; // + 2x padding
		Common.SEARCH_BAR_SPACE_WIDTH = getIntField(Common.DEVICE_PROFILE_INSTANCE, "searchBarSpaceWidthPx");		
	}
}