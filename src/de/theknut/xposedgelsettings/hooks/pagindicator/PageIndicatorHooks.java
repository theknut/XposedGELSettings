package de.theknut.xposedgelsettings.hooks.pagindicator;

import android.graphics.Rect;
import android.view.ViewGroup;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
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
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class PageIndicatorHooks {

    public static void initAllHooks(LoadPackageParam lpparam) {

        // 0 - Default
        // 1 - Homescreen only
        // 2 - App drawer only
        // 3 - None

        switch (PreferencesHelper.pageIndicatorMode) {
            case 1:
                CommonHooks.LauncherOnCreateListeners.add(new XGELSCallback() {
                    @Override
                    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup tabHost = (ViewGroup) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lAppsCustomizeTabHost);
                        int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("apps_customize_page_indicator", "id", Common.HOOKED_PACKAGE);
                        if (id != 0) {
                            tabHost.findViewById(id).getLayoutParams().height = 0;
                        }
                        tabHost.setPadding(tabHost.getPaddingLeft(), tabHost.getPaddingTop(), tabHost.getPaddingRight(), -Utils.dpToPx(6));
                    }
                });
                break;
            case 2:
                CommonHooks.GetWorkspacePaddingListeners.add(new XGELSCallback() {
                    int pageIndicatorHeight = -1;
                    @Override
                    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                        pageIndicatorHeight = getIntField(param.thisObject, Fields.dpPageIndicatorHeightPx);
                        setIntField(param.thisObject, Fields.dpPageIndicatorHeightPx, 0);
                    }

                    @Override
                    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                        setIntField(param.thisObject, Fields.dpPageIndicatorHeightPx, pageIndicatorHeight);
                    }
                });

                CommonHooks.LauncherOnCreateListeners.add(new XGELSCallback() {
                    @Override
                    public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup dragLayer = (ViewGroup) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetDragLayer);
                        int id = Common.LAUNCHER_CONTEXT.getResources().getIdentifier("page_indicator", "id", Common.HOOKED_PACKAGE);
                        if (id != 0) {
                            dragLayer.findViewById(id).getLayoutParams().height = 0;
                        }
                    }
                });
                break;
            case 3:
                // hides the page indicator
                XposedBridge.hookAllMethods(Classes.PagedView, "onAttachedToWindow", new OnAttachedToWindowHook());

                // sets the height of the page indicator to 0
                CommonHooks.DeviceProfileConstructorListeners.add(new DeviceProfileConstructorHook());

                // reduce the bottom margin height in app drawer
                if (Common.IS_KK_TREBUCHET) {
                    findAndHookMethod(Classes.AppsCustomizeLayout, Methods.acthSetInsets, Rect.class, new SetInsetsHook(true));
                } else if (Common.GNL_VERSION < ObfuscationHelper.GNL_4_0_26) {
                    findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthSetInsets, Rect.class, new SetInsetsHook(false));
                }
            break;
        }
    }
}