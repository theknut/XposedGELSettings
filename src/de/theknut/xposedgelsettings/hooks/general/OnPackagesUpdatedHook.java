package de.theknut.xposedgelsettings.hooks.general;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Iterator;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.ui.SaveActivity;

public class OnPackagesUpdatedHook extends HooksBaseClass {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#432
    // public void onPackagesUpdated(ArrayList<Object> widgetsAndShortcuts)

    @SuppressWarnings("unchecked")
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (DEBUG) log(param, "Hide Widgets");

        if (PreferencesHelper.hiddenWidgets.size() != 0
                && PreferencesHelper.hiddenWidgets.iterator().next().contains("#")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setComponent(new ComponentName(Common.PACKAGE_NAME, SaveActivity.class.getName()));
            intent.putExtra("mode", SaveActivity.CONVERT_APPSWIDGETS);
            Common.LAUNCHER_CONTEXT.startActivity(intent);
        }

        ArrayList<Object> widgets = (ArrayList<Object>) param.args[0];
        Iterator it = widgets.iterator();
        while(it.hasNext()) {
            Object widget = it.next();
            String cmp;

            if (widget instanceof AppWidgetProviderInfo) {
                cmp = ((AppWidgetProviderInfo) widget).provider.flattenToString();
            } else if (widget instanceof ResolveInfo) {
                ResolveInfo item = (ResolveInfo) widget;
                cmp = new ComponentName(item.activityInfo.packageName, item.activityInfo.loadLabel(Common.LAUNCHER_CONTEXT.getPackageManager()).toString()).flattenToString();
            } else {
                continue;
            }

            if (PreferencesHelper.hiddenWidgets.contains(cmp)) {
                it.remove();
            }
        }

        param.args[0] = widgets;
    }
}
