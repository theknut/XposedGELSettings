package de.theknut.xposedgelsettings.hooks.TestRange;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getIntField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;

import java.util.ArrayList;
import java.util.HashMap;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.theknut.xposedgelsettings.hooks.Common;
import de.theknut.xposedgelsettings.hooks.PreferencesHelper;
import de.theknut.xposedgelsettings.hooks.GoogleSearchBar.DynamicGridLayoutHook;
import de.theknut.xposedgelsettings.hooks.Homescreen.HomescreenHooks;

public class TestRange {
		
	public static void initAllHooks(LoadPackageParam lpparam) {
		
//		final Class<?> DynamicGridClass = findClass(Common.LAUNCHER3 + "Workspace", lpparam.classLoader);
//		XposedBridge.hookAllMethods(DynamicGridClass, "animateBackgroundGradient", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				param.args[0] = 0.0f;
//				param.args[1] = false;
//			}
//		});
		
		
		
		
//		XposedBridge.hookAllMethods(WallpaperCropActivityClass, "suggestWallpaperDimension", new XC_MethodHook() {
//			@Override
//			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//				
//				final SharedPreferences prefs = (SharedPreferences) param.args[1];
//				final WallpaperManager wallpaperManager = (WallpaperManager) param.args[3];
//				final Point defaultWallpaperSize = (Point) callStaticMethod(WallpaperCropActivityClass, "getDefaultWallpaperSize", (Resources) param.args[0], (WindowManager) param.args[2]);
//				
//				new Thread("suggestWallpaperDimension") {
//				    public void run() {
//				        // If we have saved a wallpaper width/height, use that instead
//				        int savedWidth = prefs.getInt("wallpaper.width", defaultWallpaperSize.x);
//				        int savedHeight = prefs.getInt("wallpaper.height", defaultWallpaperSize.y);
//				        wallpaperManager.suggestDesiredDimensions(savedWidth + 300, savedHeight);
//				    }
//				}.start();
//			}
//		});
	}
	public void l(String msg) {
		XposedBridge.log(msg);
	}
}
