package de.theknut.xposedgelsettings.hooks.general;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GeneralHooks extends HooksBaseClass {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		findAndHookMethod(Classes.Launcher, Methods.launcherOnCreate, Bundle.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				
				// save the launcher instance and the context
				Common.LAUNCHER_INSTANCE = param.thisObject;
				Common.LAUNCHER_CONTEXT = (Context) callMethod(Common.LAUNCHER_INSTANCE, Methods.launcherGetApplicationContext);
			}
		});
		
		if (PreferencesHelper.enableRotation) {
			// enable rotation
			XposedBridge.hookAllMethods(Classes.Launcher, Methods.launcherIsRotationEnabled, new IsRotationEnabledHook());			
		}
		
		if (PreferencesHelper.resizeAllWidgets) {
			// manipulate the widget settings to make them resizeable			
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams,  boolean.class, new AddViewToCellLayoutHook());
			} else {
				XposedBridge.hookAllMethods(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, new AddViewToCellLayoutHook());
			}
		}
		
		if (PreferencesHelper.longpressAllAppsButton) {
			// add long press listener to app drawer button
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams,  boolean.class, new AllAppsButtonHook());
			} else {
				XposedBridge.hookAllMethods(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, new AllAppsButtonHook());
			}
		}
		
		if (PreferencesHelper.disableWallpaperScroll) {
			// don't scroll the wallpaper
			XposedBridge.hookAllMethods(Classes.WallpaperOffsetInterpolator, Methods.wallpaperoffsetinterpolatorSyncWithScroll, new SyncWithScrollHook());
		}
		
		if (PreferencesHelper.lockHomescreen) {
			// prevent dragging
			
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.Workspace, Methods.workspaceStartDrag, Classes.CellLayoutCellInfo,new StartDragHook());			
				findAndHookMethod(Classes.Workspace, Methods.workspaceBeginDraggingApplication, View.class, new BeginDragHook());
				findAndHookMethod(Classes.Workspace, Methods.workspaceBeginDragShared, View.class, findClass("nn", lpparam.classLoader), new XC_MethodHook() {
					
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						param.setResult(null);
					}
				});
			} else {
				XposedBridge.hookAllMethods(Classes.Workspace, Methods.workspaceStartDrag, new StartDragHook());			
				XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, Methods.acpvBeginDraggingApplication, new BeginDragHook());
			}
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
		if (Common.PACKAGE_OBFUSCATED) {
			findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, ArrayList.class, new OnPackagesUpdatedHook());
		} else {
			XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, new OnPackagesUpdatedHook());
		}
	}
}