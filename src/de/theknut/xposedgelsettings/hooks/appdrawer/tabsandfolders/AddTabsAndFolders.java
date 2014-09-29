package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.widget.TabHost;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class AddTabsAndFolders extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (Common.IS_TREBUCHET) return;

        findAndHookMethod(Classes.AppsCustomizeTabHost, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                TabHelper.getInstance().init((TabHost) param.thisObject);
                FolderHelper.getInstance().init();
            }
        });

        XC_MethodHook syncAppsPageItemsHook = new XC_MethodHook() {
            final int PAGE = 0;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (TabHelper.getInstance().loadTabPage(param.thisObject, (Integer) param.args[PAGE])) {
                    param.setResult(null);
                }
            }
        };

        if (Common.PACKAGE_OBFUSCATED) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncAppsPageItems, Integer.TYPE, syncAppsPageItemsHook);
        } else {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncAppsPageItems, Integer.TYPE, boolean.class, syncAppsPageItemsHook);
        }

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSetContentType, Classes.AppsCustomizeContentType, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                TabHelper.getInstance().setContentType(param.thisObject);
                param.setResult(null);
            }
        });

        findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthSetContentTypeImmediate, Classes.AppsCustomizeContentType, new XC_MethodHook() {
            final int CONTENTTYPE = 0;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (TabHelper.getInstance().setContentTypeImmediate(param.args[CONTENTTYPE])) {
                    param.setResult(null);
                }
            }
        });

        if (false && !PreferencesHelper.continuousScrollWithAppDrawer) {
            // open app drawer on overscroll of last page
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvOverScroll, float.class, new XC_MethodHook() {
                final int OVERSCROLL = 0;
                float overscroll;
                boolean overscrolled;
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    overscroll = (Float) param.args[OVERSCROLL];
                    log("Overscroll " + overscroll);

                    if (overscrolled && overscroll < 100.0) {
                        overscrolled = false;
                        log("Reset Overscrolled");
                    }

                    if (!overscrolled && TabHelper.getInstance().handleScroll(overscroll)) {
                        log("Overscrolled " + overscroll);
                        overscrolled = true;
                    }
                }
            });
        }

        XC_MethodHook resetHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Tab tab = TabHelper.getInstance().getCurrentTabData();
                if (tab != null && tab.isCustomTab()) {
                    param.setResult(null);
                }
            }
        };

        findAndHookMethod(Classes.AppsCustomizeTabHost, "reset", resetHook);
        findAndHookMethod(Classes.AppsCustomizePagedView, "reset", resetHook);

        XposedBridge.hookAllMethods(Classes.Launcher, "onNewIntent", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (TabHelper.getInstance().isTabSettingsOpen()) {
                    TabHelper.getInstance().closeTabSettings();
                }
            }
        });

        XposedBridge.hookAllMethods(Classes.Launcher, "onResume", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (TabHelper.getInstance().isTabSettingsOpen()
                        && !(Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) {
                    TabHelper.getInstance().closeTabSettings();
                }
            }
        });

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncPages, new XC_MethodHook() {

            int numAppPages;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                numAppPages = TabHelper.getInstance().setNumberOfPages(param.thisObject);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (numAppPages != -1) {
                    setIntField(param.thisObject, Fields.acpvNumAppsPages, numAppPages);
                }
            }
        });

        XC_MethodHook closeHook = new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        folder.closeFolder();
                        param.setResult(null);
                    }
                }
            }
        };

        XposedBridge.hookAllMethods(Classes.Launcher, "onNewIntent", closeHook);
        XposedBridge.hookAllMethods(Classes.Launcher, "onBackPressed", closeHook);

        findAndHookMethod(Classes.Workspace, Methods.wGetScreenWithId, long.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Long) param.args[0] == -1) {
                    param.setResult(callMethod(getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView), Methods.pvGetPageAt, 0));
                }
            }
        });

        findAndHookMethod(Classes.Workspace, Methods.wGetViewForTag, Object.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(getLongField(param.args[0], Fields.iiScreenId) == Folder.FOLDER_ID) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        param.setResult(folder.getFolderIcon());
                    }
                }
            }
        });

        findAndHookMethod(Classes.Workspace, Methods.wGetFolderForTag, Object.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if(getLongField(param.args[0], Fields.iiScreenId) == Folder.FOLDER_ID) {
                    Folder folder = FolderHelper.getInstance().findOpenFolder();
                    if (folder != null) {
                        param.setResult(getObjectField(folder.getFolderIcon(), Fields.fiFolder));
                    }
                }
            }
        });
    }
}