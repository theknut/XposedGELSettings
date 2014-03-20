package de.theknut.xposedgelsettings.hooks.general;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;
import de.robv.android.xposed.XC_MethodHook;

public class SuggestWallpaperDimensionHook extends XC_MethodHook {
	
	final Class<?> WallpaperCropActivityClass;
	
	public SuggestWallpaperDimensionHook(Class clazz) {
		WallpaperCropActivityClass = clazz;
	}
	
	@Override
	protected void afterHookedMethod(MethodHookParam param) throws Throwable {
		
		final SharedPreferences prefs = (SharedPreferences) param.args[1];
		final WallpaperManager wallpaperManager = (WallpaperManager) param.args[3];
		final Point defaultWallpaperSize = (Point) callStaticMethod(WallpaperCropActivityClass, "getDefaultWallpaperSize", (Resources) param.args[0], (WindowManager) param.args[2]);
		
		new Thread("suggestWallpaperDimension") {
		    public void run() {
		        // If we have saved a wallpaper width/height, use that instead
		        int savedWidth = prefs.getInt("wallpaper.width", defaultWallpaperSize.x);
		        int savedHeight = prefs.getInt("wallpaper.height", defaultWallpaperSize.y);
		        wallpaperManager.suggestDesiredDimensions(savedWidth + 150, savedHeight);
		    }
		}.start();
	}
}
