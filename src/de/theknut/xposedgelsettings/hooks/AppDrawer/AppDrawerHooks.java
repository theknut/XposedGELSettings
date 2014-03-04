package de.theknut.xposedgelsettings.hooks.AppDrawer;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class AppDrawerHooks {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
			
		if (PreferencesHelper.iconSettingsSwitchApps) {
			// changing the appearence of the icons in the app drawer
			final Class<?> PagedViewIcon = findClass(Common.PAGED_VIEW_ICON, lpparam.classLoader);
			XposedBridge.hookAllMethods(PagedViewIcon, "applyFromApplicationInfo", new ApplyFromApplicationInfoHook());
		}
		
		if (PreferencesHelper.changeGridSizeApps) {
			final Class<?> DeviceProfileClass = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);
			
			// modify app drawer grid
			XposedBridge.hookAllMethods(DeviceProfileClass, "updateFromConfiguration", new UpdateFromConfigurationHook());
		}
		
		if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
			// set the background color of the app drawer
			final Class<?> AppsCustomizeLayoutClass = findClass(Common.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
			XposedBridge.hookAllConstructors(AppsCustomizeLayoutClass, new AppsCustomizeLayoutConstructorHook());
		}
		else {
			// set the background color of the app drawer
			final Class<?> AppsCustomizeTabHostClass = findClass(Common.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
			XposedBridge.hookAllMethods(AppsCustomizeTabHostClass, "onTabChangedEnd", new OnTabChangedHook());
		}
		
		if (PreferencesHelper.continuousScroll) {
			// open app drawer on overscroll of last page
			final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
			XposedBridge.hookAllConstructors(AppsCustomizePagedViewClass, new AppsCustomizePagedViewConstructorHook());
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "overScroll", new OverScrollAppDrawerHook());
		}
		
		if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
			final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "onClick", new OnClickHook());
		}
		
		// hiding apps from the app drawer
		final Class<?> AllAppsListClass = findClass(Common.ALL_APPS_LIST, lpparam.classLoader);
		XposedBridge.hookAllMethods(AllAppsListClass, "add", new AllAppsListAddHook());
	}
}