package de.theknut.xposedgelsettings.hooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;

// stuff we need quite often
public final class Common {
	
	// our package name
	public static final String PACKAGE_NAME = Common.class.getPackage().getName().replace(".hooks", "");
	
	// XGELS intent
	public static final String XGELS_INTENT = PACKAGE_NAME + ".Intent";
	public static final String XGELS_ACTION = "XGELSACTION";
	
	// the name of the settings file
	public static final String PREFERENCES_NAME = Common.PACKAGE_NAME + "_preferences";
	
	// the package we are currently hooked to
	public static String HOOKED_PACKAGE;
	
	// instances
	public static Object LAUNCHER_INSTANCE;
	public static Object GEL_INSTANCE;
	public static Object DEVICE_PROFILE_INSTANCE;
	public static Object NOW_OVERLAY_INSTANCE;
	public static Object PAGED_VIEW_INSTANCE;
	public static Object LAUNCHER_APP_STATE_INSTANCE;
	public static Object CONTENT_TYPE;
	public static Object WORKSPACE_INSTANCE;
	public static Object APP_DRAWER_INSTANCE;
	
	public static Context LAUNCHER_CONTEXT;
		
	// saved messures of the search bar
	public static int SEARCH_BAR_SPACE_HEIGHT;
	public static int SEARCH_BAR_SPACE_WIDTH;
	public static int HOTSEAT_BAR_HEIGHT;
	
	// saved measures for the icon sizes
	public static float NEW_ICON_SIZE;
	public static float NEW_HOTSEAT_ICON_SIZE;
	
	// class names to hook to	
	public static String ALL_APPS_LIST;
	public static String ITEM_INFO;
	public static String CELL_LAYOUT;
	public static String SEARCH_DROP_TARGET_BAR;
	public static String DYNAMIC_GRID;
	public static String LAUNCHER;
	public static String PAGED_VIEW;
	public static String PAGED_VIEW_WITH_DRAGGABLE_ITEMS;
	public static String APPS_CUSTOMIZE_PAGED_VIEW;
	public static String APPS_CUSTOMIZE_LAYOUT;
	public static String PAGED_VIEW_ICON;
	public static String DEVICE_PROFILE;
	public static String WORKSPACE;
	public static String APPS_CUSTOMIZE_TAB_HOST;
	public static String WALLPAPER_CROP_ACTIVITY;
	
	public static String GEL = "com.google.android.launcher.GEL";
	public static String NOW_OVERLAY = "com.google.android.sidekick.shared.client.NowOverlay";
	public static String SEARCH_OVERLAY_IMPL = "com.google.android.search.gel.SearchOverlayImpl";
	public static String SEARCH_PLATE = "com.google.android.search.shared.ui.SearchPlate";

	public static String GEL_PACKAGE = "com.google.android.googlequicksearchbox";
	public static String TREBUCHET_PACKAGE = "com.cyanogenmod.trebuchet";
	
	public static String LAUNCHER3 = "com.android.launcher3.";
	
	public static boolean APPDOCK_HIDDEN = true;	
	public static boolean IS_INIT = false;
	public static boolean HOOKS_AFTER_WORKSPACE_LOADED_INITIALIZED = false;
	public static boolean OVERSCROLLED = false;
	public static boolean LAUNCHER_PAUSED = false;
	public static boolean SCREEN_OFF = false;
	
	// all launchers we support (hopefully :-S)
	public static final List<String> PACKAGE_NAMES = new ArrayList<String>(Arrays.asList("com.android.launcher2", "com.android.launcher3", Common.GEL_PACKAGE, Common.TREBUCHET_PACKAGE));
	
	public static void initClassNames(String launcherName) {
		//log("Common.initClassNames: converting to " + launcherName);
		
		String launcherPackage = "com.android." + launcherName + ".";
		ALL_APPS_LIST = launcherPackage + "AllAppsList";
		ITEM_INFO = launcherPackage + "ItemInfo";
		CELL_LAYOUT = launcherPackage + "CellLayout";
		SEARCH_DROP_TARGET_BAR = launcherPackage + "SearchDropTargetBar";
		DYNAMIC_GRID = launcherPackage + "DeviceProfile";
		LAUNCHER = launcherPackage + "Launcher";
		PAGED_VIEW = launcherPackage + "PagedView";
		PAGED_VIEW_WITH_DRAGGABLE_ITEMS = launcherPackage + "PagedViewWithDraggableItems";
		APPS_CUSTOMIZE_PAGED_VIEW = launcherPackage + "AppsCustomizePagedView";
		APPS_CUSTOMIZE_LAYOUT = launcherPackage + "AppsCustomizeLayout";
		PAGED_VIEW_ICON = launcherPackage + "PagedViewIcon";
		DEVICE_PROFILE = launcherPackage + "DeviceProfile";
		WORKSPACE = launcherPackage + "Workspace";
		APPS_CUSTOMIZE_TAB_HOST = launcherPackage + "AppsCustomizeTabHost";
		WALLPAPER_CROP_ACTIVITY = launcherPackage + "WallpaperCropActivity";
	}
}