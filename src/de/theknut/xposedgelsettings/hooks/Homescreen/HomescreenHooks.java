package de.theknut.xposedgelsettings.hooks.homescreen;

import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.general.MoveToDefaultScreenHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getFloatField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setBooleanField;
import static de.robv.android.xposed.XposedHelpers.setIntField;

public class HomescreenHooks extends HooksBaseClass {

    public static void initAllHooks(LoadPackageParam lpparam) {

        // change the default homescreen
        CommonHooks.MoveToDefaultScreenListeners.add(new MoveToDefaultScreenHook());

        // modify homescreen grid
        CommonHooks.DeviceProfileConstructorListeners.add(new DeviceProfileConstructorHook());

        if (!Common.IS_PRE_GNL_4) {
            findAndHookMethod(Classes.Folder, "onFinishInflate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    ((View) param.thisObject).setBackground(Common.XGELSCONTEXT.getResources().getDrawable(R.drawable.quantum_panel));
                }
            });
        }

        if (PreferencesHelper.iconSettingsSwitchHome || PreferencesHelper.homescreenFolderSwitch || PreferencesHelper.appdockSettingsSwitch) {
            // changing the appearence of the icons on the homescreen
            CommonHooks.AddViewToCellLayoutListeners.add(new AddViewToCellLayoutHook());
        }

        if (PreferencesHelper.continuousScroll) {

            // over scroll to app drawer or first page
            findAndHookMethod(Classes.Workspace, Methods.pvOverScroll, float.class, new OverScrollWorkspaceHook());
        }

        if (PreferencesHelper.appdockSettingsSwitch || PreferencesHelper.changeGridSizeHome) {

            // hide the app dock
            CommonHooks.GetWorkspacePaddingListeners.add(new GetWorkspacePaddingHook());

            if (PreferencesHelper.appdockSettingsSwitch) {
                XposedBridge.hookAllConstructors(Classes.Hotseat, new HotseatConstructorHook());

                if (PreferencesHelper.appdockShowLabels) {
                    findAndHookMethod(Classes.CellLayout, Methods.clSetIsHotseat, boolean.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            setBooleanField(param.thisObject, Fields.clIsHotseat, false);
                            setBooleanField(getObjectField(param.thisObject, Fields.clShortcutsAndWidgets), Fields.sawIsHotseat, false);
                        }
                    });
                }

                findAndHookMethod(Classes.Hotseat, "onFinishInflate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (Common.LAUNCHER_CONTEXT.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            FrameLayout hotseat = (FrameLayout) param.thisObject;

                            int paddingLeftRight = hotseat.getPaddingLeft();
                            int paddingBottom = hotseat.getPaddingBottom();
                            paddingLeftRight = paddingLeftRight == 0 && PreferencesHelper.appdockRect != 1 ? 12 : 0;
                            hotseat.setPadding(
                                    paddingLeftRight * PreferencesHelper.appdockRect,
                                    hotseat.getPaddingTop(),
                                    paddingLeftRight * PreferencesHelper.appdockRect,
                                    paddingBottom
                            );
                        }
                    }
                });
            }
        }

        if (Common.IS_TREBUCHET) {
            // move to default homescreen after workspace has finished loading
            XposedBridge.hookAllMethods(Classes.Launcher, "onFinishBindingItems", new FinishBindingItemsHook());
        }
        else {
            // move to default homescreen after workspace has finished loading
            findAndHookMethod(Classes.Launcher, Methods.lFinishBindingItems, boolean.class, new FinishBindingItemsHook());
        }

        if (PreferencesHelper.smartFolderMode != 0) {
            findAndHookMethod(Classes.FolderIcon, "onTouchEvent", MotionEvent.class, new SmartFolderHook());
        }

        if (PreferencesHelper.unlimitedFolderSize) {
            XposedBridge.hookAllConstructors(Classes.Folder, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    setIntField(param.thisObject, Fields.fMaxCountX, Math.round(getFloatField(Common.DEVICE_PROFIL, Fields.dpNumCols)));
                    setIntField(param.thisObject, Fields.fMaxCountY, Integer.MAX_VALUE);
                    setIntField(param.thisObject, Fields.fMaxNumItems, Integer.MAX_VALUE);
                }
            });

            // very dirty hack :(
            if (!Common.IS_TREBUCHET) {
                findAndHookMethod(Classes.Folder, "onMeasure", Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Common.ON_MEASURE = true;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Common.ON_MEASURE = false;
                    }
                });

                findAndHookMethod(Classes.LauncherAppState, Methods.lasIsDisableAllApps, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        return Common.ON_MEASURE;
                    }
                });
            }
        }
    }
}