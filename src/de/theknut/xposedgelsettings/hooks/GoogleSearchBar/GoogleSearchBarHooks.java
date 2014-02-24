package de.theknut.xposedgelsettings.hooks.GoogleSearchBar;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class GoogleSearchBarHooks {
	
	public static void initAllHooks(LoadPackageParam lpparam) {
		
		if (PreferencesHelper.hideSearchBar) {
			
			// hide Google Search Bar
			final Class<?> DynamicGridClass = findClass(Common.DYNAMIC_GRID, lpparam.classLoader);
			XposedBridge.hookAllMethods(DynamicGridClass, "layout", new DynamicGridLayoutHook());
			
			// only do the following changes if we have the actual GEL launcher with GNow
			if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {				
				
				if (PreferencesHelper.autoHideSearchBar) {
					
					// saves the instance of GEL				
					final Class<?> GELClass = findClass(Common.GEL, lpparam.classLoader);
					XposedBridge.hookAllConstructors(GELClass, new GELHook());
					
					final Class<?> PagedViewClass = findClass(Common.PAGED_VIEW, lpparam.classLoader);
					// hide search bar when the page is beeing moved
					XposedBridge.hookAllMethods(PagedViewClass, "onPageBeginMoving", new OnPageBeginMovingHook());
					// show search bar if GNow is visible
					XposedBridge.hookAllMethods(PagedViewClass, "onPageEndMoving", new OnPageEndMovingHook());
					
					// show Google Search Bar on GEL sidekick - needed if GNow isn't accessed from the homescreen			
					final Class<?> NowOverlassClass = findClass(Common.NOW_OVERLAY, lpparam.classLoader);
					XposedBridge.hookAllConstructors(NowOverlassClass, new NowOverlayConstructorHook());
					XposedBridge.hookAllMethods(NowOverlassClass, "onShow", new OnShowNowOverlayHook());
				}
				
				// show when doing a Google search
				final Class<?> SearchOverlayImplClass = findClass(Common.SEARCH_OVERLAY_IMPL, lpparam.classLoader);				
				XposedBridge.hookAllMethods(SearchOverlayImplClass, "startTextSearch", new StartTextSearchHook());				
				XposedBridge.hookAllMethods(SearchOverlayImplClass, "stopSearch", new StopSearchHook());
				
				// show search plate on hotword detection
				final Class<?> SearchPlateClass = findClass(Common.SEARCH_PLATE, lpparam.classLoader);
				XposedBridge.hookAllMethods(SearchPlateClass, "enableAndShowSoundLevels", new EnableAndShowSoundLevelsHook());
			}
			
			// show DropDeleteTarget on dragging items
			final Class<?> SearchDropTargetBar = findClass(Common.SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
			XposedBridge.hookAllMethods(SearchDropTargetBar, "onDragStart", new OnDragStart());
			XposedBridge.hookAllMethods(SearchDropTargetBar, "onDragEnd", new OnDragEnd());
		}
	}
	
	// method to show or hide the Google search bar
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
