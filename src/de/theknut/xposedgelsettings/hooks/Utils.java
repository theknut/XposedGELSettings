package de.theknut.xposedgelsettings.hooks;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;

/**
 * Created by Alexander Schulz on 04.08.2014.
 */
public class Utils extends HooksBaseClass {

    public static boolean isIntersecting(View item) {
        long id = getLongField(item.getTag(), Fields.iiID);
        ViewGroup shortcutAndWidgetsContainer = (ViewGroup) item.getParent();
        for (int i = 0; i < shortcutAndWidgetsContainer.getChildCount(); i++) {
            Rect myViewRect = new Rect();
            Rect otherViewRect1 = new Rect();
            View child = shortcutAndWidgetsContainer.getChildAt(i);

            item.getHitRect(myViewRect);
            child.getHitRect(otherViewRect1);

            if (Rect.intersects(myViewRect, otherViewRect1) && getLongField(child.getTag(), Fields.iiID) != id) {
                return true;
            }
        }

        return false;
    }

    public static boolean should(View item) {
        long id = getLongField(item.getTag(), Fields.iiID);
        ViewGroup shortcutAndWidgetsContainer = (ViewGroup) item.getParent();
        for (int i = 0; i < shortcutAndWidgetsContainer.getChildCount(); i++) {
            Rect myViewRect = new Rect();
            Rect otherViewRect1 = new Rect();
            View child = shortcutAndWidgetsContainer.getChildAt(i);

            item.getHitRect(myViewRect);
            child.getHitRect(otherViewRect1);

            if (Rect.intersects(myViewRect, otherViewRect1) && getLongField(child.getTag(), Fields.iiID) != id) {
                return true;
            }
        }

        return false;
    }

    public static void startActivity(Intent intent) {
        if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
            Resources res = Common.LAUNCHER_CONTEXT.getResources();
            int task_open_enter = res.getIdentifier("task_open_enter", "anim", Common.GEL_PACKAGE);
            int no_anim = res.getIdentifier("no_anim", "anim", Common.GEL_PACKAGE);
            callMethod(Common.LAUNCHER_INSTANCE, "startActivity", intent, ActivityOptions.makeCustomAnimation(Common.LAUNCHER_CONTEXT, task_open_enter, no_anim).toBundle());
        } else {
            callMethod(Common.LAUNCHER_INSTANCE, "startActivity", intent);
        }
    }

    public static void showPremiumOnly() {
        try {
            Context XGELSContext = Common.LAUNCHER_CONTEXT.createPackageContext(Common.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Toast.makeText(Common.LAUNCHER_CONTEXT, XGELSContext.getResources().getString(R.string.toast_donate_only), Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveToSettings(Context context, String key, Object setting) {
        saveToSettings(context, key, setting, false);
    }

    public static void saveToSettings(Context context, String key, Object setting, boolean restartLauncher) {
        Intent saveIntent = new Intent(Common.XGELS_ACTION_SAVE_SETTING);
        saveIntent.putExtra("key", key);
        saveIntent.putExtra("restart", restartLauncher);

        if (setting instanceof Boolean) {
            saveIntent.putExtra("type", "boolean");
            saveIntent.putExtra(key, (Boolean) setting);
        } else if (setting instanceof ArrayList) {
            saveIntent.putExtra("type", "arraylist");
            saveIntent.putStringArrayListExtra(key, (ArrayList<String>) setting);
        } else if (setting instanceof HashSet) {
            saveIntent.putExtra("type", "arraylist");
            saveIntent.putStringArrayListExtra(key, new ArrayList<String>((HashSet) setting));
        }
        context.sendBroadcast(saveIntent);
    }
}
