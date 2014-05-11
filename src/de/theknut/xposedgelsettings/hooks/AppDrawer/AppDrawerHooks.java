package de.theknut.xposedgelsettings.hooks.appdrawer;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class AppDrawerHooks extends HooksBaseClass {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		// save an instance of the app drawer object
		XposedBridge.hookAllConstructors(Classes.AppsCustomizePagedView, new AppsCustomizePagedViewConstructorHook());
			
		if (PreferencesHelper.iconSettingsSwitchApps) {
			// changing the appearence of the icons in the app drawer
			
			XposedBridge.hookAllMethods(Classes.PagedViewIcon, Methods.applyFromApplicationInfo, new ApplyFromApplicationInfoHook());
			
//			if (!PreferencesHelper.appdrawerIconLabelShadow) {
//				XposedBridge.hookAllMethods(PagedViewIcon, "draw", new DrawHook());
//			}
		}
		
		if (PreferencesHelper.changeGridSizeApps) {			
			// modify app drawer grid
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.DeviceProfile, Methods.dpUpdateFromConfiguration, float.class, Integer.TYPE, Resources.class, DisplayMetrics.class, new UpdateFromConfigurationHook());
			} else {
				XposedBridge.hookAllMethods(Classes.DeviceProfile, Methods.dpUpdateFromConfiguration, new UpdateFromConfigurationHook());
			}
		}
		
		if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
			// set the background color of the app drawer
			XposedBridge.hookAllConstructors(Classes.AppsCustomizeLayout, new AppsCustomizeLayoutConstructorHook());
		}
		else {
			// set the background color of the app drawer
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthOnTabChanged, Classes.AppsCustomizeContentType, new OnTabChangedHook());
			} else {
				findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthOnTabChanged, String.class, new OnTabChangedHook());
			}
		}
		
		if (PreferencesHelper.continuousScroll) {
			// open app drawer on overscroll of last page
			findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvOverScroll, float.class, new OverScrollAppDrawerHook());
		}
		
		if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
			findAndHookMethod(Classes.AppsCustomizePagedView, "onClick", View.class, new OnClickHook());
		}
		
		if (PreferencesHelper.appdrawerRememberLastPosition) {
			
			findAndHookMethod(Classes.Workspace, Methods.wOnLauncherTransitionEnd, Classes.Launcher, boolean.class, boolean.class, new XC_MethodHook() {
				
				int TOWORKSPACE = 2;
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					
					if ((Boolean) param.args[TOWORKSPACE]) {
						Object acpv = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);
						Common.APPDRAWER_LAST_POSITION = getIntField(acpv, Fields.acpvCurrentPage);
						if (DEBUG) log(param, "AppDrawerHooks: get current position - " + Common.APPDRAWER_LAST_POSITION);
					}
				}
			});
			
			findAndHookMethod(Classes.Workspace, Methods.wOnTransitionPrepare, new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					//if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) return;
					Object acpv = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);
					
					int lastPage = (Integer) callMethod(acpv, "getChildCount") - 1;
					if (Common.APPDRAWER_LAST_POSITION > lastPage) {
						Common.APPDRAWER_LAST_POSITION = lastPage;
					}
					
					if (DEBUG) log(param, "AppDrawerHooks: set to last position " + Common.APPDRAWER_LAST_POSITION);
					callMethod(acpv, Methods.acpvSetCurrentPage, Common.APPDRAWER_LAST_POSITION);
				}
			});
		}
		
		// hiding apps from the app drawer
		findAndHookMethod(Classes.AllAppsList, Methods.aalAdd, Classes.AppInfo, new AllAppsListAddHook());
		
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