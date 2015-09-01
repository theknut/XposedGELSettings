package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AddTabsAndFolders;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AddTabsAndFoldersLegacy;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelperLegacy;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelperNew;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class AppDrawerHooks extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        // save an instance of the app drawer object
        XposedBridge.hookAllConstructors(Classes.AppsCustomizePagedView, new AppsCustomizePagedViewConstructorHook());

        if (PreferencesHelper.iconSettingsSwitchApps && Common.IS_PRE_GNL_4) {
            // changing the appearence of the icons in the app drawer
            XposedBridge.hookAllMethods(Classes.PagedViewIcon, Methods.pviApplyFromApplicationInfo, new ApplyFromApplicationInfoHook());
        }
        // modify app drawer grid
        if (Common.PACKAGE_OBFUSCATED) {
            findAndHookMethod(Classes.DeviceProfile, Methods.dpUpdateFromConfiguration, float.class, Integer.TYPE, Resources.class, DisplayMetrics.class, new UpdateFromConfigurationHook());
        } else {
            XposedBridge.hookAllMethods(Classes.DeviceProfile, Methods.dpUpdateFromConfiguration, new UpdateFromConfigurationHook());
        }

        if (Common.IS_KK_TREBUCHET) {
            // set the background pref_color of the app drawer
            XposedBridge.hookAllConstructors(Classes.AppsCustomizeLayout, new AppsCustomizeLayoutConstructorHook());
        }
        else {
            // set the background pref_color of the app drawer
            if (Common.PACKAGE_OBFUSCATED || Common.IS_L_TREBUCHET) {
                if (Common.IS_PRE_GNL_4) {
                    findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthOnTabChanged, Classes.AppsCustomizeContentType, new OnTabChangedHook());
                } else {
                    findAndHookMethod(Classes.AppsCustomizeTabHost, "onFinishInflate", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            ((View) param.thisObject).setBackgroundColor(PreferencesHelper.appdrawerBackgroundColor);
                        }
                    });

                    XC_MethodHook GetChangeStateAnimation = new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (Color.alpha(PreferencesHelper.appdrawerBackgroundColor) < 55
                                    && getObjectField(param.thisObject, ObfuscationHelper.Fields.wState).toString().equals("NORMAL_HIDDEN")) {
                                param.args[0] = 0f;
                            }
                        }
                    };

                    if (Common.IS_L_TREBUCHET) {
                        findAndHookMethod(Classes.Workspace, Methods.wGetChangeStateAnimation, Classes.WorkspaceState, boolean.class, ArrayList.class, GetChangeStateAnimation);
                    } else {
                        findAndHookMethod(Classes.Workspace, Methods.wGetChangeStateAnimation, float.class, boolean.class, GetChangeStateAnimation);
                    }
                }
            } else {
                findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthOnTabChanged, String.class, new OnTabChangedHook());
            }
        }

        if (Common.PACKAGE_OBFUSCATED && Common.GNL_VERSION < ObfuscationHelper.GNL_5_2_33) {
            // possible fix for folder not completely opening
            findAndHookMethod(Classes.Folder, "b", Classes.Folder, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    View folder = (View) param.args[0];
                    folder.setScaleX(1.0f);
                    folder.setScaleY(1.0f);
                    folder.setAlpha(1.0f);
                }
            });
        }

        if (PreferencesHelper.continuousScroll) {
            // open app drawer on overscroll of last page
            CommonHooks.AppsCustomizePagedViewOverScrollListeners.add(new OverScrollAppDrawerHook());
        }

        if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
            findAndHookMethod(Common.IS_PRE_GNL_4 ? Classes.AppsCustomizePagedView : Classes.Launcher, "onClick", View.class, new OnClickHook());
        }

        findAndHookMethod(Classes.Workspace, Methods.wOnTransitionPrepare, Classes.Launcher, boolean.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) param.args[2]) return;

                if (PreferencesHelper.enableAppDrawerTabs
                        && (Common.PACKAGE_OBFUSCATED || Common.IS_L_TREBUCHET)
                        && getObjectField(Common.APP_DRAWER_INSTANCE, ObfuscationHelper.Fields.acpvContentType).toString().equals("Widgets")) {

                    TabHelper tabHelper = TabHelper.getInstance();
                    if (tabHelper instanceof TabHelperNew) {
                        ((TabHelperNew) tabHelper).setCurrentTab(Tab.WIDGETS_ID);
                    }
                } else {
                    if (PreferencesHelper.appdrawerRememberLastPosition) {
                        if ((!Common.IS_KK_TREBUCHET && Common.IS_PRE_GNL_4) && !TabHelperLegacy.getInstance().getCurrentTabData().isWidgetsTab()) {
                            int lastTab = TabHelperLegacy.getInstance().getTabHost().getTabWidget().getTabCount() - 1;
                            if (Common.APPDRAWER_LAST_TAB_POSITION > lastTab) {
                                Common.APPDRAWER_LAST_TAB_POSITION = lastTab;
                            }

                            TabHelperLegacy.getInstance().setCurrentTab(Common.APPDRAWER_LAST_TAB_POSITION);
                        }

                        int lastPage = (Integer) callMethod(Common.APP_DRAWER_INSTANCE, "getChildCount") - 1;
                        if (Common.APPDRAWER_LAST_PAGE_POSITION > lastPage) {
                            Common.APPDRAWER_LAST_PAGE_POSITION = lastPage;
                        }

                        if (DEBUG)
                            log(param, "AppDrawer: set to last position " + Common.APPDRAWER_LAST_PAGE_POSITION);
                        callMethod(Common.APP_DRAWER_INSTANCE, Methods.pvSetCurrentPage, Common.APPDRAWER_LAST_PAGE_POSITION);
                    } else {
                        callMethod(Common.APP_DRAWER_INSTANCE, Methods.pvSetCurrentPage, 0);
                    }

                    if (!Common.IS_KK_TREBUCHET)
                        TabHelper.getInstance().scroll();
                }
            }
        });

        CommonHooks.OnLauncherTransitionEndListeners.add(new XGELSCallback() {

            int TOWORKSPACE = 2;

            @Override
            public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((Boolean) param.args[TOWORKSPACE]) {
                    Tab currTab = Common.IS_KK_TREBUCHET ? null : TabHelper.getInstance().getCurrentTabData();

                    if (PreferencesHelper.appdrawerRememberLastPosition) {
                        if (!Common.OVERSCROLLED) {
                            Common.APPDRAWER_LAST_PAGE_POSITION = getIntField(Common.APP_DRAWER_INSTANCE, ObfuscationHelper.Fields.pvCurrentPage);
                        }

                        if (Common.IS_PRE_GNL_4) {
                            if (!Common.OVERSCROLLED && !Common.IS_KK_TREBUCHET && !currTab.isWidgetsTab()) {
                                Common.APPDRAWER_LAST_TAB_POSITION = currTab.getIndex();
                            }
                        } else {
                            if (!Common.OVERSCROLLED && !Common.IS_KK_TREBUCHET && !currTab.isWidgetsTab()) {
                                Common.APPDRAWER_LAST_TAB_POSITION = currTab.getLayoutId();
                            }
                        }

                        if (DEBUG)
                            log(param, "AppDrawerHooks: get current position - " + Common.APPDRAWER_LAST_PAGE_POSITION);
                    }

                    if (!PreferencesHelper.appdrawerRememberLastPosition || (currTab != null && currTab.isWidgetsTab())) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Common.IS_KK_TREBUCHET) {
                                    return;
                                }

                                callMethod(Common.APP_DRAWER_INSTANCE, Methods.acpvSetContentType, callMethod(getObjectField(Common.LAUNCHER_INSTANCE, ObfuscationHelper.Fields.lAppsCustomizeTabHost), Methods.acthGetContentTypeForTabTag, "APPS"));
                                if (Common.IS_PRE_GNL_4) {
                                    TabHelperLegacy.getInstance().setCurrentTab(0);
                                } else {
                                    TabHelperNew.getInstance().setCurrentTab(0x80 + Tab.APPS_ID, true);
                                }
                            }
                        }, 1000);
                    }
                    Common.OVERSCROLLED = false;
                } else {
                    Common.ORIENTATION = Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation;
                }
            }
        });

        if (PreferencesHelper.changeGridSizeApps) {
            XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, Methods.acpvUpdatePageCounts, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setIntField(param.thisObject, ObfuscationHelper.Fields.acpvCellCountY, Common.ALL_APPS_Y_COUNT_HORIZONTAL);
                        setIntField(param.thisObject, ObfuscationHelper.Fields.acpvCellCountX, Common.ALL_APPS_X_COUNT_HORIZONTAL);
                        ((ViewGroup) param.thisObject).setPadding(Utils.dpToPx(24), ((ViewGroup) param.thisObject).getPaddingTop(), Utils.dpToPx(24), ((ViewGroup) param.thisObject).getPaddingBottom());
                    } else {
                        setIntField(param.thisObject, ObfuscationHelper.Fields.acpvCellCountY, Common.ALL_APPS_Y_COUNT_VERTICAL);
                        setIntField(param.thisObject, ObfuscationHelper.Fields.acpvCellCountX, Common.ALL_APPS_X_COUNT_VERTICAL);
                    }
                }
            });
        }

        if (Common.PACKAGE_OBFUSCATED && Common.IS_PRE_GNL_4) {
            findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSetAllAppsPadding, Rect.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Rect r = (Rect) param.args[0];
                        r.left = 0;
                        r.right = 0;
                    }
                }
            });
        } else if (Common.IS_KK_TREBUCHET) {
            findAndHookMethod(Classes.AppsCustomizePagedView, "setupPage", Classes.AppsCustomizeCellLayout, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        ViewGroup vg = (ViewGroup) param.args[0];
                        vg.setPadding(0, vg.getPaddingTop(), 0, vg.getPaddingBottom());
                    }
                }
            });
        }

        // hiding apps from the app drawer
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSetApps, ArrayList.class, new AllAppsListAddHook());
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvUpdateApps, ArrayList.class, new AllAppsListAddHook());
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvRemoveApps, ArrayList.class, new AllAppsListAddHook());

        AddTabsAndFolders.initAllHooks(lpparam);
        AddTabsAndFoldersLegacy.initAllHooks(lpparam);
    }
}