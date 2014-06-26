package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.ClassNames;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.appdrawer.AppDrawerHooks;
import de.theknut.xposedgelsettings.hooks.general.GeneralHooks;
import de.theknut.xposedgelsettings.hooks.gestures.GestureHooks;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;
import de.theknut.xposedgelsettings.hooks.homescreen.HomescreenHooks;
import de.theknut.xposedgelsettings.hooks.icon.IconHooks;
import de.theknut.xposedgelsettings.hooks.notificationbadges.NotificationBadgesHooks;
import de.theknut.xposedgelsettings.hooks.pagindicator.PageIndicatorHooks;
import de.theknut.xposedgelsettings.hooks.systemui.SystemUIReceiver;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class GELSettings extends XC_MethodHook implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

		// only hook to supported launchers		
		if (lpparam.packageName.equals(Common.PACKAGE_NAME)) {

			// tells the UI that the module is active and running
			findAndHookMethod(Common.PACKAGE_NAME + ".ui.FragmentWelcome", lpparam.classLoader, "isXGELSActive", XC_MethodReplacement.returnConstant(true));			
			return;

		} else if (lpparam.packageName.equals("com.android.systemui")) {

			SystemUIReceiver.initAllHooks(lpparam);
			return;

		} else if (!Common.PACKAGE_NAMES.contains(lpparam.packageName)) {
			return;
		}

		// saving the name of the hooked package
		Common.HOOKED_PACKAGE = lpparam.packageName;

		if (PreferencesHelper.Debug) XposedBridge.log("GELSettings.handleLoadPackage: hooked package -> " + lpparam.packageName);

        try {
            if (Common.HOOKED_PACKAGE.contains("com.android.launcher2")) {
                ClassNames.initNames("launcher2", true);
            } else {
                ClassNames.initNames("launcher3", true);
            }

            Classes.hookAllClasses(lpparam);
            Methods.initMethodNames(true);
            Fields.initFieldNames(true);

            // init all hooks...
            GeneralHooks.initAllHooks(lpparam);
            GoogleSearchBarHooks.initAllHooks(lpparam);
            PageIndicatorHooks.initAllHooks(lpparam);
            HomescreenHooks.initAllHooks(lpparam);
            AppDrawerHooks.initAllHooks(lpparam);
            GestureHooks.initAllHooks(lpparam);
            NotificationBadgesHooks.initAllHooks(lpparam);
            IconHooks.initAllHooks(lpparam);
        } catch (NoSuchMethodError nsme) {

            if (Common.HOOKED_PACKAGE.contains("com.android.launcher2")) {
                ClassNames.initNames("launcher2", false);
            } else {
                ClassNames.initNames("launcher3", false);
            }

            Classes.hookAllClasses(lpparam);
            Methods.initMethodNames(false);
            Fields.initFieldNames(false);

            // init all hooks...
            GeneralHooks.initAllHooks(lpparam);
            GoogleSearchBarHooks.initAllHooks(lpparam);
            PageIndicatorHooks.initAllHooks(lpparam);
            HomescreenHooks.initAllHooks(lpparam);
            AppDrawerHooks.initAllHooks(lpparam);
            GestureHooks.initAllHooks(lpparam);
            NotificationBadgesHooks.initAllHooks(lpparam);
            IconHooks.initAllHooks(lpparam);
        }
	}
}