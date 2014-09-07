package de.theknut.xposedgelsettings.hooks.general;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Iterator;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

public class OnPackagesUpdatedHook extends HooksBaseClass {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/AppsCustomizePagedView.java#432
    // public void onPackagesUpdated(ArrayList<Object> widgetsAndShortcuts)

    @SuppressWarnings("unchecked")
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        if (DEBUG) log(param, "Hide Widgets");

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
