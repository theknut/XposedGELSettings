package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public final class DynamicGridLayoutHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/DynamicGrid.java#366
	// public void layout(Launcher launcher)
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		// save the instance
		Common.DEVICE_PROFILE_INSTANCE = param.thisObject;
		
		if (Common.SEARCH_BAR_SPACE_HEIGHT == -1) {
			// save height and with of the search bar
			Common.SEARCH_BAR_SPACE_HEIGHT = getIntField(Common.DEVICE_PROFILE_INSTANCE, Fields.dpSearchBarHeightPx) + 12; // + 2x padding
			Common.SEARCH_BAR_SPACE_WIDTH = getIntField(Common.DEVICE_PROFILE_INSTANCE, "searchBarSpaceWidthPx");
		}

		// claim back the search bar space
		setObjectField(Common.DEVICE_PROFILE_INSTANCE, Fields.dpSearchBarHeightPx, 0);
	}

	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
	    
	    if (!PreferencesHelper.searchBarOnDefaultHomescreen) {
    		// hide search bar
    		GoogleSearchBarHooks.hideSearchbar();
	    }
	}
}