package de.theknut.xposedgelsettings.hooks.appdrawer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.Tab;
import de.theknut.xposedgelsettings.hooks.appdrawer.tabsandfolders.TabHelper;
import de.theknut.xposedgelsettings.ui.CommonUI;
import de.theknut.xposedgelsettings.ui.SaveActivity;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

public final class AllAppsListAddHook extends XC_MethodHook {
	
	// http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AllAppsList.java#65
	// public void add(AppInfo info)

    List<String> packages = new ArrayList<String>();
    boolean init;
    final int APPINFOLIST = 0;
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (PreferencesHelper.iconPackHide && !init && Common.LAUNCHER_CONTEXT != null) {
            init = true;
            packages = CommonUI.getIconPacks(Common.LAUNCHER_CONTEXT);
        }

        ArrayList<String> appsToHide = new ArrayList<String>(PreferencesHelper.hiddenApps);
        appsToHide.addAll(TabHelper.getInstance().getAppsToHide());

        if (PreferencesHelper.hiddenApps.size() != 0 && PreferencesHelper.hiddenApps.iterator().next().contains("#")
                || PreferencesHelper.hiddenWidgets.size() != 0 && PreferencesHelper.hiddenWidgets.iterator().next().contains("#")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setComponent(new ComponentName(Common.PACKAGE_NAME, SaveActivity.class.getName()));
            intent.putExtra("mode", SaveActivity.CONVERT_APPSWIDGETS);
            Common.LAUNCHER_CONTEXT.startActivity(intent);
        }

        ArrayList appInfoList = (ArrayList) param.args[APPINFOLIST];
        Iterator it = appInfoList.iterator();

        while(it.hasNext()) {
            Object appInfo = it.next();
            ComponentName componentName = (ComponentName) getObjectField(appInfo, Fields.aiComponentName);

            if (appsToHide.contains(componentName.flattenToString())
                    || packages.contains(componentName.getPackageName())) {
                // remove it from the allAppsList if it is in our list
                it.remove();
            }
        }
	}

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Tab tab = TabHelper.getInstance().getTabById(Tab.APPS_ID);
        Collections.sort((ArrayList) param.args[APPINFOLIST], tab.getSortComparator());
    }

    public void makeNotification() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setComponent(new ComponentName(Common.PACKAGE_NAME, SaveActivity.class.getName()));
        intent.putExtra("mode", SaveActivity.CONVERT_APPSWIDGETS);
        PendingIntent pInstallTab = PendingIntent.getActivity(Common.LAUNCHER_CONTEXT, 0xB00B5, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String msg = "Your current list of hidden widgets/apps needs to be converted into a new format. Touch here to start converting your settings! Otherwise widgets/apps won't be hidden!";
        NotificationCompat.BigTextStyle notiStyle = new NotificationCompat.BigTextStyle();
        notiStyle.setBigContentTitle("XGELS Information");
        notiStyle.bigText(msg);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Common.LAUNCHER_CONTEXT)
                .setContentTitle("Information")
                .setContentText(msg)
                .setTicker("XGELS Information")
                .setContentIntent(pInstallTab)
                .setAutoCancel(true)
                .setStyle(notiStyle)
                .setSmallIcon(android.R.drawable.ic_dialog_alert);

        ((NotificationManager) Common.LAUNCHER_CONTEXT.getSystemService(Context.NOTIFICATION_SERVICE)).notify(null, 0, builder.build());
    }
}