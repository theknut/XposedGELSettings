package de.theknut.xposedgelsettings.hooks.icon;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.Utils;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.FolderHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.hooks.common.CommonHooks;
import de.theknut.xposedgelsettings.hooks.common.XGELSCallback;
import de.theknut.xposedgelsettings.ui.Blur;
import de.theknut.xposedgelsettings.ui.CommonUI;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticIntField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

public class IconHooks extends HooksBaseClass {

    static IconPack iconPack;
    static boolean hasCalendarIcon;

    static BroadcastReceiver autoapplyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pkg = intent.getDataString().replace("package:", "");
            List<String> packages = CommonUI.getIconPacks(context);

            TabHelper.getInstance().updateTabs();

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                if (!packages.contains(pkg)) return;

                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        && PreferencesHelper.iconPackAutoApply) {
                    savePackageName(pkg, context);
                    killLauncher();
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                if (!packages.contains(pkg)) return;
                if (PreferencesHelper.iconPackAutoApply
                        && pkg.equals(PreferencesHelper.prefs.getString("iconpack", ""))) {
                    killLauncher();
                }
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                if (!intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {

                    FolderHelper.getInstance().updateFolders(pkg);

                    if (PreferencesHelper.iconpack.equals(pkg)) {
                        savePackageName(Common.ICONPACK_DEFAULT, context);
                        killLauncher();
                    }
                }
            }
        }

        void savePackageName(String pkg, Context context) {
            Intent i = new Intent(Common.XGELS_ACTION_SAVE_ICONPACK);
            i.putExtra("PACKAGENAME", pkg);
            context.sendBroadcast(i);
        }
    };

    static BroadcastReceiver updateCalendarReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_DATE_CHANGED)
                    || action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

                if (IconPack.getDayOfMonth() != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    iconPack.onDateChanged();
                    checkCalendarApps();
                    updateIcons();
                }
            }
        }
    };

    public static void initAllHooks(LoadPackageParam lpparam) {

        if (PreferencesHelper.iconpack.equals(Common.ICONPACK_DEFAULT)) {
            for (ResolveInfo r : getCalendars()) {
                if (r.activityInfo.metaData != null) {
                    int arrayID = r.activityInfo.metaData.getInt("com.teslacoilsw.launcher.calendarIconArray");
                    if (arrayID != 0) {
                        hasCalendarIcon = true;
                    }
                }
            }
            if (!hasCalendarIcon) {
                return;
            }
        }

        XposedBridge.hookAllConstructors(Classes.IconCache, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!initIconPack(param)) return;
            }
        });

        CommonHooks.LauncherOnStartListeners.add(new XGELSCallback() {
            @Override
            public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                if ((IconPack.getDayOfMonth()) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    iconPack.onDateChanged();
                    checkCalendarApps();
                    updateIcons();
                }
            }
        });

        XC_MethodHook cacheLockedHook = new XC_MethodHook() {

            final int COMPONENTNAME = 0;
            final int LABELCACHE = 2;
            long time;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                time = System.currentTimeMillis();
                if (!initIconPack(param)) return;

                HashMap<Object, String> labelCache = (HashMap<Object, String>) param.args[LABELCACHE];
                ComponentName cmpName = ((ComponentName) param.args[COMPONENTNAME]);
                String appName = cmpName.flattenToString();
                Drawable icon = iconPack.loadIcon(appName);
                if (icon == null && !iconPack.isAppFilterLoaded()) return;

                PackageManager pkgMgr = Common.LAUNCHER_CONTEXT.getPackageManager();

                if (icon == null) {
                    if (!iconPack.shouldThemeMissingIcons()) return;

                    icon = pkgMgr.getActivityInfo(cmpName, 0).loadIcon(pkgMgr);
                    Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);

                    Icon newIcon = new Icon(appName, new BitmapDrawable(iconPack.getResources(), tmpFinalIcon));
                    iconPack.getIcons().add(newIcon);

                    Object cacheEntry = createCacheEntry(labelCache, cmpName, pkgMgr, tmpFinalIcon);
                    param.setResult(cacheEntry);
                    if (DEBUG)
                        log("CacheLocked: Loaded Themed Icon Replacement for " + appName + " took " + (System.currentTimeMillis() - time) + "ms");
                } else {
                    Bitmap replacedIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    Object cacheEntry = createCacheEntry(labelCache, cmpName, pkgMgr, replacedIcon);
                    param.setResult(cacheEntry);
                    if (DEBUG)
                        log("CacheLocked: Loaded Icon Replacement for " + appName + " took " + (System.currentTimeMillis() - time) + "ms");
                }
            }

            private Object createCacheEntry(HashMap<Object, String> labelCache, ComponentName cmpName, PackageManager pkgMgr, Bitmap tmpFinalIcon) {
                Object cacheEntry = newInstance(Classes.CacheEntry);
                setObjectField(cacheEntry, Fields.ceIcon, tmpFinalIcon);
                ActivityInfo info = null;
                try {
                    info = pkgMgr.getActivityInfo(cmpName, 0);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                if (labelCache != null && labelCache.containsKey(cmpName)) {
                    setObjectField(cacheEntry, "title", labelCache.get(cmpName).toString());
                } else {
                    String title = info.loadLabel(pkgMgr).toString();
                    setObjectField(cacheEntry, "title", title);
                    if (labelCache != null) {
                        labelCache.put(cmpName, title);
                    }
                }
                if (getObjectField(cacheEntry, "title") == null) {
                    setObjectField(cacheEntry, "title", info.loadLabel(pkgMgr));
                }
                return cacheEntry;
            }
        };

        if (!Common.IS_PRE_GNL_4) {
            findAndHookMethod(Classes.IconCache, Methods.icCacheLocked, ComponentName.class, Classes.Adb, HashMap.class, Classes.UserHandle, boolean.class, cacheLockedHook);
        } else if (Common.PACKAGE_OBFUSCATED && Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
            findAndHookMethod(Classes.IconCache, Methods.icCacheLocked, ComponentName.class, Classes.Adb, HashMap.class, Classes.UserHandle, cacheLockedHook);
        } else {
            findAndHookMethod(Classes.IconCache, Methods.icCacheLocked, ComponentName.class, ResolveInfo.class, HashMap.class, cacheLockedHook);
        }

        findAndHookMethod(Classes.IconCache, Methods.icGetFullResIcon, Resources.class, Integer.TYPE, new XC_MethodHook() {

            final int RESOURCES = 0;
            final int ICONRESID = 1;
            long time;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                time = System.currentTimeMillis();
                if (!initIconPack(param)) return;
                int resID = (Integer) param.args[ICONRESID];

                if (resID == 0) {
                    return;
                }

                Resources res = (Resources) param.args[RESOURCES];
                String pkg = res.getResourcePackageName(resID);
                if (pkg.equals("android")) return;

                PackageManager pkgMgr = iconPack.getContext().getPackageManager();

                try {
                    // try to get a more precise packagename
                    pkg = pkgMgr.getLaunchIntentForPackage(pkg).getComponent().flattenToString();
                } catch (Exception ex) { }

                Drawable icon = iconPack.loadIcon(pkg);
                if (icon == null && !iconPack.isAppFilterLoaded()) return;

                if (icon == null) {
                    if (!iconPack.shouldThemeMissingIcons()) return;
                    try {
                        icon = pkgMgr.getApplicationInfo(pkg, 0).loadIcon(pkgMgr);
                        Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                        Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);

                        icon = new BitmapDrawable(iconPack.getResources(), tmpFinalIcon);
                        Icon newIcon = new Icon(pkg, icon);
                        iconPack.getIcons().add(newIcon);
                        param.setResult(icon);
                        if (DEBUG) log("Res R: Loaded Themed Icon Replacement for " + pkg + " took " + (System.currentTimeMillis() - time) + "ms");
                    } catch (NameNotFoundException nnfe) {
                        if (DEBUG) log("Res R: Couldn't load Icon Replacement for " + pkg);
                    }
                } else {
                    param.setResult(icon);
                    if (DEBUG) log("Res R: Loaded Icon Replacement for " + pkg + " took " + (System.currentTimeMillis() - time) + "ms");
                }
            }
        });

        findAndHookMethod(Classes.LauncherModel, Methods.lmIsShortcutInfoUpdateable, Classes.ItemInfo, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                for (ResolveInfo r : getCalendars()) {
                    if (r.activityInfo.packageName.equals(((Intent) callMethod(param.args[0], "getIntent")).getComponent().getPackageName())) {
                        if (DEBUG) log(param, "Returning true for " + r.activityInfo.packageName + " instead of " + param.getResult());
                        param.setResult(true);
                        return;
                    }
                }

                if (DEBUG) log(param, "Returned " + param.getResult() + " for " + ((Intent) callMethod(param.args[0], "getIntent")).getComponent().getPackageName());
            }
        });

        findAndHookMethod(Classes.IconCache, Methods.icGetFullResIcon, ActivityInfo.class, new XC_MethodHook() {

            long time;

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                time = System.currentTimeMillis();
                if (!initIconPack(param)) return;

                ActivityInfo ai = ((ActivityInfo) param.args[0]);
                ComponentName app = new ComponentName(ai.packageName, ai.name);
                Drawable icon = iconPack.loadIcon(app.flattenToString());
                if (icon == null && !iconPack.isAppFilterLoaded()) return;

                if (icon == null) {
                    try {
                        PackageManager pkgMgr = Common.LAUNCHER_CONTEXT.getPackageManager();
                        icon = pkgMgr.getActivityIcon(app);
                        Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                        Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);

                        icon = new BitmapDrawable(iconPack.getResources(), tmpFinalIcon);
                        Icon newIcon = new Icon(app.flattenToString(), icon);
                        iconPack.getIcons().add(newIcon);
                        param.setResult(icon);
                        if (DEBUG) log("Res A: Loaded Themed Icon Replacement for " + app.flattenToString() + " took " + (System.currentTimeMillis() - time) + "ms");
                    } catch (NameNotFoundException nnfe) {
                        if (DEBUG) log("Res A: Couldn't load Icon Replacement for " + app.flattenToString());
                    }
                } else {
                    param.setResult(icon);
                    if (DEBUG) log("Res A: Loaded Icon Replacement for " + app.flattenToString() + " took " + (System.currentTimeMillis() - time) + "ms");
                }
            }
        });

        CommonHooks.AddViewToCellLayoutListeners.add(0, new XGELSCallback() {
            @Override
            public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0].getClass().equals(Classes.FolderIcon)) {
                    setFolderIcon((View) param.args[0]);
                }
            }
        });

        CommonHooks.FolderIconDispatchDrawListeners.add(0, new XGELSCallback() {
            Object mFolder = null;
            @Override
            public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                mFolder = null;
                if (null != Utils.getDataByTag(PreferencesHelper.folderIcons, ((View) param.thisObject).getTag())) {
                    mFolder = getObjectField(param.thisObject, Fields.fiFolder);
                    setObjectField(param.thisObject, Fields.fiFolder, null);
                }
            }

            @Override
            public void onAfterHookedMethod(MethodHookParam param) throws Throwable {
                if (mFolder != null) {
                    setObjectField(param.thisObject, Fields.fiFolder, mFolder);
                    mFolder = null;
                }
            }
        });

        if (!PreferencesHelper.noAllAppsButton) {

            for (String selectedIcon : PreferencesHelper.selectedIcons) {
                if (selectedIcon.split("\\|")[0].equals("all_apps_button_icon")) {

                    CommonHooks.AddViewToCellLayoutListeners.add(new XGELSCallback() {

                        final int ITEM_TYPE_ALLAPPS = 5; // Trebuchet

                        @Override
                        public void onBeforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object tag = ((View) param.args[0]).getTag();
                            if (param.args[0] instanceof TextView
                                    && (!getBooleanField(param.args[3], Fields.cllpCanReorder) || (tag != null && getIntField(tag, Fields.iiItemType) == ITEM_TYPE_ALLAPPS))) {
                                if (DEBUG) log(param, "theme all apps button");

                                Drawable icon = iconPack.loadIcon("all_apps_button_icon");
                                if (icon != null) {

                                    Bitmap tmpIcon = CommonUI.drawableToBitmap(icon);
                                    Bitmap iconPressed = Bitmap.createBitmap(tmpIcon.getWidth(), tmpIcon.getHeight(), Bitmap.Config.ARGB_8888);

                                    Canvas c = new Canvas(iconPressed);
                                    Paint p = new Paint();
                                    p.setAlpha(0x80);
                                    c.drawBitmap(tmpIcon, 0, 0, p);

                                    Drawable pressedIcon = new BitmapDrawable(iconPressed);
                                    StateListDrawable states = new StateListDrawable();
                                    states.addState(new int[] {android.R.attr.state_pressed}, pressedIcon);
                                    states.addState(new int[] {android.R.attr.state_focused}, pressedIcon);
                                    states.addState(new int[] { }, icon);

                                    TextView allAppsButton = (TextView) param.args[0];
                                    Rect bounds = allAppsButton.getCompoundDrawables()[1].copyBounds();
                                    states.setBounds(bounds);
                                    allAppsButton.setCompoundDrawables(null, states, null, null);
                                } else {
                                    if (DEBUG) log(param, "Couldn't load icon for all apps button");
                                }
                            }
                        }
                    });
                }
            }
        }

        findAndHookMethod(Classes.ShortcutInfo, Methods.siGetIcon, Classes.IconCache, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Drawable d = Utils.loadIconByTag(iconPack, PreferencesHelper.shortcutIcons, param.thisObject);
                if (d == null) return;
                param.setResult(CommonUI.drawableToBitmap(d));
            }
        });
    }

    public static boolean initIconPack(MethodHookParam param) throws NameNotFoundException {
        if (Common.LAUNCHER_CONTEXT == null) { return false; }

        if (iconPack == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            Common.LAUNCHER_CONTEXT.registerReceiver(autoapplyReceiver, intentFilter);

            iconPack = new IconPack(Common.LAUNCHER_CONTEXT, PreferencesHelper.iconpack);
            if (DEBUG) log("Instantiated " + iconPack.getPackageName());
            if (!PreferencesHelper.iconpack.equals(Common.ICONPACK_DEFAULT)) {
                iconPack.loadAppFilter();
                if (DEBUG) log ("Appfilter loaded");
            }

            iconPack.loadSelectedIcons(PreferencesHelper.selectedIcons);
            checkCalendarApps();
        }

        return true;
    }

    public static void checkCalendarApps() {
        boolean hasThemedCalendarIcon = false;

        for (ResolveInfo r : getCalendars()) {
            if (r.activityInfo.metaData != null) {
                int arrayID = r.activityInfo.metaData.getInt("com.teslacoilsw.launcher.calendarIconArray");
                if (arrayID != 0) {
                    try {
                        for (IconInfo icon : iconPack.getCalendarIcons()) {
                            if (icon.getComponentName().contains(r.activityInfo.packageName)) {
                                hasThemedCalendarIcon = true;
                            }
                        }

                        if (!hasThemedCalendarIcon) {
                            Context ctx = iconPack.getContext().createPackageContext(r.activityInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
                            TypedArray icons = ctx.getResources().obtainTypedArray(arrayID);
                            int iconID = icons.getResourceId(IconPack.getDayOfMonth() - 1, 29);
                            Drawable icon = ctx.getResources().getDrawable(iconID);

                            if (iconPack.shouldThemeMissingIcons()) {
                                Bitmap tmpIcon = Blur.drawableToBitmap(icon);
                                Bitmap finalIcon = iconPack.themeIcon(tmpIcon);
                                icon = new BitmapDrawable(iconPack.getResources(), finalIcon);
                            }

                            iconPack.getIcons().add(new Icon(r.activityInfo.packageName, icon, true));
                            icons.recycle();
                        }

                        hasCalendarIcon = true;
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (hasCalendarIcon) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
            intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
            Common.LAUNCHER_CONTEXT.registerReceiver(updateCalendarReceiver, intentFilter);
            if (DEBUG) log("Has Calendar app");
        }
    }

    public static void setFolderIcon(View folderIcon) {
        ImageView prevBackground = (ImageView) getObjectField(folderIcon, Fields.fiPreviewBackground);
        Drawable icon = Utils.loadIconByTag(iconPack, PreferencesHelper.folderIcons, folderIcon.getTag());
        if (icon == null) {
            return;
        }

        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        icon = new BitmapDrawable(
                Common.LAUNCHER_CONTEXT.getResources(),
                Bitmap.createScaledBitmap(
                        bitmap,
                        getStaticIntField(ObfuscationHelper.Classes.Utilities, Fields.uIconWidth),
                        getStaticIntField(ObfuscationHelper.Classes.Utilities, Fields.uIconHeight),
                        true
                )
        );

        prevBackground.setScaleType(ImageView.ScaleType.CENTER);
        prevBackground.setImageDrawable(icon);
        prevBackground.clearColorFilter();
    }

    static void killLauncher() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 5000);
    }

    @SuppressWarnings({ "rawtypes", "rawtypes", "unchecked" })
    static void updateIcons() {
        long time = System.currentTimeMillis();

        List<Object> appsToUpdate = new ArrayList<Object>();
        for (ResolveInfo r : getCalendars()) {
            if (Common.PACKAGE_OBFUSCATED) {
                Intent i = Common.LAUNCHER_CONTEXT.getPackageManager().getLaunchIntentForPackage(r.activityInfo.packageName);
                appsToUpdate.add(callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppInfo, i));

            } else {
                appsToUpdate.add(newInstance(
                                Classes.AppInfo,
                                iconPack.getContext().getPackageManager(),
                                r,
                                getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache),
                                new HashMap<Object, CharSequence>())
                );
            }
        }

        callMethod(Common.LAUNCHER_INSTANCE, Methods.lBindAppsUpdated, appsToUpdate);
        if (DEBUG) log("updateIcons took " + (System.currentTimeMillis() - time) + "ms");
    }

    public static List<ResolveInfo> getCalendars() {
        if (Common.XGELSCONTEXT == null) return new ArrayList<ResolveInfo>();

        PackageManager packageManager = Common.XGELSCONTEXT.getPackageManager();
        Intent calendarIntent = new Intent(Intent.ACTION_MAIN);
        calendarIntent.addCategory("android.intent.category.LAUNCHER");
        calendarIntent.addCategory("android.intent.category.APP_CALENDAR");
        return packageManager.queryIntentActivities(calendarIntent, PackageManager.GET_META_DATA);
    }
}