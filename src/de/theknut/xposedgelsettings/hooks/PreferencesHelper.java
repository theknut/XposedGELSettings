package de.theknut.xposedgelsettings.hooks;

import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import de.theknut.xposedgelsettings.Common;

public class PreferencesHelper {
	public static XSharedPreferences prefs = new XSharedPreferences(Common.PACKAGE_NAME);
	public static boolean hideSearchBar = prefs.getBoolean("hidesearchbar", false);
	public static boolean autoHideSearchBar = prefs.getBoolean("autohidehidesearchbar", false);
	public static boolean hideIconLabelHome = prefs.getBoolean("hideiconhomescreen", false);
	public static boolean hideIconLabelApps = prefs.getBoolean("hideiconappdrawer", false);
	public static boolean changeGridSize = prefs.getBoolean("changegridsize", false);
	public static boolean changeHotseatCount = prefs.getBoolean("changehotseatcount", false);
	public static int xCountHomescreen = Integer.parseInt(prefs.getString("xcounthomescreen", "4"));
	public static int yCountHomescreen = Integer.parseInt(prefs.getString("ycounthomescreen", "5"));
	public static int hotseatCount = Integer.parseInt(prefs.getString("hotseatcount", "4"));
	public static Set<String> hiddenApps = prefs.getStringSet("hiddenapps", new HashSet<String>());
}