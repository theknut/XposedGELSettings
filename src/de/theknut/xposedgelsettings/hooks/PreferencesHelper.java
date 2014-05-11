package de.theknut.xposedgelsettings.hooks;

import java.util.HashSet;
import java.util.Set;
import android.graphics.Color;
import de.robv.android.xposed.XSharedPreferences;

public class PreferencesHelper {
	public static XSharedPreferences prefs = new XSharedPreferences(Common.PACKAGE_NAME);
	public static boolean Debug = prefs.getBoolean("debug", false);
	public static boolean hideSearchBar = prefs.getBoolean("hidesearchbar", false);
	public static boolean autoHideSearchBar = prefs.getBoolean("autohidehidesearchbar", false);
	public static boolean hideIconLabelHome = prefs.getBoolean("hideiconhomescreen", false);
	public static boolean hideIconLabelApps = prefs.getBoolean("hideiconappdrawer", false);
	public static boolean changeGridSizeHome = prefs.getBoolean("changegridsizehome", false);
	public static boolean changeGridSizeApps = prefs.getBoolean("changegridsizeapps", false);
	public static boolean iconSettingsSwitchHome = prefs.getBoolean("iconsettingsswitchhome", false);
	public static boolean iconSettingsSwitchApps = prefs.getBoolean("iconsettingsswitchapps", false);
	public static boolean appdockSettingsSwitch = prefs.getBoolean("appdocksettingsswitch", false);
	public static boolean hidePageIndicator = prefs.getBoolean("hidepageindicator", false);
	public static boolean enableRotation = prefs.getBoolean("enablerotation", false);
	public static boolean resizeAllWidgets = prefs.getBoolean("resizeallwidgets", false);
	public static boolean homescreenIconLabelShadow = prefs.getBoolean("homescreeniconlabelshadow", true);
	public static boolean appdrawerIconLabelShadow = prefs.getBoolean("appdrawericonlabelshadow", true);
	public static boolean longpressAllAppsButton = prefs.getBoolean("longpressallappsbutton", false);
	public static boolean disableWallpaperScroll = prefs.getBoolean("disablewallpaperscroll", false);
	public static boolean hideAppDock = prefs.getBoolean("hide_appdock", false);
	public static boolean autoHideAppDock = prefs.getBoolean("autohideappdock", false);
	public static boolean lockHomescreen = prefs.getBoolean("lockhomescreen", false);
	public static boolean continuousScroll = prefs.getBoolean("continuousscroll", false);
	public static boolean continuousScrollWithAppDrawer = prefs.getBoolean("continuousscrollwithappdrawer", false);
	public static boolean closeAppdrawerAfterAppStarted = prefs.getBoolean("closeappdrawerafterappstarted", false);
	public static boolean noAllAppsButton = prefs.getBoolean("noallappsbutton", false);
	
	public static boolean hideClock = prefs.getBoolean("hideclock", false);
	public static boolean dynamicHomebutton = prefs.getBoolean("dynamichomebutton", false);
	public static boolean dynamicBackbutton = prefs.getBoolean("dynamicbackbutton", false);
	public static boolean dynamicRecentsbutton = prefs.getBoolean("dynamicrecentsbutton", false);
	public static boolean dynamicIconBackbutton = prefs.getBoolean("changeicondynamicbackbutton", false);
	public static boolean dynamicIconHomebutton = prefs.getBoolean("changeicondynamichomebutton", false);
	public static boolean dynamicIconRecentsbutton = prefs.getBoolean("changeicondynamicrecentsbutton", false);
	public static boolean dynamicAnimateIconHomebutton = prefs.getBoolean("animatedynamichomebutton", false);
	public static boolean dynamicAnimateIconBackbutton = prefs.getBoolean("animatedynamicbackbutton", false);
	public static boolean dynamicAnimateIconRecentsbutton = prefs.getBoolean("dynamicanimateiconrecentsbutton", false);
	public static boolean dynamicBackButtonOnEveryScreen = prefs.getBoolean("dynamicbackbuttononeveryscreen", false);
	
	public static boolean homescreenFolderSwitch = prefs.getBoolean("homescreenfolderswitch", false);
	public static boolean gesture_appdrawer = prefs.getBoolean("gesture_appdrawer", false);
	public static boolean appdrawerRememberLastPosition = prefs.getBoolean("appdrawerrememberlastposition", false);
	public static int xCountHomescreen = Integer.parseInt(prefs.getString("xcounthomescreen", "4"));
	public static int yCountHomescreen = Integer.parseInt(prefs.getString("ycounthomescreen", "5"));
	public static int xCountAllApps = Integer.parseInt(prefs.getString("xcountallapps", "4"));
	public static int yCountAllApps = Integer.parseInt(prefs.getString("ycountallapps", "6"));
	public static int appDockCount = Integer.parseInt(prefs.getString("appdockcount", "5"));
	public static int iconSize = Integer.parseInt(prefs.getString("iconsize", "100"));
	public static int appdockIconSize = Integer.parseInt(prefs.getString("appdockiconsize", "100"));
	public static int iconTextSize = Integer.parseInt(prefs.getString("icontextsize", "100"));
	public static int homescreenIconLabelColor = prefs.getInt("homescreeniconlabelcolor", Color.WHITE);
	public static int appdrawerIconLabelColor = prefs.getInt("appdrawericonlabelcolor", Color.WHITE);
	public static int appdrawerBackgroundColor = prefs.getInt("appdrawerbackgroundcolor", Color.argb(0xA5, 0x00, 0x00, 0x00));
	public static int appDockBackgroundColor = prefs.getInt("appdockbackgroundcolor", Color.argb(0x00, 0xFF, 0xFF, 0xFF));
	public static int homescreenFolderColor = prefs.getInt("homescreenfoldercolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
	public static int homescreenFolderAppTextColor = prefs.getInt("homescreenfolderapptextcolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
	public static int homescreenFolderNameTextColor = prefs.getInt("homescreenfoldernametextcolor", Color.argb(0xFF, 0x77, 0x77, 0x77));
	public static int homescreenFolderPreviewColor = prefs.getInt("homescreenfolderpreviewcolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
	public static int defaultHomescreen = Integer.parseInt(prefs.getString("defaulthomescreen", "-1"));
	public static int workspaceRect = Integer.parseInt(prefs.getString("workspacerect", "1"));
	
	public static String gesture_one_up_left = prefs.getString("gesture_one_up_left", "NONE");
	public static String gesture_one_up_middle = prefs.getString("gesture_one_up_middle", "NONE");
	public static String gesture_one_up_right = prefs.getString("gesture_one_up_right", "NONE");
	public static String gesture_one_down_left = prefs.getString("gesture_one_down_left", "NONE");
	public static String gesture_one_down_middle = prefs.getString("gesture_one_down_middle", "NONE");
	public static String gesture_one_down_right = prefs.getString("gesture_one_down_right", "NONE");
	public static String gesture_double_tap = prefs.getString("gesture_double_tap", "NONE");
	public static boolean gesture_double_tap_only_on_wallpaper = prefs.getBoolean("gesture_double_tap_only_on_wallpaper", false);
	
	public static Set<String> hiddenApps = prefs.getStringSet("hiddenapps", new HashSet<String>());
	public static Set<String> hiddenWidgets = prefs.getStringSet("hiddenwidgets", new HashSet<String>());
	
	public static String notificationDialerApp = prefs.getString("notificationbadge_dialer_launch", "");
	public static String notificationSMSApp = prefs.getString("notificationbadge_sms_launch", "");
	public static boolean enableBadges = prefs.getBoolean("enablenotificationbadges", false);
	public static int notificationBadgeFrameSize = Integer.parseInt(prefs.getString("notificationbadgeframesize", "0"));
	public static int notificationBadgeTextSize = Integer.parseInt(prefs.getString("notificationbadgetextsize", "10"));
	public static int notificationBadgeCornerRadius = Integer.parseInt(prefs.getString("notificationbadgecornerradius", "5"));
	public static int notificationBadgeLeftRightPadding = Integer.parseInt(prefs.getString("notificationbadgeleftrightpadding", "5"));
	public static int notificationBadgeTopBottomPadding = Integer.parseInt(prefs.getString("notificationbadgetopbottompadding", "2"));
	public static int notificationBadgeBackgroundColor = prefs.getInt("notificationbadgebackgroundcolor", Color.argb(0xA0, 0xD4, 0x49, 0x37));
	public static int notificationBadgeTextColor = prefs.getInt("notificationbadgetextcolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
	public static int notificationBadgeFrameColor = prefs.getInt("notificationbadgeframecolor", Color.argb(0xFF, 0xFF, 0xFF, 0xFF));
}