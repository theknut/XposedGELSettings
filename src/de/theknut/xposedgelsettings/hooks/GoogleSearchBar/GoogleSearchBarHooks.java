package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class GoogleSearchBarHooks extends HooksBaseClass {
	
	public static void initAllHooks(final LoadPackageParam lpparam) {



        // Experiment flags:
//        findAndHookMethod(findClass("bgr", lpparam.classLoader), "getHotwordConfigMap", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Map m = (Map) param.getResult();
//                String locale;
//                if (Common.NOW_SETTINGS != null) {
//                    locale = callMethod(Common.NOW_SETTINGS, "aqp").toString();
//                } else {
//                    locale = Locale.getDefault().toString().replace("_", "-");
//                }
//
//                log(locale + " " +  m.get(locale));
//                if (m.get(locale) == null) {
//                    Object bpl = newInstance(findClass("bpl", lpparam.classLoader));
//                    callMethod(bpl, "ei", locale); // 0x1
//                    callMethod(bpl, "ej", "en-US/hotword.data"); // 0x2
//                    m.put(locale, bpl);
//                }
//            }
//        });

        // current-bluetooth-device
//        XposedBridge.hookAllConstructors(findClass("gtv", lpparam.classLoader), new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Common.NOW_SETTINGS = param.thisObject;
//            }
//        });

        //findAndHookMethod(findClass("brc", lpparam.classLoader), "Gv", XC_MethodReplacement.returnConstant(true));
        //findAndHookMethod(findClass("brc", lpparam.classLoader), "Gw", XC_MethodReplacement.returnConstant(true));
//        findAndHookMethod(findClass("gtv", lpparam.classLoader), "jN", String.class, XC_MethodReplacement.returnConstant(true)); // voiceEverywhereEnabled
//        findAndHookMethod(findClass("bpl", lpparam.classLoader), "Gh", XC_MethodReplacement.returnConstant(true)); // for (int i = 1; ; i = 0)
//        findAndHookMethod(findClass("bjm", lpparam.classLoader), "Do", XC_MethodReplacement.returnConstant(2)); // debug_features_level

		if (PreferencesHelper.hideSearchBar) {
		    // hide Google Search Bar
		    findAndHookMethod(Classes.DeviceProfile, Methods.dpGetWorkspacePadding, Integer.TYPE, new GetWorkspacePaddingHook());
		    findAndHookMethod(Classes.Launcher, "onCreate", Bundle.class, new LauncherOnCreateHook());

            if (PreferencesHelper.searchBarOnDefaultHomescreen) {
                // show on default homescreen
                findAndHookMethod(Classes.Launcher, "onResume", new LauncherOnResumeHook());
                // hide search bar when the page is beeing moved
                hookAllMethods(Classes.PagedView, Methods.pvPageBeginMoving, new OnPageBeginMovingHook());
                // show search bar if GNow is visible
                hookAllMethods(Classes.PagedView, Methods.pvPageEndMoving, new OnPageEndMovingHook());
                // avoid that nasty animation when showing the search bar again
                findAndHookMethod(Classes.TransitionsManager, Methods.tmSetTransitionsEnabled, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = false;
                    }
                });
            }
			
			// only do the following changes if we have the actual GEL launcher with GNow
			if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {				
				
				if (PreferencesHelper.autoHideSearchBar) {
					
					// hide search bar when the page is beeing moved
					hookAllMethods(Classes.PagedView, Methods.pvPageBeginMoving, new OnPageBeginMovingHook());
					// show search bar if GNow is visible
					hookAllMethods(Classes.PagedView, Methods.pvPageEndMoving, new OnPageEndMovingHook());
					
					// show Google Search Bar on GEL sidekick - needed if GNow isn't accessed from the homescreen
					findAndHookMethod(Classes.NowOverlay, Methods.noOnShow, boolean.class, boolean.class, new OnShowNowOverlayHook());

                    // avoid that nasty animation when showing the search bar again
                    findAndHookMethod(Classes.TransitionsManager, Methods.tmSetTransitionsEnabled, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[0] = false;
                        }
                    });
				}

				// show when doing a Google search
                findAndHookMethod(Classes.SearchOverlayImpl, Methods.soiSetSearchStarted, boolean.class, new SetSearchStarted());
            }
			
			// show DropDeleteTarget on dragging items
			if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
				// this is actually not DragSource but the parameter type is unknown as of now
				findAndHookMethod(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, Classes.DragSource, Object.class, new OnDragStart());
			} else {
				hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragStart, new OnDragStart());
			}
			
			hookAllMethods(Classes.SearchDropTargetBar, Methods.sdtbOnDragEnd, new OnDragEnd());
		}

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {

            if (PreferencesHelper.alwaysShowSayOKGoogle) {
                findAndHookMethod(Classes.GSAConfigFlags, Methods.gsaShouldAlwaysShowHotwordHint, XC_MethodReplacement.returnConstant(true));
                findAndHookMethod(Classes.RecognizerView, Methods.rvCanShowHotwordAnimation, XC_MethodReplacement.returnConstant(false));
            }

            // 0 - Default
            // 1 - Android L
            if (PreferencesHelper.searchbarStyle == 1) {
                XC_MethodHook proximityToNowHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = 1.0f;
                    }
                };

                findAndHookMethod(Classes.SearchPlate, Methods.spSetProximityToNow, float.class, proximityToNowHook);
                findAndHookMethod(Classes.GelSearchPlateContainer, Methods.spSetProximityToNow, float.class, proximityToNowHook);
            }
        }
	}

	// method to hide the Google search bar
	public static void hideSearchbar() {
		setLayoutParams(false);
	}
	
	// method to show the Google search bar
	public static void showSearchbar() {
		setLayoutParams(true);
	}
	
	// method to show or hide the Google search bar
	public static void setLayoutParams(final boolean show) {
		
		if (Common.LAUNCHER_INSTANCE == null) {
			log("Couldn't do anything because the launcher instance is null");
			return;
		}
		
		// Layout the search bar space
		View searchBar = (View) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetSearchbar);
		final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
		
		if (Common.SEARCH_BAR_SPACE_HEIGHT == -1 && lp.height != 0) {
			Common.SEARCH_BAR_SPACE_HEIGHT = lp.height;
		}
        lp.height = show ? Common.SEARCH_BAR_SPACE_HEIGHT : 0;
		
		// Layout the search bar
		View qsbBar = (View) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetQsbBar);
		LayoutParams vglp = qsbBar.getLayoutParams();
		vglp.width = show ? LayoutParams.MATCH_PARENT : 0;
		vglp.height = show ? LayoutParams.MATCH_PARENT : 0;
        qsbBar.setLayoutParams(vglp);
	}
}
