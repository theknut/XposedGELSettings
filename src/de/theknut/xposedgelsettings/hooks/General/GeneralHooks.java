package de.theknut.xposedgelsettings.hooks.general;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Folder;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.FolderHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelperLegacy;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;
import de.theknut.xposedgelsettings.hooks.homescreen.WorkspaceConstructorHook;
import de.theknut.xposedgelsettings.hooks.icon.IconHooks;
import de.theknut.xposedgelsettings.ui.FragmentSelectiveIcon;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

public class GeneralHooks extends HooksBaseClass {

    static Unhook unhook;

    public static class Stat {
        String pkg = null;
        int count;

        public Stat(String intent, int count) {
            this.count = count;
            String[] split = intent.split(";");
            for (int i = split.length - 1; i >= 0; i--) {
                if (split[i].contains("component") || split[i].contains("package")) {
                    this.pkg = split[i];
                }
            }
            if (pkg == null) {
                this.pkg = "Unknown";
            }
        }
    }

    public static void initAllHooks(final LoadPackageParam lpparam) {
        //final ArrayList<Stat> stats = new ArrayList<Stat>();
        findAndHookMethod(Classes.Launcher, "onCreate", Bundle.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                // save the launcher instance and the context
                Common.LAUNCHER_INSTANCE = (Activity) param.thisObject;
                Common.LAUNCHER_CONTEXT = (Context) callMethod(Common.LAUNCHER_INSTANCE, "getApplicationContext");

                IntentFilter filter = new IntentFilter();
                filter.addAction(Common.XGELS_ACTION_RELOAD_SETTINGS);
                filter.addAction(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS);
                filter.addAction(Common.XGELS_ACTION_MODIFY_TAB);
                filter.addAction(Common.XGELS_ACTION_MODIFY_FOLDER);
                filter.addAction(Common.XGELS_ACTION_UPDATE_ICON);
                Common.LAUNCHER_CONTEXT.registerReceiver(broadcastReceiver, filter);

                try {
                    Common.XGELSCONTEXT = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
                } catch (Exception e) { }
            }

//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Object mStats = getObjectField(param.thisObject, "IS");
//                ArrayList<String> mIntents = (ArrayList<String>) getObjectField(mStats, "QG");
//                ArrayList<Integer> mHistogram = (ArrayList<Integer>) getObjectField(mStats, "QH");
//
//                for (int i = 0; i < mIntents.size(); i++) {
//                    stats.add(new Stat(mIntents.get(i), mHistogram.get(i)));
//                }
//
//                Collections.sort(stats, new Comparator<Stat>() {
//                    @Override
//                    public int compare(Stat lhs, Stat rhs) {
//                        return rhs.count-lhs.count;
//                    }
//                });
//
//                for (Stat stat : stats) {
//                    log(stat.pkg + " " + stat.count);
//                }
//            }
        });

        unhook = findAndHookMethod(Classes.DynamicGrid, Methods.dgGetDeviceProfile, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Common.DEVICE_PROFIL = param.getResult();
                unhook.unhook();
            }
        });

        try {
            if (PreferencesHelper.enableLLauncher && Common.IS_PRE_GNL_4) {
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
                if (!Common.IS_TREBUCHET) {
                    findAndHookMethod(Classes.Launcher, "startSettings", overriderSettingsHook);
                }
            }
        }

        if (PreferencesHelper.resizeAllWidgets) {
            // manipulate the widget settings to make them resizeable
            CommonHooks.AddViewToCellLayoutListeners.add(new AddViewToCellLayoutHook());
        }

        if (PreferencesHelper.longpressAllAppsButton) {
            // add long press listener to app drawer button
            if (Common.PACKAGE_OBFUSCATED) {
                CommonHooks.AddViewToCellLayoutListeners.add(new AllAppsButtonHook());
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
                if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_1_21) {
                    findAndHookMethod(Classes.LoaderTask, Methods.lmCheckItemPlacement, HashMap.class, Classes.ItemInfo, checkItemPlacementHook);
                } else {
                    findAndHookMethod(Classes.LoaderTask, Methods.lmCheckItemPlacement, HashMap.class, Classes.ItemInfo, AtomicBoolean.class, checkItemPlacementHook);
                }
            } else
                XposedBridge.hookAllMethods(Classes.LoaderTask, Methods.lmCheckItemPlacement, checkItemPlacementHook);
        }

        if (PreferencesHelper.scrolldevider != 10) {
            XGELSCallback snapToPageHook = new XGELSCallback() {

                final int SPEED = 2;

                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[SPEED] = (int) Math.round((Integer) param.args[SPEED] / ((PreferencesHelper.scrolldevider == -1)
                            ? (Integer) param.args[SPEED]
                            : (PreferencesHelper.scrolldevider / 10)));
                }
            };

            CommonHooks.SnapToPageListeners.add(snapToPageHook);
        }

        if (PreferencesHelper.hideWorkspaceShadow) {

            findAndHookMethod(Classes.Launcher, Methods.lSetWorkspaceBackground, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
        }

        // hiding widgets
        if (Common.PACKAGE_OBFUSCATED) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, ArrayList.class, new OnPackagesUpdatedHook());
        } else {
            XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, Methods.acpvOnPackagesUpdated, new OnPackagesUpdatedHook());
        }

        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList items;
                if (param.args[1] instanceof ArrayList) {
                    items = ((ArrayList) param.args[1]);
                } else {
                    items = new ArrayList();
                    items.add(param.args[1]);
                }
                for (Object item : items) {
                    Iterator it = PreferencesHelper.shortcutIcons.iterator();
                    while (it.hasNext()) {
                        String[] name = it.next().toString().split("\\|");
                        if (name[0].equals("" + getLongField(item, Fields.iiID))) {
                            it.remove();
                            Utils.saveToSettings(Common.LAUNCHER_CONTEXT, "shortcuticons", PreferencesHelper.shortcutIcons);
                            return;
                        }
                    }
                }
            }
        };

        if (Common.IS_PRE_GNL_4) {
            findAndHookMethod(Classes.LauncherModel, Methods.lmDeleteItemFromDatabase, Context.class, Classes.ItemInfo, hook);
        } else {
            findAndHookMethod(Classes.LauncherModel, Methods.lmDeleteItemFromDatabase, Context.class, ArrayList.class, hook);
        }

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

        // onSetAlpha is blocking alpha animations and doesn't have any usage anyways
        XposedBridge.hookAllMethods(Classes.BubbleTextView, "onSetAlpha", XC_MethodReplacement.returnConstant(false));
    }

    static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            PreferencesHelper.init();
            PreferencesHelper.initDefaultHomescreen();

            if (DEBUG) log("Launcher: Settings reloaded");
            try {
                if (intent.getAction().equals(Common.XGELS_ACTION_RESTART_LAUNCHER)) {
                    killLauncher();
                } else if (intent.getAction().equals(Common.XGELS_ACTION_UPDATE_FOLDER_ITEMS)) {
                    long folderID = intent.getLongExtra("itemid", -1);
                    View view = Common.CURRENT_CONTEXT_MENU_ITEM;

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
                                if (currCmp.equals(((Intent) callMethod(item.getTag(), "getIntent")).getComponent())) {
                                    callMethod(getObjectField(mFolder, Fields.fFolderInfo), Methods.fiRemove, item.getTag());
                                }
                            }
                        }

                        Folder folder = FolderHelper.getInstance().getFolder(getLongField(view.getTag(), Fields.iiID));
                        if (folder != null) {
                            if (folder.hideFromAppsPage()) {
                                Object mAppsCustomizePane = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);
                                ArrayList allApps = (ArrayList) getObjectField(mAppsCustomizePane, Fields.acpvAllApps);
                                folder.invalidateRawData();
                                for (String app : folder.getRawData()) {
                                    allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(app)));
                                }

                                for (String removeItem : intent.getStringArrayListExtra("removeitems")) {
                                    allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(removeItem)));
                                }

                                callMethod(mAppsCustomizePane, Methods.acpvSetApps, allApps);
                            }
                        }
                    }

                    if (DEBUG) log("Launcher: Updated folder items");
                } else if (intent.getAction().equals(Common.XGELS_ACTION_MODIFY_TAB)) {
                    if (intent.getBooleanExtra("add", false)) {
                        if (Common.IS_PRE_GNL_4) {
                            TabHelperLegacy.getInstance().addTab(new Tab(intent, true));
                        } else {
                            new Tab(intent, true, true);
                        }
                    } else if (intent.getBooleanExtra("remove", false)) {
                        TabHelper.getInstance().removeTab(TabHelper.getInstance().getCurrentTabData());
                    } else if (intent.hasExtra("color")) {
                        TabHelper tabHelper = TabHelper.getInstance();
                        tabHelper.setTabColor(intent.getIntExtra("color", Color.WHITE));
                        tabHelper.saveTabData();
                        tabHelper.invalidate();
                    } else {
                        TabHelper tabHelper = TabHelper.getInstance();
                        Tab tab = tabHelper.getCurrentTabData();

                        if (tab.isAppsTab()) {
                            ArrayList allApps = (ArrayList) getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvAllApps);
                            for (String app : intent.getStringArrayListExtra("additems")) {
                                allApps.add(Utils.createAppInfo(ComponentName.unflattenFromString(app)));
                            }
                            callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetApps, allApps);
                            tabHelper.invalidate();
                        } else {
                            tab.initData();
                        }
                    }

                    if (DEBUG) log("Launcher: Tab reloaded");
                } else if (intent.getAction().equals(Common.XGELS_ACTION_MODIFY_FOLDER)) {
                    if (intent.getBooleanExtra("add", false)) {
                        FolderHelper.getInstance().addFolder(new Folder(intent, true));
                    } else if (intent.getBooleanExtra("setup", false)) {
                        FolderHelper.getInstance().setupFolderSettings(null, Tab.APPS_ID);
                    }

                    if (DEBUG) log("Launcher: Folder reloaded");
                } else if (intent.getAction().equals(Common.XGELS_ACTION_UPDATE_ICON)) {
                    final long itemId = intent.getLongExtra("itemid", -1);
                    final long mode = intent.getIntExtra("mode", -1);
                    final boolean isDefault = intent.getBooleanExtra("default", false);
                    final Drawable[] background = new Drawable[1];

                    new AsyncTask<Void, Void, Void>() {
                        View icon;
                        boolean isFolder;

                        @Override
                        protected Void doInBackground(Void... params) {

                            View view = Common.CURRENT_CONTEXT_MENU_ITEM;
                            if (view != null && itemId == getLongField(view.getTag(), Fields.iiID)) {
                                icon = view;
                                return null;
                            }

                            // fallback if we don't have any view saved but that shouldn't happen...
                            ArrayList cellLayouts = (ArrayList) callMethod(Common.WORKSPACE_INSTANCE, Methods.wGetWorkspaceAndHotseatCellLayouts);
                            for (Object layoutParent : cellLayouts) {
                                ViewGroup layout = (ViewGroup) callMethod(layoutParent, Methods.clGetShortcutsAndWidgets);
                                int childCount = layout.getChildCount();
                                for (int i = 0; i < childCount; ++i) {
                                    view = layout.getChildAt(i);
                                    Object tag = view.getTag();
                                    if (tag == null) continue;

                                    if (mode == FragmentSelectiveIcon.MODE_PICK_FOLDER_ICON
                                            && view.getClass().equals(Classes.FolderIcon)
                                            && getLongField(tag, Fields.iiID) == itemId) {
                                        icon = view;
                                        return null;
                                    } else if (mode == FragmentSelectiveIcon.MODE_PICK_SHORTCUT_ICON
                                            && view.getClass().equals(Classes.BubbleTextView)
                                            && getLongField(tag, Fields.iiID) == itemId) {

                                        if (isDefault) {
                                            int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("portal_ring_inner_holo", "drawable", Common.HOOKED_PACKAGE);
                                            if (id != 0) {
                                                Bitmap bitmap = ((BitmapDrawable) Common.LAUNCHER_CONTEXT.getResources().getDrawable(id)).getBitmap();
                                                background[0] = new BitmapDrawable(
                                                        Common.LAUNCHER_CONTEXT.getResources(),
                                                        Bitmap.createScaledBitmap(
                                                                bitmap,
                                                                getIntField(Common.DEVICE_PROFIL, Fields.dpFolderIconSize),
                                                                getIntField(Common.DEVICE_PROFIL, Fields.dpFolderIconSize),
                                                                true
                                                        )
                                                );
                                            }
                                            return null;
                                        }

                                        if (Common.PACKAGE_OBFUSCATED) {
                                            icon = (View) callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppInfo, callMethod(tag, "getIntent"));
                                        } else {
                                            PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
                                            icon = (View) newInstance(
                                                    Classes.AppInfo,
                                                    pm,
                                                    pm.resolveActivity((Intent) callMethod(tag, "getIntent"), 0),
                                                    getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache),
                                                    new HashMap<Object, CharSequence>()
                                            );
                                        }

                                        return null;
                                    }
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (mode == FragmentSelectiveIcon.MODE_PICK_SHORTCUT_ICON) {
                                isFolder = icon.getParent().getParent().getClass().equals(Classes.Folder);
                                if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_9_00) {
                                    callMethod(icon, Methods.btvApplyFromShortcutInfo, icon.getTag(), getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache), !isFolder);
                                } else {
                                    callMethod(icon, Methods.btvApplyFromShortcutInfo, icon.getTag(), getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache));
                                }
                            } else if (mode == FragmentSelectiveIcon.MODE_PICK_FOLDER_ICON) {
                                if (isDefault) {
                                    ImageView prevBackground = (ImageView) getObjectField(icon, Fields.fiPreviewBackground);
                                    int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("portal_ring_inner_holo", "drawable", Common.HOOKED_PACKAGE);
                                    if (id != 0) {
                                        Bitmap bitmap = ((BitmapDrawable) Common.LAUNCHER_CONTEXT.getResources().getDrawable(id)).getBitmap();
                                        background[0] = new BitmapDrawable(
                                                Common.LAUNCHER_CONTEXT.getResources(),
                                                Bitmap.createScaledBitmap(
                                                        bitmap,
                                                        getIntField(Common.DEVICE_PROFIL, Fields.dpFolderIconSize),
                                                        getIntField(Common.DEVICE_PROFIL, Fields.dpFolderIconSize),
                                                        true
                                                )
                                        );
                                    }
                                    background[0].setColorFilter(Color.parseColor(ColorPickerPreference.convertToARGB(PreferencesHelper.homescreenFolderPreviewColor)), PorterDuff.Mode.MULTIPLY);
                                    prevBackground.setImageDrawable(background[0]);
                                } else {
                                    IconHooks.setFolderIcon(icon);
                                }
                                icon.postInvalidate();
                            }
                        }
                    }.execute();
                }
            } catch (Exception e) {}
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