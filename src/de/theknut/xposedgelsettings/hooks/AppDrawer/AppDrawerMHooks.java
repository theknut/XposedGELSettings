package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.AddTabsAndFoldersM;

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
            findAndHookMethod(Classes.GELClass, "getPredictedApps", XC_MethodReplacement.returnConstant(Collections.emptyList()));
        }

        findAndHookMethod(Classes.AllAppsContainerView, "onUpdateBackgroundAndPaddings", Rect.class, Rect.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ((View) param.thisObject).setBackgroundColor(PreferencesHelper.appdrawerBackgroundColor);

                String[] fields = {"mContainerView", "mRevealView"};
                for (String field : fields) {
                    InsetDrawable background = (InsetDrawable) ((View) getObjectField(param.thisObject, field)).getBackground();
                    background.setColorFilter(PreferencesHelper.appdrawerFolderStyleBackgroundColor, PorterDuff.Mode.MULTIPLY);
                }
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
            XposedBridge.hookAllConstructors(findClass("com.android.launcher3.allapps.FullMergeAlgorithm", lpparam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
        findAndHookMethod(Classes.AlphabeticalAppsList, "onAppsUpdated", new AllAppsListAddMHook());

        AddTabsAndFoldersM.initAllHooks(lpparam);
    }
}