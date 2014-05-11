package de.theknut.xposedgelsettings.hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.appdrawer.AppDrawerHooks;
import de.theknut.xposedgelsettings.hooks.general.GeneralHooks;
import de.theknut.xposedgelsettings.hooks.gestures.GestureHooks;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;
import de.theknut.xposedgelsettings.hooks.homescreen.HomescreenHooks;
import de.theknut.xposedgelsettings.hooks.notificationbadges.NotificationBadgesHooks;
import de.theknut.xposedgelsettings.hooks.pagindicator.PageIndicatorHooks;
import de.theknut.xposedgelsettings.hooks.systemui.SystemUIReceiver;

public class GELSettings extends XC_MethodHook implements IXposedHookLoadPackage {
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		// only hook to supported launchers		
		if (lpparam.packageName.equals(Common.PACKAGE_NAME)) {
			
			// tells the UI that the module is active and running
			findAndHookMethod(Common.PACKAGE_NAME + ".ui.FragmentWelcome", lpparam.classLoader, "isXGELSActive", new XC_MethodReplacement() {
			    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
			        return true;
			    }
			});
			
			return;
			
		} else if (lpparam.packageName.equals("com.android.systemui")) {
			
			SystemUIReceiver.initAllHooks(lpparam);
			return;
			
		} else if (!Common.PACKAGE_NAMES.contains(lpparam.packageName)) {
			return;
		}
		
		if (!lpparam.isFirstApplication) {
			return;
		}
		
		// saving the name of the hooked package
		Common.HOOKED_PACKAGE = lpparam.packageName;		
		if (PreferencesHelper.Debug) XposedBridge.log("GELSettings.handleLoadPackage: hooked package -> " + lpparam.packageName);
		
		if (Common.HOOKED_PACKAGE.contains("com.android.launcher2")) {
			ObfuscationHelper.initClassNames("launcher2");
		}
		else {
			ObfuscationHelper.initClassNames("launcher3");
		}
		
		Classes.hookAllClasses(lpparam);
		Methods.initMethodNames();
		Fields.initFieldNames();
		
		// all hooks to modify...
		GeneralHooks.initAllHooks(lpparam);
		GoogleSearchBarHooks.initAllHooks(lpparam);
		PageIndicatorHooks.initAllHooks(lpparam);
		HomescreenHooks.initAllHooks(lpparam);
		AppDrawerHooks.initAllHooks(lpparam);
		GestureHooks.initAllHooks(lpparam);
		NotificationBadgesHooks.initAllHooks(lpparam);
	}
}