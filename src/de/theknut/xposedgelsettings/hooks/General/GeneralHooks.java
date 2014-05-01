package de.theknut.xposedgelsettings.hooks.general;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.Context;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GeneralHooks extends HooksBaseClass {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		final Class<?> LauncherClass = findClass(Common.LAUNCHER, lpparam.classLoader);
		XposedBridge.hookAllMethods(LauncherClass, "onCreate", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				// save the launcher instance and the context
				Common.LAUNCHER_INSTANCE = param.thisObject;
				Common.LAUNCHER_CONTEXT = (Context) callMethod(Common.LAUNCHER_INSTANCE, "getApplicationContext");
			}
		});
		
		if (PreferencesHelper.enableRotation) {
			// enable rotation
			XposedBridge.hookAllMethods(LauncherClass, "isRotationEnabled", new IsRotationEnabledHook());			
		}
		
		if (PreferencesHelper.resizeAllWidgets) {
			// manipulate the widget settings to make them resizeable
			final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
			XposedBridge.hookAllMethods(CellLayoutClass, "addViewToCellLayout", new AddViewToCellLayoutHook());
		}
		
		if (PreferencesHelper.longpressAllAppsButton) {
			// add long press listener to app drawer button
			final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
			XposedBridge.hookAllMethods(CellLayoutClass, "addViewToCellLayout", new AllAppsButtonHook());
		}
		
		if (PreferencesHelper.disableWallpaperScroll) {
			// don't scroll the wallpaper
			final Class<?> DynamicGridClass = findClass(Common.WORKSPACE + "$WallpaperOffsetInterpolator", lpparam.classLoader);
			XposedBridge.hookAllMethods(DynamicGridClass, "syncWithScroll", new SyncWithScrollHook());
		}
		
		if (PreferencesHelper.lockHomescreen) {
			final Class<?> WorkspaceClass = findClass(Common.WORKSPACE, lpparam.classLoader);
			XposedBridge.hookAllMethods(WorkspaceClass, "startDrag", new StartDragHook());
			final Class<?> AppCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
			XposedBridge.hookAllMethods(AppCustomizePagedViewClass, "beginDraggingApplication", new BeginnDragHook());
		}
		
//		final Class<?> PagedViewClass = findClass(Common.PAGED_VIEW, lpparam.classLoader);
//		XposedBridge.hookAllMethods(PagedViewClass, "snapToPage", new XC_MethodHook(XC_MethodHook.PRIORITY_HIGHEST) {
//			
//			int TOUCH_STATE_REST = 0;
//			
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				
//				if (getIntField(Common.WORKSPACE_INSTANCE, "mTouchState") != TOUCH_STATE_REST) {
//					if (DEBUG) log(param, "Block snapToPage");
//					param.setResult(null);
//				}
//			}
//		});
		
		// add long press listener to app drawer button
		//final Class<?> CellLayoutClass = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
		//XposedBridge.hookAllMethods(CellLayoutClass, "markCellsAsOccupiedForView", new MarkCellsAsOccupiedForViewHook());
		
		// hiding widgets
		final Class<?> AppsCustomizePagedViewClass = findClass(Common.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);		
		XposedBridge.hookAllMethods(AppsCustomizePagedViewClass, "onPackagesUpdated", new OnPackagesUpdatedHook());
	}
}