package de.theknut.xposedgelsettings.hooks.icon;

import android.app.NotificationManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static de.robv.android.xposed.XposedHelpers.*;

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
                
                if ((IconPack.getDayOfMonth()) != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    iconPack.onDateChanged();
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
		
		findAndHookMethod(Classes.IconCache, Methods.icCacheLocked, ComponentName.class, ResolveInfo.class, HashMap.class, new XC_MethodHook() {
		    
		    final int COMPONENTNAME = 0;		    
		    
		    @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		        if (!initIconPack(param)) return;
		        
		        ComponentName cmpname = ((ComponentName) param.args[COMPONENTNAME]);
                String appName = cmpname.flattenToString();
                Drawable icon = iconPack.loadIcon(appName);
                if (icon == null && !iconPack.isAppFilterLoaded()) return;
                
                if (icon == null) {
                    if (!iconPack.shouldThemeMissingIcons()) return;
                    PackageManager pkgMgr = Common.LAUNCHER_CONTEXT.getPackageManager();
                    icon = pkgMgr.getApplicationInfo(cmpname.getPackageName(), 0).loadIcon(pkgMgr);
                    Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);
                    
                    Icon newIcon = new Icon(appName, new BitmapDrawable(iconPack.getResources(), tmpFinalIcon));
                    iconPack.getIcons().add(newIcon);
                    setObjectField(param.getResult(), Fields.icIcon, tmpFinalIcon);
                    if (DEBUG) log("Couldn't load Icon Replacement for " + appName);
                } else {
                    Bitmap replacedIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                    setObjectField(param.getResult(), Fields.icIcon, replacedIcon);
                    if (DEBUG) log("Loaded Icon Replacement for " + appName);
                }                
            }
        });

        findAndHookMethod(Classes.Launcher, "l", ArrayList.class, new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList f = (ArrayList) param.args[0];

                for(Object d : f) {
                    log("Jo " + d);
                }
            }
        });
		
		findAndHookMethod(Classes.IconCache, Methods.icGetFullResIcon, Resources.class, Integer.TYPE, new XC_MethodHook() {
            
		    final int RESOURCES = 0;
		    final int ICONRESID = 1;
		    
		    @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                
                Drawable icon = iconPack.loadIcon(pkg);
                if (icon == null && !iconPack.isAppFilterLoaded()) return;

                if (icon == null) {
                    if (!iconPack.shouldThemeMissingIcons()) return;
                    try {
                        PackageManager pkgMgr = Common.LAUNCHER_CONTEXT.getPackageManager();
                        icon = pkgMgr.getApplicationInfo(pkg, 0).loadIcon(pkgMgr);
                        Bitmap tmpIcon = (Bitmap) callStaticMethod(Classes.Utilities, Methods.uCreateIconBitmap, icon, iconPack.getContext());
                        Bitmap tmpFinalIcon = iconPack.themeIcon(tmpIcon);
                        
                        icon = new BitmapDrawable(iconPack.getResources(), tmpFinalIcon);
                        Icon newIcon = new Icon(pkg, icon);
                        iconPack.getIcons().add(newIcon);
                        param.setResult(icon);
                        if (DEBUG) log("Res: Loaded Icon Replacement for " + pkg);
                    } catch (NameNotFoundException nnfe) {
                        if (DEBUG) log("Res: Couldn't load Icon Replacement for " + pkg);
                    }                    
                } else {
                    param.setResult(icon);
                    if (DEBUG) log("Res: Loaded Icon Replacement for " + pkg);
                }
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

            iconPack = new IconPack(Common.LAUNCHER_CONTEXT, PreferencesHelper.iconpack, getIntField(param.thisObject, Fields.icIconDensity));
            if (!PreferencesHelper.iconpack.equals(Common.ICONPACK_DEFAULT)) {
                iconPack.loadAppFilter();
            }

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
                    getObjectField(Common.LAUNCHER_INSTANCE, "rF"),
                    new HashMap<Object, CharSequence>())
            );
        }

        callMethod(Common.LAUNCHER_INSTANCE, "l", appsToUpdate);
        if (DEBUG) log("updateIcons took " + (System.currentTimeMillis() - time) + "ms");
    }

    public static List<ResolveInfo> getCalendars() {
        PackageManager packageManager = iconPack.getContext().getPackageManager();
        Intent calendarIntent = new Intent(Intent.ACTION_MAIN);
        calendarIntent.addCategory("android.intent.category.LAUNCHER");
        calendarIntent.addCategory("android.intent.category.APP_CALENDAR");
        return packageManager.queryIntentActivities(calendarIntent, PackageManager.GET_META_DATA);
    }
}