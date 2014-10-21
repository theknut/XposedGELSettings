package de.theknut.xposedgelsettings.hooks.pagindicator;

import android.graphics.Rect;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class PageIndicatorHooks {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (PreferencesHelper.hidePageIndicator) {
            // hides the page indicator
            XposedBridge.hookAllMethods(Classes.PagedView, "onAttachedToWindow", new OnAttachedToWindowHook());

            // sets the height of the page indicator to 0
            XposedBridge.hookAllConstructors(Classes.DeviceProfile, new DeviceProfileConstructorHook());

            // reduce the bottom margin height in app drawer
            if (Common.IS_TREBUCHET) {
                findAndHookMethod(Classes.AppsCustomizeLayout, Methods.acthSetInsets, Rect.class, new SetInsetsHook(true));
            }
            else if (Common.GNL_VERSION < ObfuscationHelper.GNL_4_0_26) {
                findAndHookMethod(Classes.AppsCustomizeTabHost, Methods.acthSetInsets, Rect.class, new SetInsetsHook(false));
            }
        }
    }
}