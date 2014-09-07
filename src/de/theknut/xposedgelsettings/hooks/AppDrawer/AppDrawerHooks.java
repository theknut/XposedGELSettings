package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AddTabsAndFolders;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class AppDrawerHooks extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        // save an instance of the app drawer object
        XposedBridge.hookAllConstructors(Classes.AppsCustomizePagedView, new AppsCustomizePagedViewConstructorHook());

        if (PreferencesHelper.iconSettingsSwitchApps) {
            // changing the appearence of the icons in the app drawer
            XposedBridge.hookAllMethods(Classes.PagedViewIcon, Methods.applyFromApplicationInfo, new ApplyFromApplicationInfoHook());
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
            // set the background pref_color of the app drawer
            XposedBridge.hookAllConstructors(Classes.AppsCustomizeLayout, new AppsCustomizeLayoutConstructorHook());
        }
        else {
            // set the background pref_color of the app drawer
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

        findAndHookMethod(Classes.Workspace, Methods.wOnLauncherTransitionEnd, Classes.Launcher, boolean.class, boolean.class, new XC_MethodHook() {

            int TOWORKSPACE = 2;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {


                if ((Boolean) param.args[TOWORKSPACE]) {
                    if (PreferencesHelper.appdrawerRememberLastPosition) {
                        if (!Common.OVERSCROLLED) {
                            Object acpv = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);
                            Common.APPDRAWER_LAST_PAGE_POSITION = getIntField(acpv, Fields.acpvCurrentPage);
                        }

                        if (DEBUG)
                            log(param, "AppDrawerHooks: get current position - " + Common.APPDRAWER_LAST_PAGE_POSITION);
                    }

                    if (!Common.OVERSCROLLED && !TabHelper.getInstance().getCurrentTabData().isWidgetsTab()) {
                        Common.APPDRAWER_LAST_TAB_POSITION = TabHelper.getInstance().getTabHost().getCurrentTab();
                    }

                    Common.OVERSCROLLED = false;
                }
            }
        });

        findAndHookMethod(Classes.Workspace, Methods.wOnTransitionPrepare, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Common.OVERSCROLLED) return;

                if (!TabHelper.getInstance().getCurrentTabData().isWidgetsTab()) {
                    int lastTab = TabHelper.getInstance().getTabHost().getTabWidget().getTabCount() - 1;
                    if (Common.APPDRAWER_LAST_TAB_POSITION > lastTab) {
                        Common.APPDRAWER_LAST_TAB_POSITION = lastTab;
                    }

                    TabHelper.getInstance().setCurrentTab(Common.APPDRAWER_LAST_TAB_POSITION);
                }

                if (PreferencesHelper.appdrawerRememberLastPosition) {

                    Object acpv = getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizePagedView);

                    int lastPage = (Integer) callMethod(acpv, "getChildCount") - 1;

                    if (Common.APPDRAWER_LAST_PAGE_POSITION > lastPage) {
                        Common.APPDRAWER_LAST_PAGE_POSITION = lastPage;
                    }

                    if (DEBUG)
                        log(param, "AppDrawerHooks: set to last position " + Common.APPDRAWER_LAST_PAGE_POSITION);
                    callMethod(acpv, Methods.acpvSetCurrentPage, Common.APPDRAWER_LAST_PAGE_POSITION);
                }
            }
        });

        // hiding apps from the app drawer
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvSetApps, ArrayList.class, new AllAppsListAddHook());
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvUpdateApps, ArrayList.class, new AllAppsListAddHook());
        findAndHookMethod(Classes.AppsCustomizePagedView, Methods.acpvRemoveApps, ArrayList.class, new AllAppsListAddHook());

        AddTabsAndFolders.initAllHooks(lpparam);

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
//		XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, "dY", new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//log("Width " + ((View) callMethod(param.thisObject, Methods.pvGetPageAt, 0)).getWidth());
//                        log("Width " + ((View) ((View) callMethod(param.thisObject, Methods.pvGetPageAt, 0)).getParent()).getWidth());
//                        log("Width " + ((View) ((View) ((View) callMethod(param.thisObject, Methods.pvGetPageAt, 0)).getParent()).getParent()).getWidth());
//                        log("lol");
//                    }
//                });
//
//        XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, "onMeasure", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                XposedBridge.log("XGELS updatePageCounts");
//
//                if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    Object tmp = param.args[0];
//                    param.args[0] = param.args[1];
//                    param.args[1] = tmp;
//                }
//            }
//        });
//
//		XposedBridge.hookAllMethods(Classes.AppsCustomizePagedView, "updatePageCounts", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("XGELS updatePageCounts");
//
//				if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//					setIntField(param.thisObject, Fields.acpvCellCountY, PreferencesHelper.xCountAllApps);
//					setIntField(param.thisObject, Fields.acpvCellCountX, PreferencesHelper.yCountAllApps);
//				} else {
//					setIntField(param.thisObject, Fields.acpvCellCountY, PreferencesHelper.yCountAllApps);
//					setIntField(param.thisObject, Fields.acpvCellCountX, PreferencesHelper.xCountAllApps);
//				}
//			}
//		});
    }
}