package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import de.robv.android.xposed.XC_MethodHook;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;

public final class DynamicGridLayoutHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#366
	// public void layout(Launcher launcher)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
		Common.DEVICE_PROFILE_INSTANCE = param.thisObject;	
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		int height = getIntField(Common.DEVICE_PROFILE_INSTANCE, Fields.dpSearchBarSpaceHeightPx);
		
		if (height != 0) {
			Common.SEARCH_BAR_SPACE_HEIGHT = getIntField(Common.DEVICE_PROFILE_INSTANCE, Fields.dpSearchBarSpaceHeightPx);
			Common.SEARCH_BAR_SPACE_WIDTH = getIntField(Common.DEVICE_PROFILE_INSTANCE, Fields.deviceProfileSearchBarSpaceWidthPx);
		}
		
		// claim back the search bar space
		setObjectField(Common.DEVICE_PROFILE_INSTANCE, Fields.dpSearchBarSpaceHeightPx, 0);
		GoogleSearchBarHooks.hideSearchbar();
	}
}