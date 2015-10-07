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
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class GoogleSearchBarHooks extends HooksBaseClass {

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
                    blockSearchbarTransitions(lpparam);
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

                    blockSearchbarTransitions(lpparam);
                }

                // show when doing a Google search
                findAndHookMethod(Classes.SearchOverlayImpl, Methods.soiSetSearchStarted, boolean.class, new SetSearchStarted());
            }

            // show DropDeleteTarget on dragging items
            CommonHooks.OnDragStartListeners.add(new OnDragStart());
            CommonHooks.OnDragEndListeners.add(new OnDragEnd());
        }

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            final int color = Color.WHITE;
            final int darkerColor = 0xFFF4F4F4;

            if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_2_16) {
                if (PreferencesHelper.alwaysShowSayOKGoogle
                        && (Common.GNL_VERSION < ObfuscationHelper.GNL_5_2_33
                        || Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23)) {
                    findAndHookMethod(Classes.SearchSettings, Methods.ssFirstHotwordHintShownAt, XC_MethodReplacement.returnConstant(true));
                }

                if (PreferencesHelper.searchbarStyle != 3) {
                    XposedBridge.hookAllConstructors(Classes.SearchPlateBar, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Drawable searchBg = (Drawable) getObjectField(param.thisObject, Fields.spbMic);
                            searchBg.setColorFilter(PreferencesHelper.searchbarPrimaryColor, PorterDuff.Mode.MULTIPLY);
                        }
                    });
                }

                findAndHookMethod(Classes.SearchPlate, "onFinishInflate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        ViewGroup searchPlate = (ViewGroup) param.thisObject;
                        Resources resources = searchPlate.getContext().getResources();

                        ImageView logo = (ImageView) searchPlate.findViewById(resources.getIdentifier("launcher_search_button", "id", Common.HOOKED_PACKAGE));
                        ImageView mic = (ImageView) searchPlate.findViewById(resources.getIdentifier("clear_or_voice_button", "id", Common.HOOKED_PACKAGE));

                        switch (PreferencesHelper.searchbarStyle) {
                            case 0:
                                return;
                            case 1:
                                logo.setImageDrawable(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_searchbox_google));
                                Drawable d = Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_mic_dark);

                                if (Utils.getContrastColor(PreferencesHelper.searchbarPrimaryColor) == Color.WHITE) {
                                    logo.setImageDrawable(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_google_small_light));
                                    d = Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_mic_m_white);
                                    setObjectField(mic, Fields.covbFields[2], d);
                                    setObjectField(mic, Fields.covbFields[3], d);
                                }

                                mic.setImageDrawable(d);
                                break;
                            case 2:
                                logo.setImageDrawable(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_google_logo_m));
                                d = Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_mic_m);
                                setObjectField(mic, Fields.covbFields[2], d);
                                setObjectField(mic, Fields.covbFields[3], d);
                                break;
                            case 3:
                                logo.setImageDrawable(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_google_logo_m_monochrome));
                                d = Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.ic_mic_monochrome);
                                setObjectField(mic, Fields.covbFields[2], d);
                                setObjectField(mic, Fields.covbFields[3], d);
                                break;
                        }

                        setObjectField(mic, Fields.covbFields[3], mic.getDrawable());
                    }
                });

                if (PreferencesHelper.searchbarStyle != 3
                        && Utils.getContrastColor(PreferencesHelper.searchbarPrimaryColor) == Color.WHITE) {

                    findAndHookMethod(Classes.SearchPlate, "onFinishInflate", new XC_MethodHook(PRIORITY_HIGHEST) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            ViewGroup searchPlate = (ViewGroup) param.thisObject;
                            Resources resources = searchPlate.getContext().getResources();

                            int id = resources.getIdentifier("search_box", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                EditText simpleSearchText = (EditText) searchPlate.findViewById(id);
                                simpleSearchText.setTextColor(color);
                                simpleSearchText.setHintTextColor(darkerColor);
                            }

                            id = resources.getIdentifier("say_ok_google", "id", Common.HOOKED_PACKAGE);
                            if (id != 0) {
                                ((TextView) searchPlate.findViewById(id)).setTextColor(darkerColor);
                            }

                            if (Common.GNL_VERSION < ObfuscationHelper.GNL_5_3_23) {
                                id = resources.getIdentifier("clear_or_voice_button", "id", Common.HOOKED_PACKAGE);
                                if (id != 0) {
                                    View clearOrVoiceButton = searchPlate.findViewById(id);

                                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_4_7_12
                                            || Common.GNL_VERSION == ObfuscationHelper.GNL_4_7_13) {
                                        callMethod(clearOrVoiceButton, Methods.covbMethods[0]);
                                        callMethod(clearOrVoiceButton, Methods.covbMethods[1]);
                                        callMethod(clearOrVoiceButton, Methods.covbMethods[2]);
                                    }

                                    Paint paint = (Paint) getObjectField(clearOrVoiceButton, Fields.covbFields[0]);
                                    if (paint != null)
                                        paint.setColor(darkerColor);
                                    ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[1])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                    ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[2])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                    ((Drawable) getObjectField(clearOrVoiceButton, Fields.covbFields[3])).setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                                }
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

    private static void blockSearchbarTransitions(LoadPackageParam lpparam) {
        if (Common.GNL_VERSION >= ObfuscationHelper.GNL_5_3_23) {
            // avoid that nasty animation when showing the search bar again
            findAndHookMethod(Classes.TransitionsManager, Methods.tmSetTransitionsEnabled, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
            findAndHookMethod(findClass("com.google.android.apps.gsa.searchplate.HintTextView", lpparam.classLoader), "eC", boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int page = getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage);
                    boolean shouldShow = (page == 0 && PreferencesHelper.autoHideSearchBar) || (PreferencesHelper.searchBarOnDefaultHomescreen && page == (PreferencesHelper.defaultHomescreen - 1));
                    // show the search bar as soon as the page has stopped moving and the GNow overlay is visible
                    if ((Common.IS_KK_TREBUCHET || (Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)) && shouldShow) {
                        param.args[0] = false;
                    }
                }
            });
        } else {
            // avoid that nasty animation when showing the search bar again
            findAndHookMethod(Classes.TransitionsManager, Methods.tmSetTransitionsEnabled, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });
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
