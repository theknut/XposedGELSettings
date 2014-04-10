package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import android.content.res.Configuration;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
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
			
//			if (!PreferencesHelper.appdrawerIconLabelShadow) {
//				XposedBridge.hookAllMethods(PagedViewIcon, "draw", new DrawHook());
//			}
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
		
		final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
		
		if (PreferencesHelper.continuousScroll) {
			// open app drawer on overscroll of last page
			XposedBridge.hookAllConstructors(AppsCustomizePagedViewClass, new AppsCustomizePagedViewConstructorHook());
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "overScroll", new OverScrollAppDrawerHook());
		}
		
		if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
			XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "onClick", new OnClickHook());
		}
		
		final Class<?> AllAppsListClass = findClass(Common.ALL_APPS_LIST, lpparam.classLoader);
		// hiding apps from the app drawer
		XposedBridge.hookAllMethods(AllAppsListClass, "add", new AllAppsListAddHook());
		
//		final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
//		XposedBridge.hookAllMethods(CellLayoutClass, "onLayout", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				//XposedBridge.log("XGELS onLayout ############### " + param.args[0]);
//				
//				ViewGroup grid = (ViewGroup) param.thisObject;
//				grid.setPadding(0, 0, 0, 0);
//			}
//		});
//		
//		final Class<?> APPS_CUSTOMIZE_PAGED_VIEW = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
//		XposedBridge.hookAllMethods(APPS_CUSTOMIZE_PAGED_VIEW, "onFinishInflate", new XC_MethodHook() {
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS onFinishInflate ############### ");
//				
//				if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//					ViewGroup grid = (ViewGroup) param.thisObject;
//					grid.setPadding(0, 0, 0, 0);
//				}
//			}
//		});
//				
//		XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "setupPage", new XC_MethodHook() {
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS setupPage ############### ");
//				
//				if (param.args[0].getClass().getName().contains("AppsCustomizeCellLayout")) {
//					Object layout = param.args[0];
//					XposedBridge.log("XGELS setupPage width " + getObjectField(layout, "mCellWidth"));
//					XposedBridge.log("XGELS setupPage height " + getObjectField(layout, "mCellHeight"));
//					
//					if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//						XposedBridge.log("XGELS setupPage2 ############### ");
//						callMethod(layout, "setCellDimensions", 232, 261);
//					}
//				}
//				
//				//XposedBridge.log("width" + param.args[0]);
//				//XposedBridge.log("height" + param.args[0]);
//			}
			
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//				if (!(Boolean) param.args[0]) return;
//				ViewGroup grid = (ViewGroup) param.thisObject;
//				int count = (Integer) callMethod(param.thisObject, "getChildCount");
//				for (int i = 0; i < count; i++) {
//				    View child = (View) callMethod(param.thisObject, "getChildAt", i);
//				    child.layout(0, grid.getPaddingTop(), 10, 10);
//				}
			//}
//		});
//		
//		XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "updatePageCounts", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS updatePageCounts");
//				
//				if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//					setIntField(param.thisObject, "mCellCountY", PreferencesHelper.xCountAllApps);
//					setIntField(param.thisObject, "mCellCountX", PreferencesHelper.yCountAllApps);
//				} else {
//					setIntField(param.thisObject, "mCellCountY", PreferencesHelper.yCountAllApps);
//					setIntField(param.thisObject, "mCellCountX", PreferencesHelper.xCountAllApps);
//				}
//			}
//		});
	}
}