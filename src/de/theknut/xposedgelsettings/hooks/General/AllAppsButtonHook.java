package de.theknut.xposedgelsettings.hooks.general;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;
import static de.robv.android.xposed.XposedHelpers.getIntField;

public class AllAppsButtonHook extends HooksBaseClass {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
    // public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)

    final int ITEM_TYPE_ALLAPPS = 5; // Trebuchet
    final int CAN_REORDER = 3;
    final int CHILD = 0;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        Object tag = ((View) param.args[CHILD]).getTag();
        // there is only one button which can't be reordered and thats the app drawer
        if ((param.args[CHILD] instanceof TextView && !getBooleanField(param.args[CAN_REORDER], Fields.cllpCanReorder)
                || (tag != null && getIntField(tag, Fields.iiItemType) == ITEM_TYPE_ALLAPPS))) {
            if (DEBUG) log(param, "Adding XGELS intent to AllAppsButton");

            View allAppsButton = (View) param.args[CHILD];
            final Context context = Common.LAUNCHER_CONTEXT;

            // set on long press listener to do the stuff we want on long press
            allAppsButton.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // start XGELS
                    Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(Common.PACKAGE_NAME);
                    context.startActivity(LaunchIntent);

                    return true;
                }
            });

            if (Common.IS_TREBUCHET && PreferencesHelper.noAllAppsButton) {
                if (DEBUG) log(param, "Removing AllAppsButton");
                param.setResult(false);
            }
        }
    }
}