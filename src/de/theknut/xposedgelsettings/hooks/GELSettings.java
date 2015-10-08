package de.theknut.xposedgelsettings.hooks;

import android.content.Context;
import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.androidintegration.AppInfo;
import de.theknut.xposedgelsettings.hooks.androidintegration.QuickSettingsL;
import de.theknut.xposedgelsettings.hooks.androidintegration.QuickSettingsPreL;
import de.theknut.xposedgelsettings.hooks.androidintegration.SystemBars;
import de.theknut.xposedgelsettings.hooks.androidintegration.SystemUIHooks;
import de.theknut.xposedgelsettings.hooks.androidintegration.SystemUIReceiver;
import de.theknut.xposedgelsettings.hooks.appdrawer.AppDrawerLHooks;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.general.ContextMenu;
import de.theknut.xposedgelsettings.hooks.general.GeneralHooks;
import de.theknut.xposedgelsettings.hooks.gestures.GestureHooks;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;
import de.theknut.xposedgelsettings.hooks.homescreen.HomescreenHooks;
import de.theknut.xposedgelsettings.hooks.icon.IconHooks;
import de.theknut.xposedgelsettings.hooks.notificationbadges.NotificationBadgesHooks;
import de.theknut.xposedgelsettings.hooks.pagindicator.PageIndicatorHooks;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class GELSettings extends XC_MethodHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        // only hook to supported launchers
        if (lpparam.packageName.equals(Common.PACKAGE_NAME)) {
            // tells the UI that the module is active and running
            findAndHookMethod(Common.PACKAGE_NAME + ".ui.FragmentWelcome", lpparam.classLoader, "isXGELSActive", XC_MethodReplacement.returnConstant(true));
            return;

        } else if (lpparam.packageName.equals("com.android.systemui")) {
            PreferencesHelper.init();
            SystemUIReceiver.initAllHooks(lpparam);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                QuickSettingsPreL.initAllHooks(lpparam);
            } else {
                QuickSettingsL.initAllHooks(lpparam);
            }

            return;

        } else if (lpparam.packageName.equals("com.android.settings")) {
            PreferencesHelper.init();
            AppInfo.initAllHooks(lpparam);
            return;

        } else if (!Common.PACKAGE_NAMES.contains(lpparam.packageName)) {
            return;
        }

        int versionIdx;
        Common.HOOKED_PACKAGE = lpparam.packageName;
        if (PreferencesHelper.Debug) XposedBridge.log("XGELS: GELSettings.handleLoadPackage: hooked package -> " + lpparam.packageName);

        try {
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // thanks to KeepChat for the following snippet:
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            Context context = (Context) callMethod(activityThread, "getSystemContext");
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
                Common.IS_GNL = true;
                Common.GNL_PACKAGE_INFO = context.getPackageManager().getPackageInfo(lpparam.packageName, 0);
                Common.GNL_VERSION = Common.GNL_PACKAGE_INFO.versionCode;
                versionIdx = ObfuscationHelper.getVersionIndex(Common.GNL_VERSION);
                if (versionIdx > 0) Common.PACKAGE_OBFUSCATED = true;
                if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_0_26) Common.IS_PRE_GNL_4 = false;
                if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23) Common.IS_M_GNL = true;

                if (PreferencesHelper.Debug)
                    XposedBridge.log("XGELS: " + Common.HOOKED_PACKAGE + " V" + Common.GNL_PACKAGE_INFO.versionName + "(" + Common.GNL_VERSION + ") Target SDK " + Common.GNL_PACKAGE_INFO.applicationInfo.targetSdkVersion);
            } else {
                Common.IS_PRE_GNL_4 = false;
                Common.IS_TREBUCHET = Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE);
                Common.IS_L_TREBUCHET = Common.IS_TREBUCHET && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
                Common.IS_KK_TREBUCHET = Common.IS_TREBUCHET && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
                if (Common.IS_L_TREBUCHET) {
                    Common.HOOKED_PACKAGE = "com.android.launcher3";
                }
                versionIdx = 0;
            }

            Common.XGELSCONTEXT = context.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            XposedBridge.log("XGELS: exception while trying to get version info. (" + e.getMessage() + ")");
            return;
        }

        PreferencesHelper.init();
        ObfuscationHelper.init(lpparam, versionIdx);

        long time = System.currentTimeMillis();
        // init all hooks...
        GeneralHooks.initAllHooks(lpparam);
        ContextMenu.initAllHooks(lpparam);
        GoogleSearchBarHooks.initAllHooks(lpparam);
        PageIndicatorHooks.initAllHooks(lpparam);
        HomescreenHooks.initAllHooks(lpparam);
        SystemUIHooks.initAllHooks(lpparam);
        SystemBars.initAllHooks(lpparam);
        if (!Common.IS_M_GNL) AppDrawerLHooks.initAllHooks(lpparam);
        GestureHooks.initAllHooks(lpparam);
        NotificationBadgesHooks.initAllHooks(lpparam);
        IconHooks.initAllHooks(lpparam);
        CommonHooks.initAllHooks(lpparam);
        if (PreferencesHelper.Debug) XposedBridge.log("Installed hooks in " + (System.currentTimeMillis() - time) + "ms");
    }
}