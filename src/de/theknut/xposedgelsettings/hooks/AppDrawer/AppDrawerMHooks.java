package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AddTabsAndFoldersM;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelperM;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class AppDrawerMHooks extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        // save an instance of the app drawer object
        XposedBridge.hookAllConstructors(Classes.AllAppsContainerView, new AppsCustomizePagedViewConstructorHook());

        XposedBridge.hookAllMethods(Classes.AllAppsContainerView, "setSearchBarController", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ViewGroup mSearchBarContainerView = (ViewGroup) getObjectField(param.thisObject, "mSearchBarView");
                mSearchBarContainerView.getBackground().setColorFilter(PreferencesHelper.searchbarPrimaryColor, PorterDuff.Mode.MULTIPLY);
            }
        });

        if (PreferencesHelper.noAllAppsPredictions) {
            findAndHookMethod(Classes.LauncherCallbacksImpl, "getPredictedApps", XC_MethodReplacement.returnConstant(Collections.emptyList()));
        }

        if (PreferencesHelper.appdrawerRememberLastPosition) {
            findAndHookMethod(Classes.AllAppsRecyclerView, "scrollToTop", XC_MethodReplacement.DO_NOTHING);
        }

        if (Utils.getContrastColor(PreferencesHelper.searchbarPrimaryColor) == Color.WHITE) {
            findAndHookMethod(findClass("com.google.android.launcher.i", lpparam.classLoader), "getView", ViewGroup.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View mSearchBarView = (View) param.getResult();
                    int id = mSearchBarView.getResources().getIdentifier("clear_button", "id", Common.HOOKED_PACKAGE);
                    FrameLayout clearButton = (FrameLayout) mSearchBarView.findViewById(id);

                    id = mSearchBarView.getResources().getIdentifier("quantum_ic_clear_white_24", "drawable", Common.HOOKED_PACKAGE);
                    ((ImageView) clearButton.getChildAt(0)).setImageDrawable(mSearchBarView.getResources().getDrawable(id));
                }
            });
        }

        findAndHookMethod(Classes.AllAppsContainerView, "onUpdateBackgroundAndPaddings", Rect.class, Rect.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TabHelperM.getInstance().setBackgroundColor(TabHelperM.getInstance().getTabById(Tab.APPS_ID));
            }
        });

        if (PreferencesHelper.closeAppdrawerAfterAppStarted) {
            findAndHookMethod(Classes.Launcher, "onClick", View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, "isAppsViewVisible")) {
                        ((ArrayList) getObjectField(Common.LAUNCHER_INSTANCE, "mOnResumeCallbacks")).add(0, new Runnable() {
                            @Override
                            public void run() {
                                callMethod(Common.LAUNCHER_INSTANCE, Methods.lShowWorkspace, -1, false, null);
                            }
                        });
                    }
                }
            });
        }

        if (PreferencesHelper.changeGridSizeApps) {
            XposedBridge.hookAllConstructors(Classes.FullMergeAlgorithm, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (PreferencesHelper.xCountAllAppsHorizontal == -1
                            || PreferencesHelper.xCountAllAppsVertical == -1)
                        return;

                    if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setObjectField(Common.APP_DRAWER_INSTANCE, "mNumAppsPerRow", PreferencesHelper.xCountAllAppsHorizontal);
                        setObjectField(Common.APP_DRAWER_INSTANCE, "mNumPredictedAppsPerRow", PreferencesHelper.xCountAllAppsHorizontal);
                    } else {
                        setObjectField(Common.APP_DRAWER_INSTANCE, "mNumAppsPerRow", PreferencesHelper.xCountAllAppsVertical);
                        setObjectField(Common.APP_DRAWER_INSTANCE, "mNumPredictedAppsPerRow", PreferencesHelper.xCountAllAppsVertical);
                    }
                }
            });
        }

        // hiding apps from the app drawer
        findAndHookMethod(Classes.AlphabeticalAppsList, "updateApps", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                HashMap mComponentToAppMap = (HashMap) getObjectField(param.thisObject, "mComponentToAppMap");
                if (mComponentToAppMap.isEmpty()) {
                    Common.ALL_APPS = new ArrayList((ArrayList) param.args[0]);
                } else {
                    Common.ALL_APPS.addAll(new ArrayList((ArrayList) param.args[0]));
                }

                TabHelper.getInstance().updateTabs();
            }
        });

        findAndHookMethod(Classes.AlphabeticalAppsList, "removeApps", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Common.ALL_APPS.removeAll(new ArrayList((ArrayList) param.args[0]));
                TabHelper.getInstance().updateTabs();
            }
        });

        AddTabsAndFoldersM.initAllHooks(lpparam);
    }
}