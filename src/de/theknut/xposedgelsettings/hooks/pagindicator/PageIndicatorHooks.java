package de.theknut.xposedgelsettings.hooks.pagindicator;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class PageIndicatorHooks {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (PreferencesHelper.hidePageIndicator) {
			final Class<?> PagedViewClass = findClass(Common.PAGED_VIEW, lpparam.classLoader);
			
			// hides the page indicator
			XposedBridge.hookAllMethods(PagedViewClass, "onAttachedToWindow", new OnAttachedToWindowHook());
			
			// sets the height of the page indicator to 0
			final Class<?> DeviceProfileClass = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);
			XposedBridge.hookAllConstructors(DeviceProfileClass, new DeviceProfileConstructorHook());
			
			// reduce the bottom margin height in app drawer
			if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
				final Class<?> AppsCustomizeClass = findClass(Common.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
				XposedBridge.hookAllMethods(AppsCustomizeClass, "setInsets", new SetInsetsHook(true));
			}
			else {
				final Class<?> AppsCustomizeClass = findClass(Common.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
				XposedBridge.hookAllMethods(AppsCustomizeClass, "setInsets", new SetInsetsHook(false));
			}			
		}
	}
}