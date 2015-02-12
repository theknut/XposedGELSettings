package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;

import java.util.LinkedHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

/**
 * Created by Alexander Schulz on 05.08.2014.
 */
public class QuickSettingsL extends HooksBaseClass {

    static Context mContext;
    static Resources XGELSResources;
    static final String LOCKDESKTOPTILE_KEY = "lockdesktoptile";
    static Object QSTileHostInstance;

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || !PreferencesHelper.quicksettingsLockDesktop)
            return;

        try {
            // dirty, don't look at this...
            final Class<?> QSTile = findClass("com.android.systemui.qs.tiles.RotationLockTile", lpparam.classLoader);
            final Class<?> QSTileHost = findClass("com.android.systemui.statusbar.phone.QSTileHost", lpparam.classLoader);

            XposedBridge.hookAllConstructors(QSTileHost, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    QSTileHostInstance = param.thisObject;
                    mContext = (Context) param.args[0];
                    XGELSResources = mContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
                }
            });

            findAndHookMethod(QSTileHost, "createTile", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (param.args[0].equals(LOCKDESKTOPTILE_KEY)) {
                        Object lockDesktopTile = newInstance(QSTile, param.thisObject);
                        setAdditionalInstanceField(lockDesktopTile, LOCKDESKTOPTILE_KEY, true);
                        param.setResult(lockDesktopTile);
                    }
                }
            });

            findAndHookMethod(QSTileHost, "recreateTiles", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    LinkedHashMap mTiles = (LinkedHashMap) getObjectField(param.thisObject, "mTiles");
                    if (!mTiles.keySet().contains(LOCKDESKTOPTILE_KEY)) {
                        Object lockDesktopTile = newInstance(QSTile, param.thisObject);
                        setAdditionalInstanceField(lockDesktopTile, LOCKDESKTOPTILE_KEY, true);
                        mTiles.put(LOCKDESKTOPTILE_KEY, lockDesktopTile);
                    }
                }
            });

            XposedBridge.hookAllMethods(QSTile, "handleClick", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (getAdditionalInstanceField(param.thisObject, LOCKDESKTOPTILE_KEY) == null) {
                        return;
                    }
                    Utils.saveToSettings(mContext, "lockhomescreen", !PreferencesHelper.lockHomescreen);
                    mContext.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                    param.setResult(null);
                }
            });

            XposedBridge.hookAllMethods(QSTile, "handleUpdateState", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (getAdditionalInstanceField(param.thisObject, LOCKDESKTOPTILE_KEY) == null) {
                        return;
                    }

                    setObjectField(param.args[0], "visible", PreferencesHelper.quicksettingsLockDesktop);
                    setObjectField(param.args[0], "value", PreferencesHelper.lockHomescreen);
                    setObjectField(param.args[0], "label", XGELSResources.getString(
                            PreferencesHelper.lockHomescreen
                                    ? R.string.quicksettings_desktop_locked
                                    : R.string.quicksettings_desktop_unlocked));
                    setObjectField(param.args[0], "contentDescription", getObjectField(param.args[0], "label"));
                    setObjectField(param.args[0], "icon", XGELSResources.getDrawable(PreferencesHelper.lockHomescreen
                            ? R.drawable.ic_qs_desktop_locked
                            : R.drawable.ic_qs_desktop_unlocked_l));

                    param.setResult(null);
                }
            });
        } catch (Error cnfe) {
            log("That didn't work " + cnfe);
        } catch (Exception cnfe) {
            log("That didn't work " + cnfe);
        }
    }
}