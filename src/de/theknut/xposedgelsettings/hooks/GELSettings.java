package de.theknut.xposedgelsettings.hooks;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.R;
import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.appdrawer.AppDrawerHooks;
import de.theknut.xposedgelsettings.hooks.general.GeneralHooks;
import de.theknut.xposedgelsettings.hooks.gestures.GestureHooks;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.GoogleSearchBarHooks;
import de.theknut.xposedgelsettings.hooks.homescreen.HomescreenHooks;
import de.theknut.xposedgelsettings.hooks.notificationbadges.NotificationBadgesHooks;
import de.theknut.xposedgelsettings.hooks.pagindicator.PageIndicatorHooks;
import de.theknut.xposedgelsettings.hooks.systemui.SystemUIReceiver;

public class GELSettings extends XC_MethodHook implements IXposedHookLoadPackage {
	
	private static String MODULE_PATH = null;
	
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
			Common.initClassNames("launcher2");
		}
		else {
			Common.initClassNames("launcher3");
		}
		
		// all hooks to modify...
		GeneralHooks.initAllHooks(lpparam);
		GoogleSearchBarHooks.initAllHooks(lpparam);
		PageIndicatorHooks.initAllHooks(lpparam);
		HomescreenHooks.initAllHooks(lpparam);
		AppDrawerHooks.initAllHooks(lpparam);
		GestureHooks.initAllHooks(lpparam);
		NotificationBadgesHooks.initAllHooks(lpparam);
	}
//	
//	@Override
//    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
//        if (!resparam.packageName.equals("com.android.systemui"))
//            return;
//
//        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
//        resparam.res.setReplacement("com.android.launcher3", "layout", "folder_icon", modRes.fwd(R.id.f));
//    }
}