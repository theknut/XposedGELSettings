package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedBridge.hookAllMethods;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;

/**
 * Created by Alexander Schulz on 03.08.2014.
 */
public class SystemBars extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (PreferencesHelper.transparentSystemBars) {
            findAndHookMethod(Classes.Launcher, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    boolean show = ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                            && getIntField(Common.WORKSPACE_INSTANCE, Fields.wCurrentPage) == 0);

                    setGradientVisbility(show);
                }
            });

            findAndHookMethod(Classes.Launcher, "onPause", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    setGradientVisbility(true);
                }
            });

            if (Common.PACKAGE_OBFUSCATED) {
                findAndHookMethod(Classes.NowOverlay, Methods.noOnShow, boolean.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        setGradientVisbility(true);
                    }
                });
            }

            hookAllMethods(Classes.PagedView, Methods.pvPageBeginMoving, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                            && getIntField(Common.WORKSPACE_INSTANCE, Fields.wCurrentPage) == 0) {
                        setGradientVisbility(false);
                    }
                }
            });

            hookAllMethods(Classes.PagedView, Methods.pvPageEndMoving, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    boolean show = ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                            && getIntField(Common.WORKSPACE_INSTANCE, Fields.wCurrentPage) == 0);

                    setGradientVisbility(show);
                }
            });

            findAndHookMethod(Classes.Workspace, Methods.workspaceMoveToDefaultScreen, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    setGradientVisbility(false);
                }
            });
        }
    }

    private static void setGradientVisbility(boolean show) {
        Intent myIntent = new Intent(Common.XGELS_INTENT);
        myIntent.putExtra(Common.XGELS_ACTION_EXTRA, Common.XGELS_ACTION_OTHER);
        myIntent.putExtra(Common.XGELS_ACTION, "SHADOWS");
        myIntent.putExtra("SHOW", show);
        Common.LAUNCHER_CONTEXT.sendBroadcast(myIntent);
    }
}
