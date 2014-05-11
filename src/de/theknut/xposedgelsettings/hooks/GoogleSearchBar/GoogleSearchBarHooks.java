package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GoogleSearchBarHooks extends HooksBaseClass {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (PreferencesHelper.hideSearchBar) {
			
			// hide Google Search Bar
			if (Common.PACKAGE_OBFUSCATED) {
				findAndHookMethod(Classes.DeviceProfile, Methods.dynamicgridLayout, float.class, Integer.TYPE, Resources.class, DisplayMetrics.class, new DynamicGridLayoutHook());
				findAndHookMethod(Classes.Launcher, "onCreate", Bundle.class, new LauncherOnCreateHook());
			} else {				
				XposedBridge.hookAllMethods(Classes.DeviceProfile, Methods.dynamicgridLayout, new DynamicGridLayoutHook());
			}
			
			// only do the following changes if we have the actual GEL launcher with GNow
			if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {				
				
				if (PreferencesHelper.autoHideSearchBar) {
					
					// hide search bar when the page is beeing moved
					XposedBridge.hookAllMethods(Classes.PagedView, Methods.pagedviewPageBeginMoving, new OnPageBeginMovingHook());
					// show search bar if GNow is visible
					XposedBridge.hookAllMethods(Classes.PagedView, Methods.pagedviewPageEndMoving, new OnPageEndMovingHook());
					
					// show Google Search Bar on GEL sidekick - needed if GNow isn't accessed from the homescreen
					findAndHookMethod(Classes.NowOverlay, Methods.noOnShow , boolean.class, boolean.class, new OnShowNowOverlayHook());
				}
				
				// show when doing a Google search			
				findAndHookMethod(Classes.SearchOverlayImpl, Methods.soiSetSearchStarted, boolean.class, new StartTextSearchHook());//"startTextSearch", new StartTextSearchHook());				
				//XposedBridge.hookAllMethods(Classes.SearchOverlayImpl, "stopSearch", new StopSearchHook());
				
				// show search plate on hotword detection
				//XposedBridge.hookAllMethods(Classes.SearchPlate, "enableAndShowSoundLevels", new EnableAndShowSoundLevelsHook());
			}
			
			// show DropDeleteTarget on dragging items
			if (Common.PACKAGE_OBFUSCATED) {
				// this is actually not DragSource but the parameter type is unknown as of now
				findAndHookMethod(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, Classes.DragSource, Object.class, new OnDragStart());
			} else {
				XposedBridge.hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, new OnDragStart());
			}
			
			XposedBridge.hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragEnd, new OnDragEnd());
		}
	}
	
	// method to hide the Google search bar
	public static void hideSearchbar() {
		setLayoutParams(0, 0, 0, 0);
	}
	
	// method to show the Google search bar
	public static void showSearchbar() {
		setLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Common.SEARCH_BAR_SPACE_WIDTH, Common.SEARCH_BAR_SPACE_HEIGHT);
	}
	
	// method to show or hide the Google search bar
	public static void setLayoutParams(int width, int height, int searchBarSpaceWidthPx, int searchBarSpaceHeightPx) {
		
		if (Common.LAUNCHER_INSTANCE == null) {
			XposedBridge.log("Couldn't do anything because the launcher instance is null");
			return;
		}
		
		Object launcher = Common.LAUNCHER_INSTANCE;
		
		// Layout the search bar space
		View searchBar = (View) callMethod(launcher, Methods.launcherGetSearchbar);
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
		
	    // horizontal search bar
		lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		lp.width = searchBarSpaceWidthPx;
		lp.height = searchBarSpaceHeightPx;
		
		// Layout the search bar
		View qsbBar = (View) callMethod(launcher, Methods.launcherGetQsbBar);
		LayoutParams vglp = qsbBar.getLayoutParams();
		vglp.width = width;
		vglp.height = height;
		
		qsbBar.setLayoutParams(vglp);
	}
}
