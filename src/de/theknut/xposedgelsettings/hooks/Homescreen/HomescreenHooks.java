package de.theknut.xposedgelsettings.hooks.homescreen;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.general.MoveToDefaultScreenHook;
import de.theknut.xposedgelsettings.hooks.systemui.SystemUIHooks;

public class HomescreenHooks {

	public static void initAllHooks(LoadPackageParam lpparam) {
		
		// save the workspace instance
		final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
		XposedBridge.hookAllConstructors(WorkspaceClass, new WorkspaceConstructorHook());
		
		// change the default homescreen
		XposedBridge.hookAllMethods(WorkspaceClass, "moveToDefaultScreen", new MoveToDefaultScreenHook());
		
		// don't animate background to semitransparent
		// XposedBridge.hookAllMethods(WorkspaceClass, "animateBackgroundGradient", new AnimateBackgroundGradientHook());
		
		final Class<?> DeviceProfileClass = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);		
		// modify homescreen grid
		XposedBridge.hookAllConstructors(DeviceProfileClass, new DeviceProfileConstructorHook());
		
		if (PreferencesHelper.iconSettingsSwitchHome) {			
			final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
			// changing the appearence of the icons on the homescreen
			XposedBridge.hookAllMethods(CellLayoutClass, "addViewToCellLayout", new AddViewToCellLayoutHook());
		}
		
		final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
		XposedBridge.hookAllConstructors(AppsCustomizePagedViewClass, new XC_MethodHook() {
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				// saving the content type
				Common.CONTENT_TYPE = getObjectField(param.thisObject, "mContentType");
			};
		});
		
		if (PreferencesHelper.continuousScroll) {
			
			// over scroll to app drawer or first page
			XposedBridge.hookAllMethods(WorkspaceClass, "overScroll", new OverScrollWorkspaceHook());
			
			final Class<?> LauncherClass = findClass(Common.LAUNCHER, lpparam.classLoader);
			XposedBridge.hookAllMethods(LauncherClass, "onWorkspaceShown", new OnWorkspaceShownHook());
		}
		
		if (PreferencesHelper.hideAppDock) {
			
			// hide the app dock
			final Class<?> dp = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);
			XposedBridge.hookAllMethods(dp, "getWorkspacePadding", new GetWorkspacePaddingHook());
		}
		
		SystemUIHooks.initAllHooks(lpparam);	
	}
}
