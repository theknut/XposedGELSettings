package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.content.Intent;
import android.os.Build;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getIntField;

/**
 * Created by Alexander Schulz on 03.08.2014.
 */
public class SystemBars extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && PreferencesHelper.transparentSystemBars) {
            CommonHooks.LauncherOnResumeListeners.add(new XGELSCallback() {
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {

                    boolean show = ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                            && getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage) == 0);

                    setGradientVisbility(show);
                }
            });

            CommonHooks.LauncherOnPauseListeners.add(new XGELSCallback() {
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                    setGradientVisbility(true);
                }
            });

            if (Common.PACKAGE_OBFUSCATED) {
                CommonHooks.OnNowShowListeners.add(new XGELSCallback() {
                    @Override
                    public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                        setGradientVisbility(true);
                    }
                });
            }

            CommonHooks.PageBeginMovingListeners.add(new XGELSCallback() {
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                    setGradientVisbility(false);
                }
            });

            CommonHooks.PageEndMovingListeners.add(new XGELSCallback() {
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                    boolean show = ((Boolean) callMethod(Common.LAUNCHER_INSTANCE, Methods.lHasCustomContentToLeft)
                            && getIntField(Common.WORKSPACE_INSTANCE, Fields.pvCurrentPage) == 0);

                    setGradientVisbility(show);
                }
            });

            CommonHooks.MoveToDefaultScreenListeners.add(new XGELSCallback() {
                @Override
                public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
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
