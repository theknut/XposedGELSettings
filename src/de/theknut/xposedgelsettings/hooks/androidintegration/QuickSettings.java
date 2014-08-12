package de.theknut.xposedgelsettings.hooks.androidintegration;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;

import static de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;

/**
 * Created by Alexander Schulz on 05.08.2014.
 */
public class QuickSettings extends HooksBaseClass {

    static Context mContext;
    static Resources XGELSResources;
    static final String LOCKDESKTOPTILE_KEY = "lockdesktoptile";

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) return;

        if (PreferencesHelper.quicksettingsLockDesktop) {
            try {
                final Class<?> QuickSettingsBasicTileClass = findClass("com.android.systemui.statusbar.phone.QuickSettingsBasicTile", lpparam.classLoader);

                findAndHookMethod("com.android.systemui.statusbar.phone.QuickSettings", lpparam.classLoader, "setupQuickSettings", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        mContext = (Context) getObjectField(param.thisObject, "mContext");
                        XGELSResources = mContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
                        ViewGroup mContainerView = (ViewGroup) getObjectField(param.thisObject, "mContainerView");
                        final FrameLayout lockDesktopTile = (FrameLayout) newInstance(QuickSettingsBasicTileClass, mContext);

                        lockDesktopTile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String text = XGELSResources.getString(
                                        !PreferencesHelper.lockHomescreen
                                                ? R.string.quicksettings_desktop_locked
                                                : R.string.quicksettings_desktop_unlocked
                                );
                                Drawable image = XGELSResources.getDrawable(
                                        !PreferencesHelper.lockHomescreen
                                                ? R.drawable.ic_qs_desktop_locked
                                                : R.drawable.ic_qs_desktop_unlocked
                                );
                                callMethod(lockDesktopTile, "setImageDrawable", image);
                                callMethod(lockDesktopTile, "setText", text);
                                Utils.saveToSettings(mContext, "lockhomescreen", !PreferencesHelper.lockHomescreen);
                            }
                        });

                        mContainerView.addView(lockDesktopTile);
                        setAdditionalInstanceField(getObjectField(param.thisObject, "mModel"), LOCKDESKTOPTILE_KEY, lockDesktopTile);
                    }
                });

                findAndHookMethod("com.android.systemui.statusbar.phone.QuickSettingsModel", lpparam.classLoader, "updateResources", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mContext = (Context) getObjectField(param.thisObject, "mContext");
                        XGELSResources = mContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
                        FrameLayout lockDesktopTile = (FrameLayout) getAdditionalInstanceField(param.thisObject, LOCKDESKTOPTILE_KEY);

                        Drawable image = XGELSResources.getDrawable(
                                PreferencesHelper.lockHomescreen
                                        ? R.drawable.ic_qs_desktop_locked
                                        : R.drawable.ic_qs_desktop_unlocked
                        );
                        String text = XGELSResources.getString(
                                PreferencesHelper.lockHomescreen
                                        ? R.string.quicksettings_desktop_locked
                                        : R.string.quicksettings_desktop_unlocked
                        );

                        callMethod(lockDesktopTile, "setImageDrawable", image);
                        callMethod(lockDesktopTile, "setText", text);
                    }
                });
            } catch (ClassNotFoundError cnfe) {
                // CM -.-
                final Class<?> QuickSettingsBasicTileClass = findClass("com.android.systemui.quicksettings.QuickSettingsTile", lpparam.classLoader);

                findAndHookMethod("com.android.systemui.statusbar.phone.QuickSettingsController", lpparam.classLoader, "loadTiles", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        mContext = (Context) getObjectField(param.thisObject, "mContext");
                        XGELSResources = mContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
                        ViewGroup mContainerView = (ViewGroup) getObjectField(param.thisObject, "mContainerView");
                        ArrayList mQuickSettingsTiles = (ArrayList) getObjectField(param.thisObject, "mQuickSettingsTiles");

                        final Object lockDesktopTile = newInstance(QuickSettingsBasicTileClass, mContext, param.thisObject);
                        callMethod(lockDesktopTile, "setupQuickSettingsTile", LayoutInflater.from(mContext), mContainerView);

                        FrameLayout mTile = (FrameLayout) getObjectField(lockDesktopTile, "mTile");
                        mTile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String text = XGELSResources.getString(
                                        !PreferencesHelper.lockHomescreen
                                                ? R.string.quicksettings_desktop_locked
                                                : R.string.quicksettings_desktop_unlocked
                                );
                                Drawable drawable = XGELSResources.getDrawable(
                                        !PreferencesHelper.lockHomescreen
                                                ? R.drawable.ic_qs_desktop_locked
                                                : R.drawable.ic_qs_desktop_unlocked
                                );

                                FrameLayout mTile = (FrameLayout) getObjectField(lockDesktopTile, "mTile");
                                TextView tv = (TextView) mTile.findViewById(mTile.getResources().getIdentifier("text", "id", mTile.getContext().getPackageName()));
                                if (tv != null) {
                                    tv.setText(text);
                                }
                                ImageView image = (ImageView) mTile.findViewById(mTile.getResources().getIdentifier("image", "id", mTile.getContext().getPackageName()));
                                if (image != null) {
                                    image.setImageDrawable(drawable);
                                }
                                Utils.saveToSettings(mContext, "lockhomescreen", !PreferencesHelper.lockHomescreen);
                            }
                        });

                        mQuickSettingsTiles.add(lockDesktopTile);

                        setAdditionalInstanceField(param.thisObject, LOCKDESKTOPTILE_KEY, lockDesktopTile);
                    }
                });

                findAndHookMethod("com.android.systemui.statusbar.phone.QuickSettingsController", lpparam.classLoader, "updateResources", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        mContext = (Context) getObjectField(param.thisObject, "mContext");
                        XGELSResources = mContext.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY).getResources();
                        Object lockDesktopTile = getAdditionalInstanceField(param.thisObject, LOCKDESKTOPTILE_KEY);

                        Drawable drawable = XGELSResources.getDrawable(
                                PreferencesHelper.lockHomescreen
                                        ? R.drawable.ic_qs_desktop_locked
                                        : R.drawable.ic_qs_desktop_unlocked
                        );
                        String text = XGELSResources.getString(
                                PreferencesHelper.lockHomescreen
                                        ? R.string.quicksettings_desktop_locked
                                        : R.string.quicksettings_desktop_unlocked
                        );

                        FrameLayout mTile = (FrameLayout) getObjectField(lockDesktopTile, "mTile");
                        TextView tv = (TextView) mTile.findViewById(mTile.getResources().getIdentifier("text", "id", mTile.getContext().getPackageName()));
                        if (tv != null) {
                            tv.setText(text);
                        }
                        ImageView image = (ImageView) mTile.findViewById(mTile.getResources().getIdentifier("image", "id", mTile.getContext().getPackageName()));
                        if (image != null) {
                            image.setImageDrawable(drawable);
                        }
                    }
                });
            }
        }
    }
}