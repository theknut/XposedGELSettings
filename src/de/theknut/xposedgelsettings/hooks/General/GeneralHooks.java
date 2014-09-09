package de.theknut.xposedgelsettings.hooks.general;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.hooks.homescreen.WorkspaceConstructorHook;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class GeneralHooks extends HooksBaseClass {

    public static void initAllHooks(final LoadPackageParam lpparam) {

        findAndHookMethod(Classes.Launcher, "onCreate", Bundle.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                // save the launcher instance and the context
                Common.LAUNCHER_INSTANCE = (Activity) param.thisObject;
                Common.LAUNCHER_CONTEXT = (Context) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetApplicationContext);

                IntentFilter filter = new IntentFilter();
                filter.addAction(Common.XGELS_ACTION_RELOAD_SETTINGS);
                filter.addAction(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS);
                filter.addAction(Common.XGELS_ACTION_MODIFY_TAB);
                Common.LAUNCHER_CONTEXT.registerReceiver(broadcastReceiver, filter);
            }
        });

        try {
            if (PreferencesHelper.enableLLauncher) {
                findAndHookMethod(Classes.Utilities, Methods.uIsL, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // somehow methodreplacement didn't work
                        // let's do it like this...
                        param.setResult(Common.L_VALUE);
                    }
                });
                findAndHookMethod(Classes.Launcher, "onAttachedToWindow", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Common.L_VALUE = false;
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Common.L_VALUE = true;
                    }
                });
            }
        } catch (ClassNotFoundError cnfe) {

        } catch (NoSuchMethodError nsme) {

        } catch (Exception e) {

        }

        // save the workspace instance
        XposedBridge.hookAllConstructors(Classes.Workspace, new WorkspaceConstructorHook());

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
                findAndHookMethod(Classes.StartSettingsOnClick, "onClick", View.class, overriderSettingsHook);
            } else {
                findAndHookMethod(Classes.Launcher, "startSettings", overriderSettingsHook);
            }
        }

        if (PreferencesHelper.enableRotation) {
            // enable rotation
            XposedBridge.hookAllMethods(Classes.Launcher, Methods.lIsRotationEnabled, new IsRotationEnabledHook());
        }

        if (PreferencesHelper.resizeAllWidgets) {
            // manipulate the widget settings to make them resizeable
            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.CellLayout, Methods.clAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams,  boolean.class, new AddViewToCellLayoutHook());
            } else {
                XposedBridge.hookAllMethods(Classes.CellLayout, Methods.clAddViewToCellLayout, new AddViewToCellLayoutHook());
            }
        }

        if (PreferencesHelper.longpressAllAppsButton) {
            // add long press listener to app drawer button
            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.CellLayout, Methods.clAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new AllAppsButtonHook());
            } else {
                XposedBridge.hookAllMethods(Classes.CellLayout, Methods.clAddViewToCellLayout, new AllAppsButtonHook());
            }
        }

        if (PreferencesHelper.disableWallpaperScroll) {
            // don't scroll the wallpaper
            XposedBridge.hookAllMethods(Classes.WallpaperOffsetInterpolator, Methods.woiSyncWithScroll, new SyncWithScrollHook());
        }

        // prevent dragging

        XC_MethodHook drag = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesHelper.lockHomescreen) {
                    if (DEBUG) log(param, "Don't allow dragging");

                    Context context = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                    Toast.makeText(Common.LAUNCHER_CONTEXT, context.getString(R.string.toast_desktop_locked), Toast.LENGTH_LONG).show();
                    param.setResult(true);
                }
            }
        };

        findAndHookMethod(Classes.Folder, "onLongClick", View.class, drag);
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvBeginDragging, View.class, drag);
        findAndHookMethod(Classes.Workspace, Methods.wStartDrag, Classes.CellLayoutCellInfo, drag);

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
                    if (param.args[3] != null && param.args[3].getClass().equals(Classes.LauncherAppWidgetHostView)){
                        param.setResult(true);
                    }
                }
            });

            XC_MethodHook checkItemPlacementHook = new XC_MethodHook() {

                final int ITEMINFO = 1;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[ITEMINFO].getClass().equals(Classes.LauncherAppWidgetInfo) || param.args[ITEMINFO].getClass().equals(Classes.BubbleTextView)) {
                        param.setResult(true);
                    }
                }
            };

            findAndHookMethod(Classes.CellLayout, Methods.clAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View view = (View) param.args[0];
                    if (view.getTag() != null && PreferencesHelper.layerPositions.contains("" + getLongField(view.getTag(), Fields.iiID))) {
                        log("Front " + view.getTag());
                        view.bringToFront();
                    }
                }
            });

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

        if (PreferencesHelper.hideWorkspaceShadow) {

            findAndHookMethod(Classes.Launcher, Methods.lSetWorkspaceBackground, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
        }

        findAndHookMethod(Classes.BubbleTextView, Methods.btvCreateGlowingOutline, Canvas.class, Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[1] = param.args[2] = PreferencesHelper.glowColor;
            }
        });

        // hiding widgets
        if (Common.PACKAGE_OBFUSCATED) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, ArrayList.class, new OnPackagesUpdatedHook());
        } else {
            XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, new OnPackagesUpdatedHook());
        }

        findAndHookMethod(Classes.LauncherModel, Methods.lmDeleteItemFromDatabase, Context.class, Classes.ItemInfo, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                Iterator it = PreferencesHelper.shortcutIcons.iterator();
                while (it.hasNext()) {
                    String[] name = it.next().toString().split("\\|");
                    if (name[0].equals("" + getLongField(param.args[1], Fields.iiID))) {
                        it.remove();
                        Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "shortcuticons", PreferencesHelper.shortcutIcons);
                        return;
                    }
                }
            }
        });

        findAndHookMethod(Classes.LauncherModel, Methods.lmDeleteFolderContentsFromDatabase, Context.class, Classes.FolderInfo, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                ArrayList folderContents = (ArrayList) getObjectField(param.args[1], Fields.fiContents);
                boolean dirty = false;

                for (int i = 0; i < folderContents.size(); i++) {
                    Iterator it = PreferencesHelper.shortcutIcons.iterator();
                    while (it.hasNext()) {
                        String[] name = it.next().toString().split("\\|");
                        if (name[0].equals("" + getLongField(folderContents.get(i), Fields.iiID))) {
                            it.remove();
                            dirty = true;
                            break;
                        }
                    }
                }

                if (dirty) {
                    Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "shortcuticons", PreferencesHelper.shortcutIcons);
                }
            }
        });
    }

    static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            PreferencesHelper.init();
            if (DEBUG) log("Launcher: Settings reloaded");

            if (intent.getAction().equals(Common.XGELS_ACTION_RESTART_LAUNCHER)) {
                killLauncher();
            } else if (intent.getAction().equals(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS)) {
                long folderID = intent.getLongExtra("itemid", -1);
                View view = Common.CURRENT_FOLDER;

                if (view.getClass() == Classes.FolderIcon) {
                    if (getLongField(view.getTag(), Fields.iiID) != folderID) return;

                    Object mFolder = getObjectField(view, Fields.fiFolder);
                    for (String newItem : intent.getStringArrayListExtra("additems")) {

                        Object shortcutInfo = Utils.createShortcutInfo(newItem);
                        callMethod(getObjectField(mFolder, Fields.fFolderInfo), Methods.fiAdd, shortcutInfo);
                    }

                    ArrayList<View> folderItems = new ArrayList<View>((ArrayList<View>) callMethod(mFolder, Methods.fGetItemsInReadingOrder));
                    for (String removeItem : intent.getStringArrayListExtra("removeitems")) {
                        ComponentName currCmp = ComponentName.unflattenFromString(removeItem);

                        Iterator<View> it = folderItems.iterator();
                        while (it.hasNext()) {
                            View item = it.next();
                            if (currCmp.equals(((Intent) callMethod(item.getTag(), Methods.siGetIntent)).getComponent())) {
                                callMethod(getObjectField(mFolder, Fields.fFolderInfo), Methods.fiRemove, item.getTag());
                            }
                        }
                    }
                }

                if (DEBUG) log("Launcher: Updated folder items");
            } else if (intent.getAction().equals(Common.XGELS_ACTION_MODIFY_TAB)) {
                if (intent.getBooleanExtra("add", false)) {
                    TabHelper.getInstance().addTab(new Tab(intent, true));
                } else if (intent.getBooleanExtra("remove", false)) {
                    TabHelper.getInstance().removeTab(TabHelper.getInstance().getCurrentTabData());
                }else if (intent.hasExtra("color")) {
                    TabHelper.getInstance().setTabColor(intent.getIntExtra("color", Color.WHITE));
                    TabHelper.getInstance().saveTabData();
                } else {
                    TabHelper tabHelper = TabHelper.getInstance();
                    Object mAppsCustomizePane = getObjectField(tabHelper.getTabHost(), Fields.acthAppsCustomizePane);
                    Tab tab = tabHelper.getCurrentTabData();

                    if (tab.isAppsTab()) {
                        ArrayList allApps = (ArrayList) getObjectField(mAppsCustomizePane, Fields.acpvAllApps);
                        for (String app : intent.getStringArrayListExtra("additems")) {
                            allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(app)));
                        }
                        callMethod(mAppsCustomizePane, Methods.acpvSetApps, allApps);
                        tabHelper.invalidate();
                    } else {
                        tab.initData();
                    }
                }

                if (DEBUG) log("Launcher: Tab reloaded");
            }
        }
    };

    static void killLauncher() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 5000);
    }
}