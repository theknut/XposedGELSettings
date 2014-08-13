package de.theknut.xposedgelsettings.hooks.general;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.HooksBaseClass;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;

import static de.robv.android.xposed.XposedHelpers.getBooleanField;

public class AllAppsButtonHook extends HooksBaseClass {

    // http://androidxref.com/4.4.2_r1/xref/packages/apps/Launcher3/src/com/android/launcher3/CellLayout.java#604
    // public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells)

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

        // there is only one button which can't be reordered and thats the app drawer
        if (param.args[0] instanceof TextView && !getBooleanField(param.args[3], Fields.cllpCanReorder)) {
            if (DEBUG) log(param, "Adding XGELS intent to AllAppsButton");

            View allAppsButton = (View) param.args[0];
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
        }
    }
}