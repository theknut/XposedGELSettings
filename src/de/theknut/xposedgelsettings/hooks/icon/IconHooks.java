package de.theknut.xposedgelsettings.hooks.icon;

import android.app.NotificationManager;
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
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Classes;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.ui.Blur;
import de.theknut.xposedgelsettings.ui.CommonUI;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
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
                if (PreferencesHelper.iconpack.equals(pkg) && !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                    savePackageName(Common.ICONPACK_DEFAULT, context);
                    killLauncher();
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

	    if (PreferencesHelper.iconpack == Common.ICONPACK_DEFAULT) {
	        
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

        findAndHookMethod(Classes.Launcher, "onStart", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ((IconPack.getDayOfMonth()) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    iconPack.onDateChanged();
                    checkCalendarApps();
                    updateIcons();
                }
            }
        });
		
		findAndHookMethod(Classes.IconCache, Methods.icCacheLocked, ComponentName.class, ResolveInfo.class, HashMap.class, new XC_MethodHook() {
		    
		    final int COMPONENTNAME = 0;
            final int RESOLVEINFO = 1;
            final int LABELCACHE = 2;
            long time;
		    
		    @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                time = System.currentTimeMillis();
                if (!initIconPack(param)) return;

                HashMap<Object, String> labelCache = (HashMap<Object, String>) param.args[LABELCACHE];
		        ComponentName cmpName = ((ComponentName) param.args[COMPONENTNAME]);
                ResolveInfo info = ((ResolveInfo) param.args[RESOLVEINFO]);
                String appName = cmpName.flattenToString();
                Drawable icon = iconPack.loadIcon(appName);
                if (icon == null && !iconPack.isAppFilterLoaded()) return;

                PackageManager pkgMgr = Common.LAUNCHER_CONTEXT.getPackageManager();

                if (icon == null) {
                    if (!iconPack.shouldThemeMissingIcons()) return;
                    icon = info.loadIcon(pkgMgr);
                    Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);

                    Icon newIcon = new Icon(appName, new BitmapDrawable(iconPack.getResources(), tmpFinalIcon));
                    iconPack.getIcons().add(newIcon);

                    Object cacheEntry = createCacheEntry(labelCache, info, pkgMgr, tmpFinalIcon);
                    param.setResult(cacheEntry);
                    if (DEBUG) log("CacheLocked: Loaded Themed Icon Replacement for " + appName + " took " + (System.currentTimeMillis() - time) + "ms");
                } else {
                    Bitmap replacedIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    Object cacheEntry = createCacheEntry(labelCache, info, pkgMgr, replacedIcon);
                    param.setResult(cacheEntry);
                    if (DEBUG) log("CacheLocked: Loaded Icon Replacement for " + appName + " took " + (System.currentTimeMillis() - time) + "ms");
                }
            }

            private Object createCacheEntry(HashMap<Object, String> labelCache, ResolveInfo info, PackageManager pkgMgr, Bitmap tmpFinalIcon) {
                Object cacheEntry = newInstance(Classes.CacheEntry);
                setObjectField(cacheEntry, Fields.ceIcon, tmpFinalIcon);

                ComponentName key = getComponentNameFromResolveInfo(info);
                if (labelCache != null && labelCache.containsKey(key)) {
                    setObjectField(cacheEntry, Fields.ceTitle, labelCache.get(key).toString());
                } else {
                    String title = info.loadLabel(pkgMgr).toString();
                    setObjectField(cacheEntry, Fields.ceTitle, title);
                    if (labelCache != null) {
                        labelCache.put(key, title);
                    }
                }
                if (getObjectField(cacheEntry, Fields.ceTitle) == null) {
                    setObjectField(cacheEntry, Fields.ceTitle, info.activityInfo.name);
                }
                return cacheEntry;
            }

            private ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
                if (info.activityInfo != null) {
                    return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                } else {
                    return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
                }
            }
        });
		
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
		            String msg = "Some of your homescreen icons couldn't be themed. Please delete the shortcut from the homescreen and add it from the app drawer again.";
		            NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
		            notiStyle.setBigContentTitle("Oh snap!");
		            notiStyle.bigText(msg);
		            
		            Context ctx = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
		            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
		            .setContentTitle("Oh snap!")
		            .setContentText(msg)
		            .setTicker("Something went wrong :-\\")
		            .setAutoCancel(true)
		            .setStyle(notiStyle)
		            .setSmallIcon(R.drawable.ic_launcher);

		            ((NotificationManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE)).notify(null, 0, builder.build());
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

        if (!PreferencesHelper.noAllAppsButton) {

            for (String selectedIcon : PreferencesHelper.selectedIcons) {
                if (selectedIcon.split("\\|")[0].equals("all_apps_button_icon")) {

                    findAndHookMethod(Classes.CellLayout, Methods.celllayoutAddViewToCellLayout, View.class, Integer.TYPE, Integer.TYPE, Classes.CellLayoutLayoutParams, boolean.class, new XC_MethodHook() {

                        final int ITEM_TYPE_ALLAPPS = 5; // Trebuchet

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Object tag = ((View) param.args[0]).getTag();
                            if (param.args[0] instanceof TextView
                                && (!getBooleanField(param.args[3], Fields.celllayoutlayoutparamsCanReorder) || (tag != null && getIntField(tag, Fields.iiItemType) == ITEM_TYPE_ALLAPPS))) {
                                if (DEBUG) log(param, "theme all apps button");

                                Drawable icon = iconPack.loadIcon("all_apps_button_icon");
                                if (icon != null) {
                                    TextView allAppsButton = (TextView) param.args[0];
                                    Rect bounds = allAppsButton.getCompoundDrawables()[1].copyBounds();
                                    icon.setBounds(bounds);
                                    allAppsButton.setCompoundDrawables(null, icon, null, null);
                                } else {
                                    if (DEBUG) log(param, "Couldn't load icon for all apps button");
                                }
                            }
                        }
                    });
                }
            }
        }
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
            if (!PreferencesHelper.iconpack.equals(Common.ICONPACK_DEFAULT)) {
                iconPack.loadAppFilter();
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
            appsToUpdate.add(newInstance(
                    Classes.AppInfo,
                    iconPack.getContext().getPackageManager(),
                    r,
                    getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache),
                    new HashMap<Object, CharSequence>())
            );
        }

        callMethod(Common.LAUNCHER_INSTANCE, Methods.lBindAppsUpdated, appsToUpdate);
        if (DEBUG) log("updateIcons took " + (System.currentTimeMillis() - time) + "ms");
    }

    public static List<ResolveInfo> getCalendars() {
        if (Common.LAUNCHER_CONTEXT == null) return new ArrayList<ResolveInfo>();

        PackageManager packageManager = Common.LAUNCHER_CONTEXT.getPackageManager();
        Intent calendarIntent = new Intent(Intent.ACTION_MAIN);
        calendarIntent.addCategory("android.intent.category.LAUNCHER");
        calendarIntent.addCategory("android.intent.category.APP_CALENDAR");
        return packageManager.queryIntentActivities(calendarIntent, PackageManager.GET_META_DATA);
    }
}