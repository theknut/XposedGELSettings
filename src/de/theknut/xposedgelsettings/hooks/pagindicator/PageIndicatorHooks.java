package de.theknut.xposedgelsettings.hooks.pagindicator;

import android.graphics.Rect;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class PageIndicatorHooks {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (PreferencesHelper.hidePageIndicator) {
			// hides the page indicator
			XposedBridge.hookAllMethods(Classes.PagedView, "onAttachedToWindow", new OnAttachedToWindowHook());
			
			// sets the height of the page indicator to 0
			XposedBridge.hookAllConstructors(Classes.DeviceProfile, new DeviceProfileConstructorHook());

            if (Common.GNL_VERSION < ObfuscationHelper.GNL_4_0_26) {
                // reduce the bottom margin height in app drawer
                findAndHookMethod(Classes.AppsCustomizeLayout, ObfuscationHelper.Methods.acthSetInsets, Rect.class, new SetInsetsHook(Common.IS_TREBUCHET));
            }
		}
	}
}