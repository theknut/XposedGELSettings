package de.theknut.xposedgelsettings.hooks;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Fields;
import de.theknut.xposedgelsettings.hooks.ObfuscationHelper.Methods;
import de.theknut.xposedgelsettings.hooks.icon.IconPack;
import de.theknut.xposedgelsettings.ui.CommonUI;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.getLongField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

/**
 * Created by Alexander Schulz on 04.08.2014.
 */
public class Utils {

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
        Toast.makeText(
                Common.LAUNCHER_CONTEXT,
                Common.XGELSCONTEXT.getResources().getString(R.string.toast_donate_only),
                Toast.LENGTH_LONG
        ).show();
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

    public static Object createAppInfo(String pkg) {
        return createAppInfo(Common.LAUNCHER_CONTEXT.getPackageManager().getLaunchIntentForPackage(pkg));
    }

    public static Object createAppInfo(ComponentName cmp) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(cmp);

        return createAppInfo(intent);
    }

    public static Object createAppInfo(Intent intent) {
        if (Common.PACKAGE_OBFUSCATED) {
            return callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppInfo, intent);
        }

        PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
        return newInstance(
                ObfuscationHelper.Classes.AppInfo,
                pm,
                pm.resolveActivity(intent, 0),
                getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache),
                new HashMap<Object, CharSequence>());
    }

    public static Object createShortcutInfo(String componentName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(ComponentName.unflattenFromString(componentName));

        if (Common.PACKAGE_OBFUSCATED) {
            return callMethod(callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppInfo, intent), Methods.aiMakeShortcut);
        }

        PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
        Object appInfo = newInstance(
                ObfuscationHelper.Classes.AppInfo,
                pm,
                pm.resolveActivity(intent, 0),
                getObjectField(Common.LAUNCHER_INSTANCE, Fields.lIconCache),
                new HashMap<Object, CharSequence>()
        );
        return callMethod(appInfo, Methods.aiMakeShortcut);
    }

    public static List<ResolveInfo> getAllApps() {
        PackageManager pm = Common.LAUNCHER_CONTEXT.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));

        return apps;
    }

    public static String[] getDataByTag(Set<String> preference, Object tag) {
        if (tag == null) return null;

        long id = getLongField(tag, Fields.iiID);
        Iterator it = preference.iterator();
        while (it.hasNext()) {
            String[] name = it.next().toString().split("\\|");
            if (name[0].equals("" + id)) {
                return name;
            }
        }

        return null;
    }

    public static Drawable loadIconByTag(IconPack iconPack, Set<String> preference, Object tag) {
        String[] data = Utils.getDataByTag(preference, tag);
        if (data == null) return null;
        return iconPack.loadSingleIconFromIconPack(data[1], null, data[2], false);
    }

    public static int dpToPx(int dp) {
        return dpToPx(dp, Resources.getSystem().getDisplayMetrics());
    }

    public static double dpToPxExact(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static int dpToPx(int dp, DisplayMetrics displayMetrics) {
        return Math.round(dp * displayMetrics.density);
    }

    public static void setDrawableSelector(ImageView view) {

        Drawable icon = view.getDrawable();
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

        view.setImageDrawable(states);
    }
}
