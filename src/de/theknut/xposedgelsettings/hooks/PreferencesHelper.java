package de.theknut.xposedgelsettings.hooks;

import java.util.HashSet;
import java.util.Set;
import android.graphics.Color;
import de.robv.android.xposed.XSharedPreferences;

public class PreferencesHelper {
	public static XSharedPreferences prefs = new XSharedPreferences(Common.PACKAGE_NAME);
	public static boolean hideSearchBar = prefs.getBoolean("hidesearchbar", false);
	public static boolean autoHideSearchBar = prefs.getBoolean("autohidehidesearchbar", false);
	public static boolean hideIconLabelHome = prefs.getBoolean("hideiconhomescreen", false);
	public static boolean hideIconLabelApps = prefs.getBoolean("hideiconappdrawer", false);
	public static boolean changeGridSizeHome = prefs.getBoolean("changegridsizehome", false);
	public static boolean changeGridSizeApps = prefs.getBoolean("changegridsizeapps", false);
	public static boolean iconSettingsSwitchHome = prefs.getBoolean("iconsettingsswitchhome", false);
	public static boolean iconSettingsSwitchApps = prefs.getBoolean("iconsettingsswitchapps", false);
	public static boolean hidePageIndicator = prefs.getBoolean("hidepageindicator", false);
	public static boolean enableRotation = prefs.getBoolean("enablerotation", false);
	public static boolean resizeAllWidgets = prefs.getBoolean("resizeallwidgets", false);
	public static boolean homescreenIconLabelShadow = prefs.getBoolean("homescreeniconlabelshadow", true);
	public static boolean longpressAllAppsButton = prefs.getBoolean("longpressallappsbutton", false);
	public static boolean disableWallpaperScroll = prefs.getBoolean("disablewallpaperscroll", false);
	public static int xCountHomescreen = Integer.parseInt(prefs.getString("xcounthomescreen", "4"));
	public static int yCountHomescreen = Integer.parseInt(prefs.getString("ycounthomescreen", "5"));
	public static int xCountAllApps = Integer.parseInt(prefs.getString("xcountallapps", "4"));
	public static int yCountAllApps = Integer.parseInt(prefs.getString("ycountallapps", "6"));
	public static int hotseatCount = Integer.parseInt(prefs.getString("hotseatcount", "4"));
	public static int iconSize = Integer.parseInt(prefs.getString("iconsize", "100"));
	public static int hotseatIconSize = Integer.parseInt(prefs.getString("hotseaticonsize", "100"));
	public static int iconTextSize = Integer.parseInt(prefs.getString("icontextsize", "100"));
	public static int homescreenIconLabelColor = prefs.getInt("homescreeniconlabelcolor", Color.WHITE);
	public static int appdrawerIconLabelColor = prefs.getInt("appdrawericonlabelcolor", Color.WHITE);
	public static int appdrawerBackgroundColor = prefs.getInt("appdrawerbackgroundcolor", Color.argb(0xA5, 0X00, 0x00, 0x00));
	public static int defaultHomescreen = Integer.parseInt(prefs.getString("defaulthomescreen", "1"));
	public static Set<String> hiddenApps = prefs.getStringSet("hiddenapps", new HashSet<String>());
}