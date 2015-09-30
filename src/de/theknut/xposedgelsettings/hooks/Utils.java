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
import android.graphics.Color;
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
import java.util.Random;
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

    static Random rand = new Random();
    static String[] colors = new String[] {
            "#e84e40", // red_400
            "#e51c23", // red_500
            "#dd191d", // red_600
            "#d01716", // red_700
            "#c41411", // red_800
            "#b0120a", // red_900
            "#5c6bc0", // indigo_400
            "#3f51b5", // indigo_500
            "#3949ab", // indigo_600
            "#303f9f", // indigo_700
            "#283593", // indigo_800
            "#1a237e", // indigo_900
            "#3d5afe", // indigo_A400
            "#304ffe", // indigo_A700
            "#5677fc", // blue_500
            "#4e6cef", // blue_600
            "#455ede", // blue_700
            "#3b50ce", // blue_800
            "#2a36b1", // blue_900
            "#4d73ff", // blue_A400
            "#4d69ff", // blue_A700
            "#039be5", // light_blue_600
            "#0288d1", // light_blue_700
            "#0277bd", // light_blue_800
            "#01579b", // light_blue_900
            "#00b0ff", // light_blue_A400
            "#0091ea", // light_blue_A700
            "#00acc1", // cyan_600
            "#0097a7", // cyan_700
            "#00838f", // cyan_800
            "#006064", // cyan_900
            "#26a69a", // teal_400
            "#009688", // teal_500
            "#00897b", // teal_600
            "#00796b", // teal_700
            "#00695c", // teal_800
            "#004d40", // teal_900
            "#2baf2b", // green_400
            "#259b24", // green_500
            "#0a8f08", // green_600
            "#0a7e07", // green_700
            "#056f00", // green_800
            "#12c700", // green_A700
            "#558b2f", // light_green_800
            "#33691e", // light_green_900
            "#827717", // lime_900
            "#e65100", // orange_900
            "#f4511e", // deep_orange_600
            "#e64a19", // deep_orange_700
            "#d84315", // deep_orange_800
            "#bf360c", // deep_orange_900
            "#ff3d00", // deep_orange_A400
            "#dd2c00", // deep_orange_A700
            "#8d6e63", // brown_400
            "#795548", // brown_500
            "#6d4c41", // brown_600
            "#5d4037", // brown_700
            "#4e342e", // brown_800
            "#3e2723", // brown_900
            "#757575", // grey_600
            "#616161", // grey_700
            "#424242", // grey_800
            "#212121", // grey_900
            "#607d8b", // blue_grey_500
            "#546e7a", // blue_grey_600
            "#455a64", // blue_grey_700
            "#37474f", // blue_grey_800
            "#263238"  // blue_grey_900
    };

    // http://stackoverflow.com/a/1855903/809277
    public static int getContrastColor(int color) {
        int d;
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - ( 0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (a < 0.5)
            d = 0; // bright colors - black font
        else
            d = 255; // dark colors - white font

        return  Color.argb(255, d, d, d);
    }

    public static int getRandomColor() {
        return Color.parseColor(colors[rand.nextInt(colors.length - 1)]);
    }

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
        if (Common.GNL_PACKAGE_INFO.versionCode >= ObfuscationHelper.GNL_3_5_14) {
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
        for (Object appInfo : Common.ALL_APPS) {
            try {
                if (intent.getComponent().equals(((Intent) callMethod(appInfo, "getIntent")).getComponent())) {
                    return appInfo;
                }
            } catch (NoSuchMethodError nsme) { }
        }

        if (Common.PACKAGE_OBFUSCATED) {
            return callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppDragInfo, intent);
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
            return callMethod(callMethod(Common.LAUNCHER_INSTANCE, Methods.lCreateAppDragInfo, intent), Methods.aiMakeShortcut);
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
            if (name[0].equals(String.valueOf(id))) {
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

    public static float dpToPxExact(int dp) {
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
