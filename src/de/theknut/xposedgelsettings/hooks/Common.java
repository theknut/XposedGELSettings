package de.theknut.xposedgelsettings.hooks;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// stuff we need quite often
public final class Common {

    // our package name
    public static final String PACKAGE_NAME = Common.class.getPackage().getName().replace(".hooks", "");

    // XGELS intent
    public static final String XGELS_INTENT = PACKAGE_NAME + ".Intent";
    public static final String XGELS_ACTION = "XGELSACTION";
    public static final String XGELS_ACTION_EXTRA = "XGELSACTION_EXTRA";
    public static final String XGELS_ACTION_NAVBAR = "NAVIGATION_BAR";
    public static final String XGELS_ACTION_OTHER = "OTHER";
    public static final String XGELS_ACTION_APP_REQUEST = "APP_REQUEST";
    public static final String XGELS_ACTION_SAVE_ICONPACK = XGELS_INTENT + ".SAVE_ICONPACK";
    public static final String XGELS_ACTION_SAVE_LAYER_POSITIONS = XGELS_INTENT + ".SAVE_LAYER_POSITIONS";
    public static final String XGELS_ACTION_SAVE_SETTING = XGELS_INTENT + ".SAVE_SETTING";
    public static final String XGELS_ACTION_RELOAD_SETTINGS = XGELS_INTENT + ".RELOAD_SETTINGS";
    public static final String XGELS_ACTION_RESTART_LAUNCHER = XGELS_INTENT + ".RESTART_LAUNCHER";
    public static final String XGELS_ACTION_UPDATE_FOLDER_ITEMS = XGELS_INTENT + ".UPDATE_FOLDER_ITEMS";
    public static final String XGELS_ACTION_MODIFY_TAB = XGELS_INTENT + ".MODIFY_TAB";
    public static final String XGELS_ACTION_MODIFY_FOLDER = XGELS_INTENT + ".MODIFY_FOLDER";
    public static final String XGELS_ACTION_UPDATE_ICON = XGELS_INTENT + ".UPDATE_ICON";
    public static final String XGELS_ACTION_CONVERT_SETTING = XGELS_INTENT + ".CONVERT_SETTING";
    public static final String XGELS_ACTION_RECEIVER_RUNNING = XGELS_INTENT + ".RECEIVER_RUNNING";

    // MissedIt intent
    public static final String MISSEDIT_INTENT = "net.igecelabs.android.MissedIt.";
    public static final String MISSEDIT_ACTION_INTENT = MISSEDIT_INTENT + "action.";
    public static final String MISSEDIT_REQUESET_COUNTERS = MISSEDIT_ACTION_INTENT + "REQUEST_COUNTERS";
    public static final String MISSEDIT_COUNTERS_STATUS = MISSEDIT_INTENT + "COUNTERS_STATUS";
    public static final String MISSEDIT_CALL_NOTIFICATION = MISSEDIT_INTENT + "CALL_NOTIFICATION";
    public static final String MISSEDIT_SMS_NOTIFICATION = MISSEDIT_INTENT + "SMS_NOTIFICATION";
    public static final String MISSEDIT_APP_NOTIFICATION = MISSEDIT_INTENT + "APP_NOTIFICATION";
    public static final String MISSEDIT_GMAIL_NOTIFICATION = MISSEDIT_INTENT + "GMAIL_NOTIFICATION";

    public static final String ICONPACK_DEFAULT = "DEFAULT";

    // the name of the settings file
    public static final String PREFERENCES_NAME = Common.PACKAGE_NAME + "_preferences";

    // the package we are currently hooked to
    public static String HOOKED_PACKAGE;

    // Instances
    public static Activity LAUNCHER_INSTANCE;
    public static Object WORKSPACE_INSTANCE;
    public static Object HOTSEAT_INSTANCE;
    public static View APP_DRAWER_INSTANCE;

    public static Context LAUNCHER_CONTEXT;
    public static Context XGELSCONTEXT;

    // saved messures of the search bar
    public static int SEARCH_BAR_SPACE_HEIGHT = -1;
    public static int HOTSEAT_BAR_HEIGHT;

    public static String GEL_PACKAGE = "com.google.android.googlequicksearchbox";
    public static String TREBUCHET_PACKAGE = "com.cyanogenmod.trebuchet";

    public static boolean APPDOCK_HIDDEN = true;
    public static boolean OVERSCROLLED = false;
    public static boolean IS_DRAGGING = false;
    public static boolean MOVED_TO_DEFAULTHOMESCREEN = false;
    public static int APPDRAWER_LAST_PAGE_POSITION = 0;
    public static int APPDRAWER_LAST_TAB_POSITION = 0;

    public static boolean PACKAGE_OBFUSCATED;
    public static int GNL_VERSION;

    public static final List<String> PACKAGE_NAMES = new ArrayList<String>(Arrays.asList("com.android.launcher3", Common.GEL_PACKAGE, Common.TREBUCHET_PACKAGE));

    public static boolean FOLDER_GESTURE_ACTIVE;
    public static boolean L_VALUE = true;
    public static boolean ON_MEASURE;
    public static View CURRENT_CONTEXT_MENU_ITEM;
    public static boolean IS_TREBUCHET;
    public static boolean IS_PRE_GNL_4 = true;
    public static boolean APP_DRAWER_PAGE_SWITCHED;
    public static int ORIENTATION;
    public static int ALL_APPS_X_COUNT_VERTICAL;
    public static int ALL_APPS_Y_COUNT_VERTICAL;
    public static int ALL_APPS_X_COUNT_HORIZONTAL;
    public static int ALL_APPS_Y_COUNT_HORIZONTAL;
    public static int APP_DRAWER_ICON_SIZE = -1;
    public static Object DEVICE_PROFIL;
    public static ArrayList ALL_APPS;
}