package de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders;

import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class AddTabsAndFolders extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (Common.IS_KK_TREBUCHET || Common.IS_PRE_GNL_4) return;

        PreferencesHelper.moveTabHostBottom = false;
        findAndHookMethod(Classes.AppsCustomizeTabHost, "onFinishInflate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                TabHelperNew.getInstance().init((FrameLayout) param.thisObject);
                FolderHelper.getInstance().init();
            }
        });


        findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthSetInsets, Rect.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesHelper.enableAppDrawerTabs) {
                    ((FrameLayout.LayoutParams) ((View) getObjectField(param.thisObject, Fields.acthContent)).getLayoutParams()).topMargin = Utils.dpToPx(0);
                }
            }
        });

        XC_MethodHook syncAppsPageItemsHook = new XC_MethodHook() {
            final int PAGE = 0;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (TabHelperNew.getInstance().loadTabPage(param.thisObject, (Integer) param.args[PAGE])) {
                    param.setResult(null);
                }
            }
        };

        if (Common.IS_L_TREBUCHET
                || (Common.PACKAGE_OBFUSCATED && Common.GNL_PACKAGE_INFO.versionCode >= ObfuscationHelper.GNL_4_2_16)) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncAppsPageItems, Integer.TYPE, boolean.class, syncAppsPageItemsHook);
        } else {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncAppsPageItems, Integer.TYPE, syncAppsPageItemsHook);
        }

        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSyncPages, new XC_MethodHook() {

            int numAppPages;
            int contentHeight = -1;
            int orientation;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                if (PreferencesHelper.enableAppDrawerTabs && PreferencesHelper.moveTabHostBottom) {
//                    int currOrientation = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
//                    if (contentHeight == -1 || orientation != currOrientation) {
//                        orientation = currOrientation;
//                        contentHeight = getIntField(param.thisObject, ObfuscationHelper.Fields.acpvContentHeight);
//                    }
//                    setIntField(param.thisObject, ObfuscationHelper.Fields.acpvContentHeight, contentHeight - Utils.dpToPx(52));
//                }
                numAppPages = TabHelperNew.getInstance().setNumberOfPages(param.thisObject);
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
                    param.setResult(callMethod(Common.APP_DRAWER_INSTANCE, Methods.pvGetPageAt, 0));
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

        CommonHooks.AppsCustomizePagedViewOverScrollListeners.add(new XGELSCallback() {
            @Override
            public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lIsAllAppsVisible)) {
                    TabHelperNew.getInstance().handleOverscroll(getIntField(param.thisObject, Fields.pvOverscrollX));
                }
            }
        });

        findAndHookMethod(Classes.Launcher, Methods.lDispatchOnLauncherTransitionStart, View.class, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) param.args[2]) {
                    TabHelperNew.getInstance().hideTabBar();
                } else {
                    TabHelperNew.getInstance().hideTabBar();
                }
            }
        });

        findAndHookMethod(Classes.Launcher, Methods.lDispatchOnLauncherTransitionEnd, View.class, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (!(Boolean) param.args[2]) {
                    TabHelperNew.getInstance().showTabBar();
                }
            }
        });

        CommonHooks.GetCenterDeltaInScreenSpaceListener.add(new XGELSCallback() {
            @Override
            public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                if (getObjectField(Common.APP_DRAWER_INSTANCE, Fields.acpvContentType).toString().equals("Widgets")) {
                    return;
                }

                int color = PreferencesHelper.appdrawerFolderStyleBackgroundColor;
                if (PreferencesHelper.enableAppDrawerTabs) {
                    color = TabHelperNew.getInstance().getCurrentTabData().getPrimaryColor();
                }
                ((View) param.args[0]).getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });

        if (PreferencesHelper.enableAppDrawerTabs && PreferencesHelper.appdrawerSwipeTabs
                && (!PreferencesHelper.continuousScroll || !PreferencesHelper.continuousScrollWithAppDrawer)) {
            // open app drawer on overscroll of last page
            CommonHooks.AppsCustomizePagedViewOverScrollListeners.add(new XGELSCallback() {
                final int OVERSCROLL = 0;
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!Common.APP_DRAWER_PAGE_SWITCHED
                            && TabHelper.getInstance().handleScroll((Float) param.args[OVERSCROLL])) {
                        Common.APP_DRAWER_PAGE_SWITCHED = true;
                    }
                }
            });
        }
    }
}