package de.theknut.xposedgelsettings.hooks.notificationbadges;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class NotificationBadgesHooks extends NotificationBadgesHelper {

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (!PreferencesHelper.enableBadges) return;

        XposedBridge.hookAllMethods(Classes.Launcher, "onCreate", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                final Context mContext = (Context) callMethod(Common.LAUNCHER_INSTANCE, "getApplicationContext");

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
                intentFilter.addAction(Common.MISSEDIT_COUNTERS_STATUS);
                intentFilter.addAction(Common.MISSEDIT_CALL_NOTIFICATION);
                intentFilter.addAction(Common.MISSEDIT_SMS_NOTIFICATION);
                intentFilter.addAction(Common.MISSEDIT_APP_NOTIFICATION);
                intentFilter.addAction(Common.MISSEDIT_GMAIL_NOTIFICATION);

                mContext.registerReceiver(notificationReceiver, intentFilter);
            }
        });

        XposedBridge.hookAllMethods(Classes.Launcher, "onDestroy", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Common.LAUNCHER_CONTEXT.unregisterReceiver(notificationReceiver);
            }
        });

        XC_MethodHook requestCountersHook = new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (DEBUG) log(param, "Request Counters");
                requestCounters();
            }
        };

        findAndHookMethod(Classes.Workspace, Methods.wOnDragEnd, requestCountersHook);
        findAndHookMethod(Classes.Launcher, Methods.lOpenFolder, Classes.FolderIcon, requestCountersHook);
        findAndHookMethod(Classes.Launcher, Methods.lFinishBindingItems, boolean.class, requestCountersHook);

        findAndHookMethod(Classes.Workspace, Methods.wOnLauncherTransitionEnd, Classes.Launcher, boolean.class, boolean.class, new XC_MethodHook() {

            int TOWORKSPACE = 2;

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if ((Boolean) param.args[TOWORKSPACE]) {
                    if (DEBUG) log(param, "Transitioning to Workspace - do nothing");
                } else {
                    if (DEBUG) log(param, "Transitioning to All Apps - Request Counters");
                    requestCounters();
                }
            }
        });

        XC_MethodHook drawHook = new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                ComponentName cmp = getComponentName(param.thisObject);
                if (cmp == null) return;

                int idx = findPendingNotification(cmp);
                if (idx != -1) {
                    drawBadge((TextView) param.thisObject, (Canvas) param.args[0], pendingNotifications.get(idx).getCount());
                }
            }

            private void drawBadge(TextView v, Canvas c, int count) {

                if (count != 0) {
                    Drawable d = new Badge(count).getDrawable();
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    c.translate(getTranslationX(v, d), getTranslationY(v, d));
                    d.draw(c);
                }
            }

            private float getTranslationX(TextView v, Drawable d) {
                if (PreferencesHelper.notificationBadgePosition == 0
                        || PreferencesHelper.notificationBadgePosition == 3) {
                    return v.getScrollX()
                            + (v.getWidth() / 2)
                            - (v.getCompoundDrawables()[1].getIntrinsicWidth() / 2);
                }

                return v.getScrollX()
                        + (v.getWidth() / 2)
                        + (v.getCompoundDrawables()[1].getIntrinsicWidth() / 2)
                        - d.getIntrinsicWidth();
            }

            private float getTranslationY(TextView v, Drawable d) {
                if (PreferencesHelper.notificationBadgePosition == 0
                        || PreferencesHelper.notificationBadgePosition == 1) {
                    return v.getScrollY()
                            + v.getPaddingTop();
                }

                return v.getScrollY()
                        + v.getPaddingTop()
                        + v.getCompoundDrawables()[1].getIntrinsicHeight()
                        - d.getIntrinsicHeight();
            }
        };

        if (!PreferencesHelper.hideBadgesFromAppDrawer) {
            findAndHookMethod(Classes.PagedViewIcon, "draw", Canvas.class, drawHook);
        }
        findAndHookMethod(Classes.BubbleTextView, "draw", Canvas.class, drawHook);

        findAndHookMethod(Classes.FolderIcon, "dispatchDraw", Canvas.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<View> items = (ArrayList<View>) callMethod(getObjectField(param.thisObject, Fields.fiFolder), Methods.fGetItemsInReadingOrder);

                int count = 0;
                for (View item : items) {
                    int idx = findPendingNotification(getComponentName(item));
                    if (idx != -1) {
                        count += pendingNotifications.get(idx).getCount();
                    }
                }

                if (count != 0) {
                    View bg = (View) getObjectField(param.thisObject, Fields.fiPreviewBackground);
                    Canvas c = (Canvas) param.args[0];
                    Drawable d = new Badge(count).getDrawable();
                    c.translate(getTranslationX(bg, d), getTranslationY(bg, d));
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    d.draw(c);
                }
            }

            private float getTranslationX(View bg, Drawable d) {
                if (PreferencesHelper.notificationBadgePosition == 0
                        || PreferencesHelper.notificationBadgePosition == 3) {
                    return bg.getX();
                }

                return bg.getX()
                        + bg.getWidth()
                        - d.getIntrinsicWidth();
            }

            private float getTranslationY(View bg, Drawable d) {
                if (PreferencesHelper.notificationBadgePosition == 0
                        || PreferencesHelper.notificationBadgePosition == 1) {
                    return bg.getY()
                            + d.getIntrinsicHeight() / 2;
                }

                return bg.getY()
                        + bg.getHeight()
                        - d.getIntrinsicHeight();
            }
        });

        XposedBridge.hookAllMethods(Classes.Launcher, "onResume", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if (activityManager == null) {
                    activityManager = (ActivityManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
                }

                List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                if (Common.PACKAGE_NAMES.contains(appProcesses.get(0).processName)) {

                    if (DEBUG) log(param, "Request Counters");
                    requestCounters();
                }
            }
        });
    }

    private static ComponentName getComponentName(Object o) {
        try {
            return ((Intent) callMethod(((View) o).getTag(), Methods.siGetIntent)).getComponent();
        } catch (Exception ex) {
            return null;
        } catch (Error ex) {
            return null;
        }
    }

    private static int findPendingNotification(ComponentName cmp) {
        for (int i = 0; i < pendingNotifications.size(); i++) {
            if (pendingNotifications.get(i).equals(cmp)) {
                return i;
            }
        }
        return -1;
    }
}