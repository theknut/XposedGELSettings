package de.theknut.xposedgelsettings.hooks;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.Common;
import de.theknut.xposedgelsettings.hooks.Apps.AllAppsGridHook;
import de.theknut.xposedgelsettings.hooks.Apps.AllAppsListHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.DynamicGridHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.EnableAndShowSoundLevelsHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.GELHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.NowOverlayConstructorHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.OnDragEnd;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.OnDragStart;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.OnPageBeginMovingHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.OnPageEndMovingHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.StartTextSearchHook;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.StopSearchHook;
import de.theknut.xposedgelsettings.hooks.Icons.CellLayoutHook;
import de.theknut.xposedgelsettings.hooks.Icons.PagedViewIconHook;

public class GELSettings implements IXposedHookLoadPackage {
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!Common.PACKAGE_NAMES.contains(lpparam.packageName))
			return;
		
		Common.HOOKED_PACKAGE = lpparam.packageName;
		
		if (Common.HOOKED_PACKAGE.contains("com.android.launcher2")) {
			Common.ALL_APPS_LIST = "com.android.launcher2.AllAppsList";
			Common.ITEM_INFO = "com.android.launcher2.ItemInfo";
			Common.CELL_LAYOUT = "com.android.launcher2.CellLayout";
			Common.SEARCH_DROP_TARGET_BAR = "com.android.launcher2.SearchDropTargetBar";
			Common.DYNAMIC_GRID = "com.android.launcher2.DeviceProfile";
			Common.PAGED_VIEW = "com.android.launcher2.PagedView";
			Common.PAGED_VIEW_ICON = "com.android.launcher2.PagedViewIcon";
			Common.DEVICE_PROFILE = "com.android.launcher2.DeviceProfile";
		}			
		
		if (PreferencesHelper.hideSearchBar) {
			// hide Google Search Bar
			final Class<?> dg = findClass(Common.DYNAMIC_GRID, lpparam.classLoader);
			XposedBridge.hookAllMethods(dg, "layout", new DynamicGridHook());
			
			if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {				
				
				if (PreferencesHelper.autoHideSearchBar) {
					// show Google Search Bar on GEL sidekick				
					final Class<?> no = findClass(Common.NOW_OVERLAY, lpparam.classLoader);
					XposedBridge.hookAllConstructors(no, new NowOverlayConstructorHook());
					
					// show Google Search Bar on GEL sidekick				
					final Class<?> gel = findClass(Common.GEL, lpparam.classLoader);
					XposedBridge.hookAllConstructors(gel, new GELHook());
					
					final Class<?> pv = findClass(Common.PAGED_VIEW, lpparam.classLoader);
					XposedBridge.hookAllMethods(pv, "onPageBeginMoving", new OnPageBeginMovingHook());
					XposedBridge.hookAllMethods(pv, "onPageEndMoving", new OnPageEndMovingHook());
				}
				
				// show when doing a Google search
				final Class<?> vs = findClass(Common.SEARCH_OVERLAY_IMPL, lpparam.classLoader);				
				XposedBridge.hookAllMethods(vs, "startTextSearch", new StartTextSearchHook());				
				XposedBridge.hookAllMethods(vs, "stopSearch", new StopSearchHook());
				
				// show search plate on hotword detection
				final Class<?> sp = findClass(Common.SEARCH_PLATE, lpparam.classLoader);				
				XposedBridge.hookAllMethods(sp, "enableAndShowSoundLevels", new EnableAndShowSoundLevelsHook());								
			}
			
			// show DropDeleteTarget on dragging items
			final Class<?> sdtb = findClass(Common.SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
			XposedBridge.hookAllMethods(sdtb, "onDragStart", new OnDragStart());
			XposedBridge.hookAllMethods(sdtb, "onDragEnd", new OnDragEnd());
		}
		
		if (PreferencesHelper.hideIconLabelHome && PreferencesHelper.iconSettingsSwitch) {
			// changing the appearence of the icons on the homescreen
			final Class<?> cl = findClass(Common.CELL_LAYOUT, lpparam.classLoader);
			XposedBridge.hookAllMethods(cl, "addViewToCellLayout", new CellLayoutHook());
		}
		
		if (PreferencesHelper.hideIconLabelApps && PreferencesHelper.iconSettingsSwitch) {
			// changing the appearence of the icons in the app drawer
			final Class<?> pvi = findClass(Common.PAGED_VIEW_ICON, lpparam.classLoader);
			XposedBridge.hookAllMethods(pvi, "applyFromApplicationInfo", new PagedViewIconHook());
		}
		
		if (PreferencesHelper.changeGridSize || PreferencesHelper.iconSettingsSwitch) {
			final Class<?> dp = findClass(Common.DEVICE_PROFILE, lpparam.classLoader);
			XposedBridge.hookAllConstructors(dp, new DeviceProfileHook());
			XposedBridge.hookAllMethods(dp, "updateFromConfiguration", new AllAppsGridHook());
		}
		
		// hiding apps from the app drawer
		final Class<?> aal = findClass(Common.ALL_APPS_LIST, lpparam.classLoader);
		XposedBridge.hookAllMethods(aal, "add", new AllAppsListHook());
	}
	
	public static void setLayoutParams(Object launcher, int width, int height, int searchBarSpaceWidthPx, int searchBarSpaceHeightPx) {
		if (launcher == null) {
			XposedBridge.log("Couldn't do anything because the launcher instance is null");
			return;
		}
		// Layout the search bar space
		View searchBar = (View) callMethod(launcher, "getSearchBar");
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
		
	    // horizontal search bar
		lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		lp.width = searchBarSpaceWidthPx;
		lp.height = searchBarSpaceHeightPx;
		
		// Layout the search bar
		View qsbBar = (View) callMethod(launcher, "getQsbBar");
		LayoutParams vglp = qsbBar.getLayoutParams();
		vglp.width = width;
		vglp.height = height;
		
		qsbBar.setLayoutParams(vglp);
		
		try {
			setObjectField(launcher, "mSearchDropTargetBar", searchBar);
			setObjectField(launcher, "mQsbBar", qsbBar);
		}
		catch (NoSuchFieldError nsfe) {}
	}
}