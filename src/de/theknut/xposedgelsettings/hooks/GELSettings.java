package de.theknut.xposedgelsettings.hooks;

import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.AppDrawer.AppDrawerHooks;
import de.theknut.xposedgelsettings.hooks.General.GeneralHooks;
import de.theknut.xposedgelsettings.hooks.Gestures.GestureHooks;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.GoogleSearchBarHooks;
import de.theknut.xposedgelsettings.hooks.Homescreen.HomescreenHooks;
import de.theknut.xposedgelsettings.hooks.PageIndicator.PageIndicatorHooks;

public class GELSettings implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		// only hook to supported launchers		
		if (!Common.PACKAGE_NAMES.contains(lpparam.packageName)) {
			return;
		}
		else if (lpparam.packageName.contains(Common.PACKAGE_NAME) && Common.LAUNCHER != null) {
			Toast.makeText(Common.LAUNCHER_CONTEXT, "Ready for awesome!", Toast.LENGTH_LONG).show();
			return;
		}
		
		// saving the name of the hooked package
		Common.HOOKED_PACKAGE = lpparam.packageName;
		
		if (Common.HOOKED_PACKAGE.contains("com.android.launcher2")) {
			Common.initClassNames("launcher2");
		}
		else {
			Common.initClassNames("launcher3");
		}
		
		// all hooks to modify...
		GeneralHooks.initAllHooks(lpparam);			// general hooks
		GoogleSearchBarHooks.initAllHooks(lpparam); // the Google search bar		
		PageIndicatorHooks.initAllHooks(lpparam);	// the page indicator		
		HomescreenHooks.initAllHooks(lpparam);		// stuff on the homescreen		
		AppDrawerHooks.initAllHooks(lpparam);		// stuff in the app drawer
		GestureHooks.initAllHooks(lpparam);			// stuff for gestures
	}	
}