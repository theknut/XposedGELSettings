package de.theknut.xposedgelsettings.hooks.general;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

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

        if (PreferencesHelper.overrideSettingsButton) {
            XC_MethodHook overriderSettingsHook = new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callMethod(Common.LAUNCHER_INSTANCE, "startActivity", startMain);

                    Intent LaunchIntent = Common.LAUNCHER_CONTEXT.getPackageManager().getLaunchIntentForPackage(Common.PACKAGE_NAME);
                    callMethod(Common.LAUNCHER_INSTANCE, "startActivity", LaunchIntent);
                    param.setResult(null);
                }
            };

            if (Common.PACKAGE_OBFUSCATED) {
                try {
                    findAndHookMethod(findClass("pu", lpparam.classLoader), "onClick", View.class, overriderSettingsHook);
                } catch (NoSuchMethodError nsme) {
                    findAndHookMethod(findClass("td", lpparam.classLoader), "onClick", View.class, overriderSettingsHook);
                }
            } else {
                findAndHookMethod(Classes.Launcher, "startSettings", overriderSettingsHook);
            }
        }

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
				findAndHookMethod(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new AllAppsButtonHook());
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

            XC_MethodHook drag = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (DEBUG) log(param, "Don't allow dragging");

                    Context context = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                    Toast.makeText(Common.LAUNCHER_CONTEXT, context.getString(R.string.toast_desktop_locked), Toast.LENGTH_LONG).show();
                    param.setResult(true);
                }
            };

            findAndHookMethod(Classes.Folder, "onLongClick", View.class, drag);
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvBeginDragging, View.class, drag);
            findAndHookMethod(Classes.Workspace, Methods.workspaceStartDrag, Classes.CellLayoutCellInfo, new StartDragHook());
		}

        if (PreferencesHelper.overlappingWidgets) {
            findAndHookMethod(Classes.CellLayout, Methods.clMarkCellsForView, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, boolean[][].class, boolean.class, new XC_MethodHook() {

                final int HSPAN = 2;
                final int VSPAN = 3;
                final int OCCUPIED = 5;

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if ((Integer) param.args[HSPAN] > 1 || (Integer) param.args[VSPAN] > 1)
                        param.args[OCCUPIED] = false;
                }
            });

            findAndHookMethod(Classes.CellLayout, Methods.clAttemptPushInDirection, ArrayList.class, Rect.class, int[].class, View.class, Classes.ItemConfiguration, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[3] != null
                        && param.args[3].getClass().getName().contains(Fields.lAppWidgetHostView)) {
                        param.setResult(true);
                    }
                }
            });

            findAndHookMethod(Classes.AppWidgetResizeFrame, Methods.awrfCommitResize, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View widget = ((View) getObjectField(param.thisObject, Fields.awrfWidgetView));
                    widget.bringToFront();
                }
            });

            XC_MethodHook checkItemPlacementHook = new XC_MethodHook() {

                final int ITEMINFO = 1;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[ITEMINFO].getClass().getName().contains(Fields.LauncherAppWidgetInfo))
                        param.setResult(true);
                }
            };

            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.LoaderTask, Methods.lmCheckItemPlacement, HashMap.class, Classes.ItemInfo, AtomicBoolean.class, checkItemPlacementHook);
            } else
                XposedBridge.hookAllMethods(Classes.LoaderTask, Methods.lmCheckItemPlacement, checkItemPlacementHook);
        }
        
        if (PreferencesHelper.scrolldevider != 10) {
            XC_MethodHook snapToPageHook = new XC_MethodHook() {
                
                final int SPEED = 2;
                
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[SPEED] = (int) Math.round((Integer) param.args[SPEED] / ((PreferencesHelper.scrolldevider == -1)
                            ? (Integer) param.args[SPEED]
                            : (PreferencesHelper.scrolldevider / 10)));
                }
            };
            
            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.PagedView, Methods.pvSnapToPage, Integer.TYPE, Integer.TYPE, Integer.TYPE, boolean.class, TimeInterpolator.class, snapToPageHook);
            } else {
                findAndHookMethod(Classes.PagedView, Methods.pvSnapToPage, Integer.TYPE, Integer.TYPE, Integer.TYPE, snapToPageHook);
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