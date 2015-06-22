package de.theknut.xposedgelsettings.hooks.googlesearchbar;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.googlesearchbar.weatherwidget.WeatherWidget;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class GoogleSearchBarHooks extends HooksBaseClass {

    private static View qsb;

    public static void initAllHooks(final LoadPackageParam lpparam) {

        if (PreferencesHelper.hideSearchBar) {
            // hide Google Search Bar
            CommonHooks.GetWorkspacePaddingListeners.add(new GetWorkspacePaddingHook());
            findAndHookMethod(Classes.Launcher, "onCreate", Bundle.class, new LauncherOnCreateHook());

            if (PreferencesHelper.searchBarOnDefaultHomescreen) {
                // show on default homescreen
                CommonHooks.LauncherOnResumeListeners.add(new LauncherOnResumeHook());
                // hide search bar when the page is beeing moved
                CommonHooks.PageBeginMovingListeners.add(new OnPageBeginMovingHook());
                // show search bar if GNow is visible
                CommonHooks.PageEndMovingListeners.add(new OnPageEndMovingHook());

                if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
                    // avoid that nasty animation when showing the search bar again
                    findAndHookMethod(Classes.TransitionsManager, Methods.tmSetTransitionsEnabled, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[0] = false;
                        }
                    });
                }
            }

            // only do the following changes if we have the actual GEL launcher with GNow
            if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {

                if (PreferencesHelper.autoHideSearchBar) {

                    // hide search bar when the page is beeing moved
                    CommonHooks.PageBeginMovingListeners.add(new OnPageBeginMovingHook());
                    // show search bar if GNow is visible
                    CommonHooks.PageEndMovingListeners.add(new OnPageEndMovingHook());

                    // show Google Search Bar on GEL sidekick - needed if GNow isn't accessed from the homescreen
                    CommonHooks.OnNowShowListeners.add(new OnShowNowOverlayHook());

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
            CommonHooks.OnDragStartListeners.add(new OnDragStart());
            CommonHooks.OnDragEndListeners.add(new OnDragEnd());
        }

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_2_16) {
                if (PreferencesHelper.alwaysShowSayOKGoogle) {
                    findAndHookMethod(Classes.SearchSettings, Methods.ssFirstHotwordHintShownAt, XC_MethodReplacement.returnConstant(0L));
                }

                XposedBridge.hookAllConstructors(Classes.SearchPlateBar, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Drawable searchBg = (Drawable) getObjectField(param.thisObject, Fields.spbMic);
                        searchBg.setColorFilter(PreferencesHelper.searchbarPrimaryColor, PorterDuff.Mode.MULTIPLY);
                    }
                });

                if (Utils.getContrastColor(PreferencesHelper.searchbarPrimaryColor) == Color.WHITE) {

                    final int color = Color.WHITE;
                    final int darkerColor = 0xFFF4F4F4;

                    findAndHookMethod(Classes.SearchPlate, "onFinishInflate", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            ViewGroup searchPlate = (ViewGroup) param.thisObject;
                            Resources resources = searchPlate.getContext().getResources();

                            int id = resources.getIdentifier("launcher_search_button", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                ((ImageView) searchPlate.findViewById(id)).setImageDrawable(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_google_small_light));
                            }

                            id = resources.getIdentifier("search_box", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                EditText simpleSearchText = (EditText) searchPlate.findViewById(id);
                                simpleSearchText.setTextColor(color);
                                simpleSearchText.setHintTextColor(darkerColor);
                            }

                            id = resources.getIdentifier("say_ok_google", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                ((TextView) searchPlate.findViewById(id)).setTextColor(darkerColor);
                            }

                            id = resources.getIdentifier("clear_or_voice_button", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                View clearOrVoiceButton = searchPlate.findViewById(id);

                                if (Common.GNL_VERSION == ObfuscationHelper.GNL_4_7_12
                                    || Common.GNL_VERSION == ObfuscationHelper.GNL_4_7_13) {
                                    callMethod(clearOrVoiceButton, "Uk");
                                    callMethod(clearOrVoiceButton, "Ul");
                                    callMethod(clearOrVoiceButton, "Um");
                                }

                                ((Paint) getObjectField(clearOrVoiceButton, Fields.covbFields[0])).setColor(darkerColor);
                                ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[1])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[2])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[3])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                            }
                        }
                    });
                }
            }

            // 0 - Default
            // 1 - Android L
            if (PreferencesHelper.searchbarStyle == 1 && Common.IS_PRE_GNL_4) {
                XC_MethodHook proximityToNowHook = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (PreferencesHelper.searchbarStyle == 1) {
                            param.args[0] = 1f;
                        }
                    }
                };

                findAndHookMethod(Classes.SearchPlate, Methods.spSetProximityToNow, float.class, proximityToNowHook);
                findAndHookMethod(Classes.GelSearchPlateContainer, Methods.spSetProximityToNow, float.class, proximityToNowHook);
            }

            if (Common.GNL_VERSION > ObfuscationHelper.GNL_4_0_26) {
                WeatherWidget.initAllHooks(lpparam);
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
        View searchBar = (View) getObjectField(Common.LAUNCHER_INSTANCE, Fields.lSearchDropTargetBar);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();

        if (Common.SEARCH_BAR_SPACE_HEIGHT == -1 && lp.height != 0) {
            Common.SEARCH_BAR_SPACE_HEIGHT = lp.height;
        }
        lp.height = show ? Common.SEARCH_BAR_SPACE_HEIGHT : 0;

        // Layout the search bar
        View qsbBar;

        if (Common.PACKAGE_OBFUSCATED && Common.GNL_VERSION >= ObfuscationHelper.GNL_4_1_21) {
            qsbBar = (View) getObjectField(searchBar, Fields.sdtbQsbBar);
        } else {
            qsbBar = (View) callMethod(Common.LAUNCHER_INSTANCE, Methods.lGetQsbBar);
        }
        LayoutParams vglp = qsbBar.getLayoutParams();
        vglp.width = show ? LayoutParams.MATCH_PARENT : 0;
        vglp.height = show ? LayoutParams.MATCH_PARENT : 0;
        qsbBar.setLayoutParams(vglp);
    }
}
